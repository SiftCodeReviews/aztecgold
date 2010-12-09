from direct.showbase.ShowBase import ShowBase
from direct.task import Task
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
            print"init recieved"
            self.client.loadTerrain()
            self.client.createWorld(request)
            
            #self.client.myPlayerID = request.getObjectID()
            #print self.client.myPlayerID
            #self.client.myPlayer = self.client.objectDic[self.client.myPlayerID]
            
            
        #elif request.getString("mid") == "move":
        #    self.client.moveObject(request)
        #elif request.getString("mid") == "plUpdate":
        #    pass
        return 0
    
        
class Object:
    pass

class AGClient(ShowBase):
    def __init__(self):
        ShowBase.__init__(self)
        self.objectDic = {}
        
        
        self.b = Broker()
        self.MH = MsgHandler()
        self.MH.setClient(self)
        self.b.registerCallBack(self.MH)

        self.b.setAuthenticationData("test1","test")
        self.b.setServerName("AztecServer")
        self.b._registrarAddress = "127.0.0.1"
        
        self.b.init()

        self.fakeInit(MH)

        #init-login

    def fakeInit(self, msgH):
        m = Message()
        m.setString("mid", "init")

        m.setDouble("numTrees", 2)
        m.setDouble("xTree0", -5.0)
        m.setDouble("yTree0", -5.0)
        m.setDouble("xTree1", -5.0)
        m.setDouble("yTree1", -2.0)

        

        dummy = Message()
        msgH.receive(m, dummy)
        
    def createObject(self,key,objectType, xpos,ypos,head):
        o = Object()        
        o.x = xpos
        o.y = ypos
        o.h = head
        o.oType = objectType
        
        if o.oType == "coin":
            o.model = self.loader.loadModel("models/coin")  
        elif o.oType == "player":
            o.model = self.loader.loadModel("models/panda-model")
        elif o.oType == "aztec":
            o.model = self.loader.loadModel("models/coin")

        self.objectDic[key] = o
        self.objectDic[key].model.reparentTo(self.render)
        self.objectDic[key].model.setScale(0.5, 0.5, 0.5)
        self.objectDic[key].model.setPos(o.x,o.y,0)
        
    def createTree(self, xpos, ypos):
        mod = self.loader.loadModel("models/coin")
        mod.reparentTo(self.render)
        mod.setScale(0.5,0.5,0.5)
        mod.setPos(xpos,ypos)
        
    def createHut(self, xpos, ypos):
        mod = self.loader.loadModel("models/coin")
        mod.reparentTo(self.render)
        mod.setScale(0.5,0.5,0.5)
        mod.setPos(xpos,ypos)
        
    def createFort(self, xpos, ypos):
        mod = self.loader.loadModel("models/coin")
        mod.reparentTo(self.render)
        mod.setScale(0.5,0.5,0.5)
        mod.setPos(xpos,ypos)
        
    def createChest(self, xpos, ypos):
        mod = slef.loader.loadModel("models/coin")
        mod.reparentTo(self.render)
        mod.setScale(0.5,0.5,0.5)
        mod.setPos(xpos,ypos)

    def movePlayerTask(self, task):
        #define stuff here
        pass
    def moveObject(self, m):
        if m.getString("objType") == "player":
            o = playerDic[m.getInteger("ID")]
        elif m.getString("objType") == "coin":
            o = coinDic[m.getInteger("ID")]
        elif m.getString("objType") == "aztec":
            o = aztecDic[m.getInteger("ID")]
        else:
            return
        o.x = m.getDouble("x")
        o.y = m.getDouble("y")
        o.h = m.getDouble("h")
        o.model.setPos(o.x,o.y,0)
        
    def loadTerrain(self):
        self.environ = self.loader.loadModel("models/environment")
        #Reparent the model to render.
        self.environ.reparentTo(self.render)
        #Apply scale and position transforms on the model.
        self.environ.setScale(0.25, 0.25, 0.25)
        self.environ.setPos(-8, 42, -1)
            
    def createWorld(self, m):
        print "init recieved\n"

        #non-Static Objects###############################
        '''numPlayers = m.getInteger("numPlayers")
        for i in range(numPlayers):
            tmpstr = "Player" + str(i)
            self.createObject(m.getInteger(tmpstr),
                         "player",
                         m.getDouble("x" + tmpstr),
                         m.getDouble("y" + tmpstr),
                         m.getDouble("h" + tmpstr))'''
            
        '''numAztecs = m.getInteger("numAztecs")
        for i in range(numAztecs):
            tmpstr = "Aztec" + str(i)
            self.createObject(m.getInteger(tmpstr),
                         "aztec",
                         m.getDouble("x" + tmpstr),
                         m.getDouble("y" + tmpstr),
                         m.getDouble("h" + tmpstr))'''
        
        '''numCoins = message.getInteger("numCoins")
        for i in range(numCoins):
            tmpstr = "Coin" + str(i)
            self.createObject(m.getInteger(tmpstr),
                         "coin",
                         m.getDouble("x" + tmpstr),
                         m.getDouble("y" + tmpstr),
                         m.getDouble("h" + tmpstr))'''
        #static objects####################################
        numTrees = m.getInteger("numTrees")
        for i in range(numTrees):
            tmpstr = "Tree" + str(i)
            self.createTree(m.getDouble("x" + tmpstr),
                            m.getDouble("y" + tmpstr))
        
        numHuts = m.getInteger("numHuts")
        for i in range(numHuts):
            tmpstr = "Hut" + str(i)
            self.createObject(m.getDouble("x" + tmpstr),
                              m.getDouble("y" + tmpstr))
        
        

        '''self.createChest(m.getDouble("xChest"), m.getDouble("yChest"))
        self.createFort(m.getDouble("xFort"), m.getDouble("yFort"))'''

app = AGClient()
app.run()

