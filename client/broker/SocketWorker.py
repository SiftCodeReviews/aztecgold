#!/usr/bin/env python
import threading
from socket import *
#import Broker import *
from Message import *

"""
The SocketWorker handles incoming data form a socket. This is done in its own Thread, so that
each SocketWorker its own Thread executes.
"""
class SocketWorker(threading.Thread):

    def __init__(self, b):
        threading.Thread.__init__(self)
        self._socket = b._socket
        self._broker = b

    def run(self):

        print "[SocketWorker] run()"

        while 1:
            message, client = self._socket.recvfrom(4096)
            #print "[SocketWorker] Packet received from ", client[0] ,":",client[1]
            
            m = Message()
            r = Message()

            # decode bytesequence
            m.decodeBER(message)

            # prepare response message
            r._sessionID = m._sessionID
            r._objectID = m._objectID
            r._requestID = m._requestID

            # callback message
            if (m._requestID&0x80000000) == 0:
                #print "[SocketWorker] callback requestID=", hex(m._requestID)
                r = self._broker._callBack.receive(m, r)

            # SessionManager message
            else:
                #print "[SocketWorker] sessionManager requestID=", hex(m._requestID)
                r = self._broker._sessionManager.receive(m, r)

            if r != 0:
                self._broker.send(r)
