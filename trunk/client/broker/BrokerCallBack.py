#!/usr/bin/env python
#import Broker

class BrokerCallBack:

    def __init__(self):
        self._broker = 0
    
    def receive(self, message):
        print "[BrokerCallBack] receive() - Message with ", message.getFieldNumber() , " field(s) received"
