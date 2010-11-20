#!/usr/bin/env python
from random import *
from socket import *
from Message import *
from SocketWorker import *
from BrokerCallBack import *

class Broker:

    def __init__(self):
        self._objectID = 0
        self._sessionID = 0
        self._requestID = 0
        self._clientPort = 0
        self._registrarAddress = ""
        self._registrarPort = 0
        self._serverAddress = ""
        self._serverPort = 0
        self._socket = socket(AF_INET, SOCK_DGRAM)
        self._callBack = 0


    def init(self):
        self._serverAddress = '127.0.0.1'
        self._serverPort = 2034
        self._clientPort = 2040#random.randint(1025,65350)

        self._socket.bind(('127.0.0.1',self._clientPort))
        sw = SocketWorker(self)
        sw.start()
        print "[Broker] SocketWorker started and back in main thread."
        
    def registerCallBack(self, callBack):
        self._callBack = callBack
        callBack._broker = self
        
    def send(self,message):
        self._socket.sendto(message, (self._serverAddress,self._serverPort))
