from direct.showbase.ShowBase import ShowBase
from direct.task import Task
import thread
import sys
sys.path.append('broker')
from Broker import *
from BrokerCallBack import *
from Message import *



class MsgHandler(BrokerCallBack):
    def setClient(self, agclient):
        self.client = agclient
    
    def receive(self,request, response):
        if request.getString("mid") == "init":
            self.client.loadTerrain()
            self.client.createWorld(request)
            
            self.client.myPlayerID = request.getObjectID()
            print self.client.myPlayerID
            self.client.myPlayer = self.client.objectDic[self.client.myPlayerID]
            print "player object type is : " + self.client.myPlayer.oType
            self.client.taskMgr.add(self.client.cameraTask, "cameraTrackingTask")
        elif request.getString("mid") == "move":
            self.client.moveObject(request)
        elif request.getString("mid") == "plUpdate":
            pass
        return 0
    
        
class Object:
    pass

class AGClient(ShowBase):
    def __init__(self):
        ShowBase.__init__(self)
        self.objectDic = {}
        self.keyboardLock = thread.allocate_lock()
        self.initControls()
        
        self.b = Broker()
        self.MH = MsgHandler()
        self.MH.setClient(self)
        self.b.registerCallBack(self.MH)

        self.b.setAuthenticationData("test1","test")
        self.b.setServerName("AztecServer")
        self.b._registrarAddress = "127.0.0.1"

        self.b.init()
        

        #self.fakeInit(self.MH)
    def cameraTask(self, task):
        self.camera.setPos(self.myPlayer.x -2.4,
                           self.myPlayer.y -8,
                           40)
        self.camera.setHpr(0,-80,0)
        return Task.cont

    def addPlayer(self, m):
        self.createObject(m.getInteger("id"),
                         "player",
                         m.getDouble("x"),
                         m.getDouble("y"),
                         m.getDouble("h"))
    def removePlayer(self, m):
        key = m.getInteger("id")
        if key == self.myPlayerID:
            return
        else:
            model = self.objectDic[key].model
            model.removeNode()
            del self.objectDic[key]
        
    def createObject(self,key,objectType, xpos,ypos,head):
        o = Object()        
        o.x = xpos
        o.y = ypos
        o.h = head
        o.oType = objectType
        
        if o.oType == "coin":
            o.model = self.loader.loadModel("models/coin")  
        elif o.oType == "player":
            o.model = self.loader.loadModel("models/coin")
        elif o.oType == "aztec":
            o.model = self.loader.loadModel("models/coin")
        self.objectDic[key] = o
        self.objectDic[key].model.reparentTo(self.render)
        self.objectDic[key].model.setPos(o.x,o.y,0)
        
    def createTree(self, xpos, ypos):
        mod = self.loader.loadModel("models/coin")
        mod.reparentTo(self.render)
        mod.setScale(0.5,0.5,0.5)
        mod.setPos(xpos,ypos,0)
        
    def createHut(self, xpos, ypos):
        mod = self.loader.loadModel("models/hut")
        mod.reparentTo(self.render)
        mod.setScale(0.5,0.5,0.5)
        mod.setPos(xpos,ypos,0)
        
    def createFort(self, xpos, ypos):
        mod = self.loader.loadModel("models/coin")
        mod.reparentTo(self.render)
        mod.setScale(0.5,0.5,0.5)
        mod.setPos(xpos,ypos,0)
        
    def createChest(self, xpos, ypos):
        mod = self.loader.loadModel("models/chest")
        mod.reparentTo(self.render)
        mod.setScale(0.5,0.5,0.5)
        mod.setPos(xpos,ypos,0)

    def moveObject(self, m):
        o = self.objectDic[m.getInteger("ID")]
        o.x = m.getDouble("x")
        o.y = m.getDouble("y")
        o.h = m.getDouble("h")
        o.model.setPos(o.x,o.y,0)
        
    def changeHeading(self, key):
        if key == "arrow_up":
            self.up = 1
        elif key == "arrow_up-up":
            self.up = 0
        elif key == "arrow_down":
            self.down = 1
        elif key == "arrow_down-up":
            self.down = 0
        elif key == "arrow_left":
            self.left = 1
        elif key == "arrow_left-up":
            self.left = 0
        elif key == "arrow_right":
            self.right = 1
        elif key == "arrow_right-up":
            self.right = 0
        vmove = self.up
        vmove = vmove - self.down
        hmove = self.right
        hmove = hmove -self.left
        heading = 1000.0
        if hmove > 0:
            heading = 90.0
        elif hmove < 0:
            heading = 270
        if vmove > 0:
            heading = 0.0
            if hmove > 0:
                heading = 45
            elif hmove < 0:
                heading = 315
        elif vmove < 0:
            heading = 180.0
            if hmove > 0:
                heading = 135.0
            elif hmove < 0:
                heading = 225.0
        #m = Message()
        #m.setString("mid", "moveRequest")
        #m.setDouble("h", heading)
        #self.b.send(m)
        print heading
    def initControls(self):
        self.up = 0
        self.down = 0
        self.left = 0
        self.right = 0

        self.accept("arrow_up", self.changeHeading, ["arrow_up"])
        self.accept("arrow_up-up", self.changeHeading, ["arrow_up-up"])
        self.accept("arrow_down", self.changeHeading, ["arrow_down"])
        self.accept("arrow_down-up", self.changeHeading, ["arrow_down-up"])
        self.accept("arrow_left", self.changeHeading, ["arrow_left"])
        self.accept("arrow_left-up", self.changeHeading, ["arrow_left-up"])
        self.accept("arrow_right", self.changeHeading, ["arrow_right"])
        self.accept("arrow_right-up", self.changeHeading, ["arrow_right-up"])
        
    def loadTerrain(self):
        self.environ = self.loader.loadModel("models/ground")
        self.environ.reparentTo(self.render)
        self.environ.setScale(2, 2, 2)
        self.environ.setPos(-150, -150, -1)
            
    def createWorld(self, m):
        print "init recieved\n"

        #non-Static Objects###############################
        numPlayers = m.getInteger("numPlayers")
        for i in range(numPlayers):
            tmpstr = "Player" + str(i+1)
            self.createObject(m.getInteger("player" + str(i+1)),
                         "player",
                         m.getDouble("x" + tmpstr),
                         m.getDouble("y" + tmpstr),
                         m.getDouble("h" + tmpstr))
            
        numAztecs = m.getInteger("numAztecs")
        for i in range(numAztecs):
            tmpstr = "Aztec" + str(i+1)
            print "aztec" + str(i+1)
            self.createObject(m.getInteger("aztec" + str(i+1)),
                         "aztec",
                         m.getDouble("x" + tmpstr),
                         m.getDouble("y" + tmpstr),
                         m.getDouble("h" + tmpstr))
        
        numCoins = m.getInteger("numCoins")
        for i in range(numCoins):
            tmpstr = "Coin" + str(i+1)
            self.createObject(m.getInteger("coin" + str(i+1)),
                         "coin",
                         m.getDouble("x" + tmpstr),
                         m.getDouble("y" + tmpstr),
                         m.getDouble("h" + tmpstr))
        #static objects####################################
        numTrees = m.getInteger("numTrees")
        for i in range(numTrees):
            tmpstr = "Tree" + str(i+1)
            self.createTree(m.getDouble("x" + tmpstr),
                            m.getDouble("y" + tmpstr))
        
        numHuts = m.getInteger("numHuts")
        for i in range(numHuts):
            tmpstr = "Hut" + str(i+1)
            self.createHut(m.getDouble("x" + tmpstr),
                              m.getDouble("y" + tmpstr)) 

        self.createChest(m.getDouble("xChest"), m.getDouble("yChest"))
        self.createFort(m.getDouble("xFort"), m.getDouble("yFort"))

app = AGClient()
app.run()

