#!/usr/bin/env python
from random import *
from socket import *
from Message import *
from SocketWorker import *
from BrokerCallBack import *

"""
The Broker manages the communcation between different objects (software components) that are locally separated or even on
the same machine. It uses a registrar to authenticate before the actual communication takes place, the registrars IP
needs to be known, the all other communication partner are dynamically assigned.
"""
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

    """
    This method needs to be called to initialize the communication with the registrar
    and to enable the Broker to communicate with other objects.
    """
    def init(self):
        self._serverAddress = '127.0.0.1'
        self._serverPort = 2038
        self._clientPort = 2040#random.randint(1025,65350)

        self._socket.bind(('127.0.0.1',self._clientPort))
        sw = SocketWorker(self)
        sw.start()
        print "[Broker] SocketWorker started and back in main thread."

    """
    The method stores a callback object at the Broker, it will get informed about
    received Messages.
    bcb BrokerCallBack object
    """
    def registerCallBack(self, bcb):
        self._callBack = bcb
        bcb._broker = self

    """
    The method sends a message to the server. in case of an incomplete header it will complete it.
    """
    def send(self,message):

        if message._objectID == 0:
            message._objectID = self._objectID

        if message._sessionID == 0:
            message._sessionID = self._sessionID

        if message._requestID == 0:
            self._requestID += 1
            message._requestID = self._requestID
        
        self._socket.sendto(message.encodeBER(), (self._serverAddress,self._serverPort))
