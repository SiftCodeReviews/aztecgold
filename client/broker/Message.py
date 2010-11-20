#!/usr/bin/env python
import math

"""
The message class is an object oriented wrapper for sending messages over a byte-based channel, such as sockets.
It contains some basic header information and a dynamic list of fields, fields are typel of a name and a value. Fields can
be used for sending data, where the name identifies each chunk of data.
"""
class Message: 

        TYPE_STRING      = 0x21
        TYPE_INTEGER    = 0x22
        TYPE_DOUBLE     = 0x23
        TYPE_BYTE          = 0x24
        TYPE_SHORT      = 0x25
        TYPE_OID            = 0x26
        TYPE_LONG        = 0x27

        """
        Default constructor
        """
        def __init__(self):
                self._objectID = 0
                self._sessionID = 0
                self._data = dict()
                self._requestID = 0
                self._error = 0
                self._hash = 0

        """
       
        """
        def _toString(self, integer, length):
                value = ""
                i = 0
                reallength = math.ceil(math.log(integer, 256))
                shift = int(reallength)
                
                print "reallength=", reallength
                
                while i < length:
                        if i < (length-reallength):
                                value = value + chr(0)
                        else:
                                value = value + chr( (integer>>(shift*8)) & 0xFF )
                                shift -= 1

                        i += 1

                return value

        """
        The method takes a byte sequence and returns its integer value, using highest byte first.
        byteSequence, string interpreted as sequence of bytes, pay attention with unicode strings, since they have 2byte per character
        offset, integer offset value, where to start in the bytesequence
        length, amount of bytes to use
        """
        def _toInteger(self, byteSequence, offset, length):
                value = 0
                i = 0
                index = 0

                while i < length:
                        value = (value<<8)
                        index = offset + i
                        value = value | (ord(byteSequence[index])&0xFF)
                        i += 1

                return value

        """
        The method returns the number of data fields stored in the message
        """
        def getFieldNumber(self):
                return len(self._data)

        def setString(self, name, value):
                self._data[name] = (Message.TYPE_STRING, value)

        def setInteger(self, name, value):
                if self._data[name][0] == Message.TYPE_INTEGER:
                        return self._toInteger(self._data[name][1], 0, len(self._data[name][1]))
                else:
                        return 0xFFFFFFFF

        def setShort(self, name, value):
                 self._data[name] == (Message.TYPE_BYTE, value)

        def setByte(self, name, value):
                self._data[name] == (Message.TYPE_BYTE, value)
               

        def getString(self, name):
                if self._data[name][0] == Message.TYPE_STRING:
                        return self._data[name][1]
                else:
                        return ""

        def getInteger(self, name):
                if self._data[name][0] == Message.TYPE_INTEGER:
                        return self._toInteger(self._data[name][1], 0, len(self._data[name][1]))
                else:
                        return 0xFFFFFFFF

        def getShort(self, name):
                if self._data[name][0] == Message.TYPE_SHORT:
                        return self._toInteger(self._data[name][1], 0, len(self._data[name][1]))
                else:
                        return 0xFFFF

        def getByte(self, name):
                if self._data[name][0] == Message.TYPE_INTEGER:
                        return ord(self._data[name][1])
                else:
                        return 0xFF

        """
        TODO: seems not to be precise, due to an error? CHECK!
        """
        def getDouble(self, name):
                if self._data[name][0] == Message.TYPE_DOUBLE and len(self._data[name][1]) == 8:
                        longInt = long(0)
                        longInt = self._toInteger(self._data[name][1],0,8)
                        sign =  (longInt&0x8000000000000000)
                        exp = ((longInt&0x7FF0000000000000)>>52) -1023
                        mant = 1 + ((longInt&0x000FFFFFFFFFFFFF) / math.pow(2,52))

                        print "exp=%(exp)d; mant=%(mant)d" % {'mant':mant, 'exp':exp}

                        double = math.pow(2,exp) * float(mant)

                        if sign != 0:
                                double = double * (-1.0)
                        
                        return double
                else:
                        return 0xFFFFFFFFFFFFFFFF

      

        """
        The method decodes a complete bytestring containing a header and a ber encoded data body
        ber_str, complete message sequence
        returns True in case everything is alright, False else
        """
        def decodeBER(self, ber_str):
                header_length = self._readHeader(ber_str)

                if header_length != -1:
                        return self._readData(ber_str, header_length);
                else:
                        return False
                        

        """
        The method reads the header information according to the DefaultProtocol.
        ber_str, complete message sequence
        returns the header length or -1 in case of an error
        """
        def _readHeader(self, ber_str):
                protID = (ord(ber_str[0])<<8) | ord(ber_str[1])
                flags = ord(ber_str[2])
                nextField = 4
                headerLength = 4

                # DefaultProtocol
                if protID == 0x4450:
                        sessionIDlength = 1+ ((flags>>6)&0x03)
                        requestIDlength = 1+ ((flags>>4)&0x03)
                        objectIDlength = 1+ ((flags>>2)&0x03)
                        hashUsed = (flags&0x02)
                        self._error = (flags&0x01)

                        # determine the header length
                        headerLength += sessionIDlength + requestIDlength + objectIDlength
                        if hashUsed != 0: headerLength += 4

                        self._sessionID = self._toInteger(ber_str, nextField, sessionIDlength)
                        nextField += sessionIDlength
                        self._requestID = self._toInteger(ber_str, nextField, requestIDlength)
                        nextField += requestIDlength
                        self._objectID = self._toInteger(ber_str, nextField, objectIDlength)

                        if hashUsed != 0:
                                nextField += objectIDlength
                                self._hash = self._toInteger(ber_str, nextField, 4)

                        #print "sessionID=", self._sessionID
                        #print "requestID=", self._requestID
                        #print "objectID=", self._objectID
                        #print "hash=", self._hash

                        return headerLength

                return -1

        """
        The method reads the ber encoded data body of a message and returns false in case of an error.
        ber_str, complete message sequence
        header_length, is used to skip the first bytes
        """
        def _readData(self, ber_str, header_length):
                strlen = len(ber_str)
                pos = header_length
                
                while pos < strlen:
                                        # reading the field id
                                        ber_type = ord(ber_str[pos])

                                        # fid shall be a string if not an error occured
                                        if ber_type == 0x21:
                                        
                                                pos += 1
                                                length = ord(ber_str[pos])&0x7F
                                                start = pos+1
                                                end = start + length
                                                fid = ber_str[start:end]
                                                #print "fid=", fid ,"; length=", length

                                                # reading the field value
                                                pos = end
                                                ber_type = ord(ber_str[pos])
                                                pos += 1
                                                length = ord(ber_str[pos])&0x80
                  
                                                # long length
                                                if length != 0:
                                                        length = ord(ber_str[pos])&0x7F
                                                        start = pos + length + 1
                                                        pos += 1
                                                        length = self._toInteger(ber_str, pos, length)

                                                # short length
                                                else:
                                                        length = ord(ber_str[pos])
                                                        start = pos + 1
                                            
                                                end = start + length
                                                value = ber_str[start:end]
                                                pos = end
                                                self._data[fid] = (ber_type,value)

                                                #print "value=", value ,"; length=", length

                                        # fid was no string -> error
                                        else:
                                                return False
          
                #print "Fields in Message: ", len(self._data)
                return True
