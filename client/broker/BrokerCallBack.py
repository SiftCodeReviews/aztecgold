#!/usr/bin/env python
from Message import *

"""
The BrokerCallBack class is actually meant to be an Interface and should be implemented by other
classes in order to be able to communicate over the Broker.
"""
class BrokerCallBack:

    def __init__(self):
        self._broker = 0

    """
    The method will be invoked in case a message for the server is arrived. The request message from the 
    client will be given and a prepared response message, in case the server wants to repsond he can
    use the response message, fill in the fields needed and in order to signal that the response shall
    be send, it returns it. In case no response shall be send, the method shall return 0.
    
    request Message from the client
    response prepared Message that the client shall use to respond
    returns the response Message parameter for a unicast response or 0 in all other cases
    """
    def receive(self, request, response):

        # reading a prepared message from the server
        print "[BrokerCallBack] receive() - Message with ", request.getFieldNumber() , " field(s) received"
        #print request.toString()

        #just a little test, on every incoming message the client sends out this message
        response.setString("mid","moveReq")
        response.setByte("byte -1", -1)
        response.setByte("byte 1", 1)
        response.setByte("byte 0", 0)
        response.setByte("byte 127", 127)
        response.setInteger("int -1032", -1032)
        response.setInteger("int 1032", 1032)
        response.setLong("int -121238", -121238)
        response.setDouble("double -231.12", -231.12)
        response.setByte("byte 61", 61)
        response.setLong("long 1000000", 1000000)
        response.setShort("short 32400",32400)
        response.setByte("byte 127", 127)
        response.setBoolean("bool true", True)
        
        return response
