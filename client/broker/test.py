#!/usr/bin/env python
from socket import *
from Message import *
from Broker import *
from BrokerCallBack import *

#sock = socket(AF_INET, SOCK_DGRAM)
#sock.bind(('127.0.0.1',2034))
#while 1:    # Run until cancelled
#   message, client = sock.recvfrom(1024) # <=256 byte datagram
#   print "\nClient connected:", client

#  m = Message()
#   m.decodeBER(message)

   #print "name: ", m.getDouble('doub')

 #  print "zahl: ",m._toInteger(m._toString(120,4), 0,4)

#   sock.sendto("Test", client)

b = Broker()
bc = BrokerCallBack()
b.registerCallBack(bc)

b.init()
