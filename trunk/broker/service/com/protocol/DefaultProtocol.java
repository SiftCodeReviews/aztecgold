package broker.service.com.protocol;

import java.net.*;

/**
 * 
 */
public class DefaultProtocol extends Protocol {

	public DefaultProtocol() {
	
		this.protocolID = Protocol.PID_DEFAULTPROTOCOL;
	
	}
	

	public Message assemble(byte[] mstream, int length) throws ProtocolException {
	
		Message m = null;
		
		/* protocol id correct */
		if ( mstream[0] == (this.protocolID>>8) && mstream[1] == (this.protocolID&0xFF) ) {

			/* min length */
			int headerLength	= 4;
			int sessionIDLength = 0;
			int objectIDLength	= 0;
			int hashLength		= 0;
			
			/* determine oid length */
			objectIDLength	= 1+((mstream[2]>>>4)&0x03);
			System.out.println("[DefaultProtocol] oid length = " + objectIDLength);

			/* determine session id length */
			sessionIDLength = 1+((mstream[2]>>>6)&0x03);
			System.out.println("[DefaultProtocol] session id length = " + sessionIDLength);

			/* determine if hash is used */
			hashLength		= ( (mstream[2]&0x08) != 0 ) ? 4 : 0;
			System.out.println("[DefaultProtocol] hash length = " + hashLength);
			
			headerLength += objectIDLength + sessionIDLength + hashLength;
			
			int sessionID = ByteConversion.toInteger(mstream, 4, 4);
			int objectID = ByteConversion.toInteger(mstream, 4, 4);
			System.out.println(String.format("[DefaultProtocol] Session ID = 0x%X", sessionID));
			
			System.out.println("[DefaultProtocol] Header length = " + headerLength);
			
			/* create message and insert parsed data */
			m = new Message(objectID,sessionID);
			m.setData( MessageField.parseBERData(mstream, headerLength, length) );
		
		}
		
		/* protocol id incorrect */
		else {
			throw new ProtocolException("[DefaultProtocol] ProtocolException is thrown because the received packet is not of type DefaultProtocol.");
		}
	
		return m;
	
	}
	
	public byte[] disassemble(Message m) {
		
		return null;
	
	}
	
}
