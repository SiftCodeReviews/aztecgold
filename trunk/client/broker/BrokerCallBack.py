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
    be send, it returns it. In case no response shall be send, the method shall return Null. In case 
    a broadcast shall be send out the method shall return Null but the response message can be used to
    call the Broker method sendBroadcast().
    
    request Message from the client
    response prepared Message that the server shall use to respond to the client
    returns the response Message parameter for a unicast response or Null in all other cases
    """
    def receive(self, request, response):

        print "[BrokerCallBack] receive() - Message with ", request.getFieldNumber() , " field(s) received"

        # reading a prepared message from the server
        double = request.getDouble('doub')

        #to be sure checking double value but maybe not necessary every time
        if Message.NaN == double:
            print "[BrokerCallBack] Value is NaN"
        else:
            print "[BrokerCallBack] Field[doub]: ", double

        print "[BrokerCallBack] Field[name]: ", request.getString("name")
        print "[BrokerCallBack] Field[lname]: ", request.getString("lname")

        #just a little test, on every incoming message the client sends out this message
        response.setString("mid","moveReq")
        response.setDouble("x", 1.65)
        response.setDouble("y", 1.65)

        return response
