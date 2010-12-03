#!/usr/bin/env python
import math

"""
The message class is an object oriented wrapper for sending messages over a byte-based channel, such as sockets.
It contains some basic header information and a dynamic list of fields, fields are typel of a name and a value. Fields can
be used for sending data, where the name identifies each chunk of data.
"""
class Message: 

        """
        send header configuration for the default protocol
        """
        SESSION_ID_LENGTH = 4
        OBJECT_ID_LENGTH = 4
        REQUEST_ID_LENGTH = 4
        HASH_USED = 0x00 # false = 0x00, true = 0xFF 
        

        NaN                     = 0x7FF8000000000000
        TYPE_STRING      = 0x21
        TYPE_INTEGER    = 0x22
        TYPE_DOUBLE     = 0x23
        TYPE_BYTE          = 0x24
        TYPE_SHORT      = 0x25
        TYPE_BOOLEAN = 0x26
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
        The method takes an integer value with a byte length and creates a not ascii string.
        integer is the value converted for external representation and the last optional parameter
        is the internal representation, this is used to create the correct string
        """
        def _IntegerToString(self, integer, length, internalRepresenation=-0):
                value = str()
                i = 1
                reallength = 0
                shift = 0

                #print "_IntegerToString(",integer,", ",internalRepresenation,")"

                #internal integer is negative, this needs to be converted
                if integer != internalRepresenation:
                        reallength = length
                elif internalRepresenation > 0:
                        reallength = int(math.ceil(math.log(internalRepresenation, 255)))          

                #in case reallength is non-zero a shift value is set
                if reallength > 0:
                        shift = reallength-1
                else:
                        reallength = 1

                #create the actual string/bytesequence  
                while i <= length:

                        #print "length=",length,"; reallength=",reallength,"; shift=",shift
                        
                        if i <= (length-reallength):
                                value = value + chr(0)
                        else:
                                #print "byte=",  (integer>>(shift*8)) & 0xFF
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
        def _StringToInteger(self, byteSequence, offset, length):
                value = 0
                i = 0
                index = 0

                #print "_StringToInteger(",byteSequence,")"
                # check for negative value
               
                while i < length:
                        value = (value<<8)
                        index = offset + i
                        value = value | (ord(byteSequence[index])&0xFF)
                        #print "index=", index,"; value=",ord(byteSequence[index])
                        i += 1

                return value

        """
        Convert from a external signed integer with arbritrary length to an internal signed integer
        """
        def _convertToLocalInteger(self, integer, lengthInByte):

                neg = integer>>((lengthInByte*8)-1)
                if neg != 0:
                        #print "negative=", bin(integer)
                        toggle = 0xFFFFFFFFFFFFFFFF
                        #print "toggle=", bin(toggle)
                        toggle = toggle>>((8-lengthInByte)*8)
                        #print "toggle=", bin(toggle)
                        integer = abs(integer) ^ toggle
                        #print "integer=", bin(integer)
                        integer +=1
                        integer = integer *(-1)

                return integer

        """
        The method is used to convert from the internal signed integer represenation to the external signed integer representation.
        It also tests if the value is allowed in the external format, in case not, it'll throw an exception.
        """
        def _convertToExternalInteger(self, integer, lengthInByte):

                maxInt = (1<<((lengthInByte*8)-1))-1
                minInt = (-1)*(1<<((lengthInByte*8)-1))

                #print "bytes=",lengthInByte,"; max=", maxInt,"; min=", minInt

                if integer < minInt or integer > maxInt:
                        raise ValueError("_convertToExternalInteger() Input value ("+str(integer)+") not in range ["+str(minInt)+","+str(maxInt)+"]")

                if integer < 0:
                        toggle = 0xFFFFFFFFFFFFFFFF
                        #print "toggle=", bin(toggle)
                        toggle = toggle>>((8-lengthInByte)*8)
                        #print "toggle=", bin(toggle)
                        #print "integer=", bin(integer)
                        integer = abs(integer) ^ toggle
                        #print "integer=", bin(integer)
                        integer +=1
                        #print "integer=", bin(integer)
                        
                return integer

        """
        The method returns the number of data fields stored in the message
        """
        def getFieldNumber(self):
                return len(self._data)

        def setString(self, name, value):
                self._data[name] = (Message.TYPE_STRING, value)

        def setInteger(self, name, value):
                self._data[name] = (Message.TYPE_INTEGER, self._IntegerToString(self._convertToExternalInteger(value,4), 4, value))

        def setLong(self, name, value):
               self._data[name] = (Message.TYPE_LONG, self._IntegerToString(self._convertToExternalInteger(value,8), 8, value))

        def setShort(self, name, value):
                 self._data[name] = (Message.TYPE_SHORT, self._IntegerToString(self._convertToExternalInteger(value,2), 2, value))

        def setByte(self, name, value):
                self._data[name] = (Message.TYPE_BYTE, self._IntegerToString(self._convertToExternalInteger(value,1), 1, value))

        def setBoolean(self, name, value):
                boolean = 0xFF

                if value == False:
                        boolean = 0x00
                
                self._data[name] = (Message.TYPE_BOOLEAN, chr(boolean))

        def setDouble(self, name, value):
                v1 = abs(int(value))
                v2 = abs(value) - v1
                binp1 = []
                binp2 = []
                binp3 = []

                #print "part 1 start ", v1

                #converting part 1 of the decimal value (before the comma)
                while v1 > 0:
                        ri = (v1>>1)
                        rf = v1%2
                        v1 = ri
                        binp1.append(rf)


                binp1.pop()
                binp1.reverse()
                bias = len(binp1)

                #convert part2of the dec (behind the comma)
                i=0
                end = 52-len(binp1)
                while i < end:
                        v2=v2*2

                        i += 1
                        if v2 > 1:
                                binp2.append(1)
                                v2 -= 1
                        else:
                                binp2.append(0)

                #determine the exponent
                bias = 1023 + bias
                i = 0
                while bias > 0:
                        ri = (bias>>1)
                        rf = bias%2
                        bias = ri
                        binp3.append(rf)

                binp3.reverse()

                i=0
                while i < 11-len(binp3):
                        binp3.insert(0, 0)
                        i += 1

                #set most significant bit according to the sign
                if value < 0:
                        binp3.insert(0,1)
                else:
                        binp3.insert(0,0)
                
                # combine binp3 and binp1
                binp3.extend(binp1)
                binp3.extend(binp2)

                #create binary representation according to IEEE 754
                i=0
                result = 0
                while i < len(binp3):
                        bit = 0
                        if binp3[i] == 1:
                                bit = 1

                        i += 1
                        result = (result<<1)|bit

                self._data[name] = (Message.TYPE_DOUBLE, self._IntegerToString(result, 8, result)) 
               
        """
        Returns a string if the field is existing or an empty string
        """
        def getString(self, name):
                if name in self._data and self._data[name][0] == Message.TYPE_STRING:
                        return self._data[name][1]
                else:
                        return ""

        """
        Returns an 8 Byte signed integer or NaN if the field is existing.
        """
        def getLong(self, name):
                if name in self._data and self._data[name][0] == Message.TYPE_LONG:
                        return self._convertToLocalInteger(self._StringToInteger(self._data[name][1], 0, len(self._data[name][1])),8)
                else:
                        return Message.NaN

        """
        Returns an 4 Byte signed integer or NaN if the field is existing.
        """
        def getInteger(self, name):
                if name in self._data and self._data[name][0] == Message.TYPE_INTEGER:
                        return self._convertToLocalInteger(self._StringToInteger(self._data[name][1], 0, len(self._data[name][1])),4)
                else:
                        return Message.NaN

        """
        Returns an 2 Byte signed integer or NaN if the field is existing.
        """
        def getShort(self, name):
                if name in self._data and self._data[name][0] == Message.TYPE_SHORT:
                        return self._convertToLocalInteger(self._StringToInteger(self._data[name][1], 0, len(self._data[name][1])),2)
                else:
                        return Message.NaN

        """
        Returns an 2 Byte signed integer or NaN if the field is existing.
        """
        def getByte(self, name):
                if name in self._data and self._data[name][0] == Message.TYPE_BYTE:
                        return self._convertToLocalInteger(self._StringToInteger(self._data[name][1], 0, len(self._data[name][1])), 1)
                else:
                        return Message.NaN

        """
        Returns an Boolean value.
        """
        def getBoolean(self, name):
                if name in self._data and self._data[name][0] == Message.TYPE_BOOLEAN:
                        if ord(self._data[name][1]) == 0xFF:
                                return True
                        else:
                                return False
                else:
                        return False

        """
        The method returns a double precision floating point value. In case of a request to a non
        double field, Message.NaN is returned.
        """
        def getDouble(self, name):
                if name in self._data and self._data[name][0] == Message.TYPE_DOUBLE and len(self._data[name][1]) == 8:

                        # conversion from IEEE 754 double precision floating point value to local double
                        longInt = long(0)
                        longInt = self._StringToInteger(self._data[name][1],0,8)
                        sign =  (longInt&0x8000000000000000)
                        exp = ((longInt&0x7FF0000000000000)>>52) -1023
                        mant = 1 + ((longInt&0x000FFFFFFFFFFFFF) / float(2<<51) )
                        double = math.pow(2,exp) * float(mant)

                        if sign != 0:
                                double = double * (-1.0)
                                
                        return double
                else:
                        return Message.NaN 

      

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
        The method needs to be called in order to generate a byte sequence out of the current message.
        In the data portion of the message the fields are ASN.1 BER encoded. The header has a basic binary
        encoding.
        """
        def encodeBER(self):
                message = str()
                message += self._writeHeader()
                message += self._writeData()

                return message

        """
        The method is utilized by encodeBER() to generate a byte sequence of the header data.
        """
        def _writeHeader(self):
                header = "DP"
                flags = 0

                flags = flags | ((Message.SESSION_ID_LENGTH-1)<<6)
                flags = flags | ((Message.REQUEST_ID_LENGTH-1)<<4)
                flags = flags | ((Message.OBJECT_ID_LENGTH-1)<<2)
                flags = flags | ((Message.HASH_USED)&0x02)
                flags = flags | ((self._error)&0x01)

                #flags and padding
                header += chr(flags)
                header += chr(0)

                header += self._IntegerToString(self._sessionID, Message.SESSION_ID_LENGTH)
                header += self._IntegerToString(self._requestID, Message.REQUEST_ID_LENGTH)
                header += self._IntegerToString(self._objectID, Message.OBJECT_ID_LENGTH)

                return header

        """
        The method generates a byte sequence of ASN.1 BER encoded data fields. The fields contain
        a field id and the field value.
        """
        def _writeData(self):
                data = str()
                iterator = self._data.iteritems()

                while True:
                        try:
                                field = iterator.next()

                                #insert field id
                                data += "!" + chr(len(field[0])) + field[0]
                                #insert value
                                data += chr(field[1][0])

                                if field[1][0] == Message.TYPE_STRING and len(field[1][1]) > 127:
                                        length = len(field[1][1])
                                        lengthbyte = int(math.ceil(math.log(length, 256)))
                                        data += chr(lengthbyte)
                                        data += self._IntegerToString(length, lengthbyte)
                                else:
                                        data += chr(len(field[1][1]))
                                        
                                data += field[1][1]
                               
                        except StopIteration: 
                                break

                return data
                
                

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

                        self._sessionID = self._StringToInteger(ber_str, nextField, sessionIDlength)
                        nextField += sessionIDlength
                        self._requestID = self._StringToInteger(ber_str, nextField, requestIDlength)
                        nextField += requestIDlength
                        self._objectID = self._StringToInteger(ber_str, nextField, objectIDlength)

                        if hashUsed != 0:
                                nextField += objectIDlength
                                self._hash = self._StringToInteger(ber_str, nextField, 4)

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
                                                        length = self._StringToInteger(ber_str, pos, length)

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

        """
        The method returns a String representation of this message object including header and all data fields.
        """
        def toString(self):
                data = str()
                iterator = self._data.iteritems()

                data += "[Message: HEADER[sessionID="+hex(self._sessionID)+"; requestID="+hex(self._requestID)+"; objectID=" + hex(self._objectID) + "]; DATA["

                while True:
                        try:
                                field = iterator.next()

                                if field[1][0] == Message.TYPE_LONG:
                                        data += "[MessageField: type="+hex(field[1][0])+"; name="+field[0]+"; value="+ str(self.getLong(field[0]))+"]"
                                elif field[1][0] == Message.TYPE_INTEGER:
                                        data += "[MessageField: type="+hex(field[1][0])+"; name="+field[0]+"; value="+ str(self.getInteger(field[0]))+"]"
                                elif field[1][0] == Message.TYPE_SHORT:
                                        data += "[MessageField: type="+hex(field[1][0])+"; name="+field[0]+"; value="+ str(self.getShort(field[0]))+"]"
                                elif field[1][0] == Message.TYPE_BYTE:
                                        data += "[MessageField: type="+hex(field[1][0])+"; name="+field[0]+"; value="+ str(self.getByte(field[0]))+"]"
                                elif field[1][0] == Message.TYPE_BOOLEAN:
                                        data += "[MessageField: type="+hex(field[1][0])+"; name="+field[0]+"; value="+ str(self.getBoolean(field[0]))+"]"
                                elif field[1][0] == Message.TYPE_DOUBLE:
                                        data += "[MessageField: type="+hex(field[1][0])+"; name="+field[0]+"; value="+ str(self.getDouble(field[0]))+"]"
                                elif field[1][0] == Message.TYPE_STRING:
                                        data += "[MessageField: type="+hex(field[1][0])+"; name="+field[0]+"; value="+ str(self.getString(field[0]))+"]" 
                               
                        except StopIteration: 
                                break

                        data += "; "


                data += "]]"
                return data
