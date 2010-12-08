#!/usr/bin/env python
from random import *
from socket import *
from Message import *
from SocketWorker import *
from BrokerCallBack import *
from SessionManager import *

"""
The Broker manages the communcation between different objects (software components) that are locally separated or even on
the same machine. It uses a registrar to authenticate before the actual communication takes place, the registrars IP
needs to be known, the all other communication partner are dynamically assigned.
"""
class Broker:

    def __init__(self):
        self._objectID = 0x80000000
        self._sessionID = 0x80000000
        self._requestID = 0x80000000
        self._clientPort = 0
        self._clientAddress = ""
        self._registrarAddress = "127.0.0.1"
        self._registrarPort = 0x4931
        self._serverAddress = ""
        self._serverPort = 0
        self._serverName = ''
        self._socket = socket(AF_INET, SOCK_DGRAM)
        self._callBack = 0
        self._username = ""
        self._password = ""
        self._sessionManager = 0
        self._state = "offline"
        self._externalIP = ''
        self._externalPort = 0

    """
    This method needs to be called to initialize the communication with the registrar
    and to enable the Broker to communicate with other objects.
    """
    def init(self):
        self._clientPort = randint(1025,65350)
        self._socket.bind((self._clientAddress,self._clientPort))
        sw = SocketWorker(self)
        sw.start()

        # load SessionManager
        self._sessionManager = SessionManager(self)
        self._sessionManager.start()
        
        print "[Broker] SocketWorker started and back in main thread."

    """
    The method assigns the username and password needed to authenticate at the registrar
    """
    def setAuthenticationData(self, username, password):
        self._username = username
        self._password = password


    """
    The method stores a callback object at the Broker, it will get informed about
    received Messages.
    bcb BrokerCallBack object
    """
    def registerCallBack(self, bcb):
        self._callBack = bcb
        bcb._broker = self

    """
    The method returns the Broker state, it can be either "offline" or "online", online indicates that an active session
    is existing.
    """
    def getState(self):
        return self._state

    def setServerName(self, servername):
        self._serverName = servername

    """
    The method sends a message to the server. in case of an incomplete header it will complete it.
    """
    def send(self,message,destination='server'):

        message._sessionID = self._sessionID

        if (message._objectID == 0):
            message._objectID = self._objectID

        if message._requestID == 0:
            self._requestID += 1
            self._requestID = self._requestID&0x7FFFFFFF
            message._requestID = self._requestID

        if destination == 'server' and self._serverAddress != "":
            self._socket.sendto(message.encodeBER(), (self._serverAddress,self._serverPort))
        else:
            self._socket.sendto(message.encodeBER(), (self._registrarAddress,self._registrarPort))
