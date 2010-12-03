package broker.service.com.protocol;

import broker.service.com.net.*;
import java.net.*;
import java.util.*;

/**
 * 
 */
public class DefaultProtocol extends Protocol {

	public static final int SESSION_ID_LENGTH = 4;

	public static final int REQUEST_ID_LENGTH = 4;

	public static final int OBJECT_ID_LENGTH = 4;

	public static final boolean HASH_USED = false;
	
			
							

	public DefaultProtocol() {
	
		this.protocolID = Protocol.PID_DEFAULTPROTOCOL;
	
	}
	
	public Message assemble(ByteSequence p) throws ProtocolException {
	
		Message m = null;
		byte[] mstream	= p.getSequence();
		int length		= p.getLength();
		
		/*
		for(int i=0; i < length; i++)
			System.out.print(String.format("0x%X ", mstream[i]));
				
			System.out.println();
		*/
		
		/* protocol id correct */
		if ( mstream[0] == (this.protocolID>>8) && mstream[1] == (this.protocolID&0xFF) ) {

			/* min length */
			int headerLength	= 4;
			int sessionIDLength = 0;
			int requestIDLength	= 0;
			int objectIDLength	= 0;
			int hashLength		= 0;
			boolean error		= false;
			int nextIndex		= 4;
			int hash			= 0;
			
			/* determine session id length */
			sessionIDLength = 1+((mstream[2]>>>6)&0x03);
			//System.out.println("[DefaultProtocol] session id length = " + sessionIDLength);
			
			/* determine session id length */
			requestIDLength = 1+((mstream[2]>>>4)&0x03);
			//System.out.println("[DefaultProtocol] session id length = " + sessionIDLength);
			
			/* determine oid length */
			objectIDLength	= 1+((mstream[2]>>>2)&0x03);
			//System.out.println("[DefaultProtocol] oid length = " + objectIDLength);

			/* determine if hash is used */
			hashLength		= ( (mstream[2]&0x02) != 0 ) ? 4 : 0;
			//System.out.println("[DefaultProtocol] hash length = " + hashLength);
			
			/* determine if it is an error message */
			error			= ( (mstream[2]&0x01) != 0 ) ? true : false;
			
			headerLength += objectIDLength + sessionIDLength + hashLength + requestIDLength;
			
			int sessionID	= (int)ByteConversion.byteToLong(mstream, nextIndex, 4);
			nextIndex		+= sessionIDLength;
			int requestID	= (int)ByteConversion.byteToLong(mstream, nextIndex, 4);
			nextIndex		+= requestIDLength;			
			int objectID	= (int)ByteConversion.byteToLong(mstream, nextIndex, 4);
			
			if ( hashLength != 0 ) {
			
				nextIndex		+= objectIDLength;			
				hash		= (int)ByteConversion.byteToLong(mstream, nextIndex, 4);
				
			}
			
//			System.out.println(String.format("[DefaultProtocol] sessionID = 0x%X", sessionID));
//			System.out.println(String.format("[DefaultProtocol] requestID = 0x%X", requestID));
//			System.out.println(String.format("[DefaultProtocol] objectID = 0x%X", objectID));
//			System.out.println(String.format("[DefaultProtocol] hash = 0x%X", hash));
//			System.out.println("[DefaultProtocol] Header length = " + headerLength);
			
			/* create message and insert parsed data */
			if ( (objectID&0x80000000) != 0 && p.getObjectID() != -1 )
				objectID = p.getObjectID();
			
			m = new Message(sessionID,requestID,objectID);
			m.setData( MessageField.parseBERData(mstream, headerLength, length) );
			
			if ( error )
				m.setError();
			
		}
		
		/* protocol id incorrect */
		else {
			throw new ProtocolException("[DefaultProtocol] ProtocolException is thrown because the received packet is not of type DefaultProtocol.");
		}
	
		return m;
	
	}
	
	public byte[] disassemble(Message m) {
		
		Hashtable<String,MessageField> md = m.getData();
		Enumeration<MessageField> mde = md.elements();
		
		/* determine total encoded message size */
		int offset = 4;		
		int headerSize = offset + SESSION_ID_LENGTH + REQUEST_ID_LENGTH + OBJECT_ID_LENGTH;
		int totalSize = headerSize;
		while(mde.hasMoreElements())
			totalSize += mde.nextElement().getBERDataSize();
		
		/* create buffer for encoded message */
		byte[] mb = new byte[totalSize];
		byte flags = 0;
		
		/* add the header */
		mb[0]	= (byte)(this.protocolID>>8);
		mb[1]	= (byte)(this.protocolID&0xFF);	
		
		flags	= (byte)(flags | ((SESSION_ID_LENGTH-1)<<6));
		flags	= (byte)(flags | ((REQUEST_ID_LENGTH-1)<<4));
		flags	= (byte)(flags | ((OBJECT_ID_LENGTH-1)<<2));
		flags	= (byte)(flags | ((HASH_USED?1:0)<<1));
		flags	= (byte)(flags | (m.getError()?1:0));
		mb[2]	= flags;
				
		ByteConversion.longToByte(m.getSessionID(), SESSION_ID_LENGTH, mb, offset);
		offset += SESSION_ID_LENGTH;
		
		ByteConversion.longToByte(m.getRequestID(), REQUEST_ID_LENGTH, mb, offset);
		offset += REQUEST_ID_LENGTH;

		ByteConversion.longToByte(m.getObjectID(), OBJECT_ID_LENGTH, mb, offset);
		offset += OBJECT_ID_LENGTH;
		
		/* add the fields */
		mde = md.elements();
		while(mde.hasMoreElements())
			offset = (mde.nextElement()).generateBERData(mb, offset);
		
		/*
		for(int i=0; i < totalSize; i++)
			System.out.print(String.format("0x%X ", mb[i]));
		
		System.out.println();
		*/
		return mb;
	
	}
	
}
