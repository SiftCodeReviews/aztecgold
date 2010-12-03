#!/usr/bin/env python
from socket import *
from Message import *
from Broker import *
from BrokerCallBack import *

#m = Message()
#print "zahl: ", m._StringToInteger(m._IntegerToString(1024,4),0,4)
#print "name: ", m.getDouble('doub')
#m.setDouble("test", 18.4)
#print "test: ", m.getDouble("test")
#mString = m.encodeBER()
#print mString ,"; length=", len(mString)

b = Broker()
bc = BrokerCallBack()
b.registerCallBack(bc)

b.init()
