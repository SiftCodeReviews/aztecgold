from direct.showbase.ShowBase import ShowBase
from direct.task import Task
import thread
#import direct.TextNode
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
            self.client.isInit = 1
        elif request.getString("mid") == "playerJoined":
            self.client.addPlayer(request)
        elif request.getString("mid") == "playerLeft":
            self.client.removePlayer(request)
        elif request.getString("mid") == "move":
            if self.client.isInit == 0: return
            self.client.moveObject(request)
            
        elif request.getString("mid") == "playerStatus":
            if self.client.isInit == 0: return
            #self.client.coinText.setText(str(request.getInteger("score")))
            #self.client.scoreText.setText(str(request.getInteger("score")))
            
        return 0
    
        
class Object:
    pass

class AGClient(ShowBase):
    def __init__(self):
        ShowBase.__init__(self)
        self.objectDic = {}
        self.isInit = 0
        self.keyboardLock = thread.allocate_lock()
        self.initControls()
        #self.scoreText = TextNode('score text')
        #self.coinText = TextNode('coin text')
        
        self.b = Broker()
        self.MH = MsgHandler()
        self.MH.setClient(self)
        self.b.registerCallBack(self.MH)

        self.b.setAuthenticationData("test4","test")
        self.b.setServerName("AztecServer")
        #self.b._registrarAddress = "130.212.3.51"
        self.b._registrarAddress = "127.0.0.1"

        self.b.init()

    def cameraTask(self, task):
        self.camera.setPos(self.myPlayer.x ,
                           self.myPlayer.y -25,
                           40)
        self.camera.setHpr(0,-60,0)
        return Task.cont

    def addPlayer(self, m):
        key = m.getInteger("id")
        if key == self.myPlayerID:
            return
        self.createObject(key,
                         "player",
                         m.getDouble("x"),
                         m.getDouble("y"),
                         m.getInteger("h"))
    def removePlayer(self, m):
        key = m.getInteger("id")
        if key in self.objectDic:
            if key == self.myPlayerID:
                return
            else:
                model = self.objectDic[key].model
                model.removeNode()
                del self.objectDic[key]
            
    #-----------Movement----------------------#
    #-----------------------------------------#
    def moveObject(self, m):
        oid = m.getInteger("id")
        if oid in self.objectDic:
            o = self.objectDic[oid]
            o.x = m.getDouble("x")
            o.y = m.getDouble("y")
            o.h = m.getInteger("h")
            o.model.setH(0.0 - o.h)
            o.model.setPos(o.x,o.y,1)
            
    def changeHeading(self, key):
        if self.isInit == 0: return
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
        heading = 1000
        if hmove > 0:
            heading = 90
        elif hmove < 0:
            heading = 270
        if vmove > 0:
            heading = 0
            if hmove > 0:
                heading = 45
            elif hmove < 0:
                heading = 315
        elif vmove < 0:
            heading = 180
            if hmove > 0:
                heading = 135
            elif hmove < 0:
                heading = 225
        m = Message()
        m.setString("mid", "moveReq")
        m.setInteger("h", heading)
        self.b.send(m)
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
                         m.getInteger("h" + tmpstr))
            
        numAztecs = m.getInteger("numAztecs")
        for i in range(numAztecs):
            tmpstr = "Aztec" + str(i+1)
            print "aztec" + str(i+1)
            self.createObject(m.getInteger("aztec" + str(i+1)),
                         "aztec",
                         m.getDouble("x" + tmpstr),
                         m.getDouble("y" + tmpstr),
                         m.getInteger("h" + tmpstr))
        
        numCoins = m.getInteger("numCoins")
        for i in range(numCoins):
            tmpstr = "Coin" + str(i+1)
            self.createObject(m.getInteger("coin" + str(i+1)),
                         "coin",
                         m.getDouble("x" + tmpstr),
                         m.getDouble("y" + tmpstr),
                         m.getInteger("h" + tmpstr))
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
        

    #---------Creating Objects---------------------------#
    #----------------------------------------------------#
    def createObject(self,key,objectType, xpos,ypos,head):
        o = Object()        
        o.x = xpos + 1
        o.y = ypos
        o.h = head
        o.oType = objectType
        
        if o.oType == "coin":
            o.model = self.loader.loadModel("models/coin")  
        elif o.oType == "player":
            o.model = self.loader.loadModel("models/fplayer")
        elif o.oType == "aztec":
            o.model = self.loader.loadModel("models/aztec")
        self.objectDic[key] = o
        o.model.reparentTo(self.render)
        o.model.setH(o.model,90)
        o.model.setPos(o.x,o.y,1)
        
    def createTree(self, xpos, ypos):
        mod = self.loader.loadModel("models/tree")
        mod.reparentTo(self.render)
        mod.setPos(xpos,ypos-1,1)
        
        
    def createHut(self, xpos, ypos):
        mod = self.loader.loadModel("models/hut")
        mod.reparentTo(self.render)
        mod.setPos(xpos,ypos-2,1)
        
    def createFort(self, xpos, ypos):
        mod = self.loader.loadModel("models/fort")
        mod.reparentTo(self.render)
        mod.setScale(5.0, 5.0, 5.0)
        mod.setPos(xpos,ypos,1)
        
        
    def createChest(self, xpos, ypos):
        mod = self.loader.loadModel("models/chest")
        mod.reparentTo(self.render)
        mod.setPos(xpos,ypos,1)

app = AGClient()
app.run()

