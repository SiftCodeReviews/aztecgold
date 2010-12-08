from direct.showbase.ShowBase import ShowBase
from direct.task import Task
import sys
sys.path.append('broker')
from Broker import *
from BrokerCallBack import *
from Message import *

class MsgHandler(BrokerCallBack):
    def receive(self, request, response):
        if request.getString("mid") == "init":
            self.client.createWorld(request)
        #elif request.getString("mid") == "move":
        #    self.client.moveObject(request)
        #elif request.getString("mid") == "plUpdate":
        #    pass
        return 0
    def setClient(self, agclient):
        self.client = agclient
        
class Object:
    pass

class AGClient(ShowBase):
    def __init__(self):
        ShowBase.__init__(self)
        
        b = Broker()
        MH = MsgHandler()
        MH.setClient(self)
        b.registerCallBack(MH)
        b.init()
        
        self.objectDic = {}
        #init-login

        ################################333333
        self.environ = self.loader.loadModel("models/environment")
        # Reparent the model to render.
        self.environ.reparentTo(self.render)
        # Apply scale and position transforms on the model.
        self.environ.setScale(0.5, 0.5, 0.5)
        self.environ.setPos(-8, 42, -1)
        ######################33333
        
    def createObject(self,key,objectType, xpos,ypos,head):
        o = Object()        
        o.x, o.y = xpos, ypos
        o.h = head
        o.oType = objectType
        
        if o.oType == "coin":
            o.model = self.loader.loadModel("models/coin")  
        elif o.oType == "player":
            o.model = self.loader.loadModel("model/panda-model")
        elif o.oType == "tree":
            o.model = self.loader.loadModel("model/coin")
        elif o.oType == "chest":
            o.model = self.loader.loadModel("model/coin")
        elif o.oType == "fort":
            o.model = self.loader.loadModel("model/coin")  
        elif o.oType == "hut":
            o.model = self.loader.loadModel("model/coin")
        elif o.oType == "aztec":
            o.model = self.loader.loadModel("model/coin")

        self.objectDic[key] = o
            
        o.model.reparentTo(self.render)
        o.model.setScale(0.5, 0.5, 0.5)
        o.model.setPos(o.x,o.y,0)

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
            
    def createWorld(self, m):
        print "init recieved\n"
        self.numPlayers = message.getInteger("numPlayers")
        for i in range(numPlayers):
            tmpstr = "player" + str(i)
            self.createObject(message.getInteger(tmpstr + "ID"),
                         "player",
                         m.getDouble("x" + tmpstr),
                         m.getDouble("y" + tmpstr),
                         m.getDouble("h" + tmpstr))
        
        self.numTrees = message.getInteger("numTrees")
        for i in range(numTrees):
            tmpstr = "tree" + str(i)
            self.createObject('''message.getInteger(tmpstr)'''i + 100,
                         "tree",
                         m.getDouble("x" + tmpstr),
                         m.getDouble("y" + tmpstr),
                         '''m.getDouble("h" + tmpstr)'''0)
        
        self.numHuts = message.getInteger("numHuts")
        for i in range(numHuts):
            tmpstr = "hut" + str(i)
            self.createObject('''message.getInteger(tmpstr)'''i + 10,
                         "hut",
                         m.getDouble("x" + tmpstr),
                         m.getDouble("y" + tmpstr),
                         '''m.getDouble("h" + tmpstr)'''0)
        
        self.numAztecs = message.getInteger("numAztecs")
        for i in range(numAztecs):
            tmpstr = "aztec" + str(i)
            self.createObject('''message.getInteger(tmpstr)'''i+ 1000,
                         "aztec",
                         m.getDouble("x" + tmpstr),
                         m.getDouble("y" + tmpstr),
                         '''m.getDouble("h" + tmpstr)'''0)
        
        '''self.numCoins = message.getInteger("numCoins")
        for i in range(numCoins):
            tmpstr = "coin" + str(i)
            self.createObject(message.getInteger(tmpstr + "ID"),
                         "coin",
                         m.getDouble("x" + tmpstr),
                         m.getDouble("y" + tmpstr),
                         m.getDouble("h" + tmpstr)0)'''

        self.createObject(0, "chest", m.getDouble("xchest"), m.getDouble("ychest"), m.getDouble("chesth"))
        self.createObject(0, "fort", m.getDouble("xfort"), m.getDouble("yfort"), m.getDouble("forth"))

app = AGClient()
app.run()

