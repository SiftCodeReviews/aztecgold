package broker.service.com.protocol;

import java.net.*;
import broker.service.com.net.*;

/**
 * 
 */
public abstract class Protocol {

	public static final short PID_DEFAULTPROTOCOL = 0x4450;

	protected short protocolID = 0x0000;
	
	public abstract Message assemble(ByteSequence p) throws ProtocolException;
	
	public abstract byte[] disassemble(Message m);
		
}
