#!/usr/bin/env python
import threading
from socket import *
#import Broker import *
from Message import *


class SocketWorker(threading.Thread):

    def __init__(self, b):
        threading.Thread.__init__(self)
        self._socket = b._socket
        self._broker = b

    def run(self):

        print "[SocketWorker] run()"

        while 1:
            message, client = self._socket.recvfrom(4096)
            print "[SocketWorker] Packet received from ", client[0] , "(server=",self._broker._serverAddress,")"
            if cmp(self._broker._serverAddress,client[0]) == 0:
                m = Message()
                m.decodeBER(message)
                self._broker._callBack.receive(m)   
            
