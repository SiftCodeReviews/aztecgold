package broker.service.com.protocol;

import java.net.*;

/**
 * 
 */
public abstract class Protocol {

	public static final short PID_DEFAULTPROTOCOL = 0x4450;

	protected short protocolID = 0x0000;
	
	public abstract Message assemble(byte[] mstream, int length) throws ProtocolException;
	
	public abstract byte[] disassemble(Message m);
		
}
