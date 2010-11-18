package broker.service.com;

import java.net.*;
import broker.service.*;
import broker.service.com.protocol.*;
import broker.service.com.net.ByteSequence;

/**
 * The ProtocolService is responsible for the translation between the byte stream from the
 * socket to an broker internal message object. It utilizes the visitor pattern to implement the
 * actual protocol.
 */
public class ProtocolService extends BrokerServiceWrapper {

	private Protocol usedProtocol;
	
	public ProtocolService() {
	
		this.usedProtocol = new DefaultProtocol();
	
	}

	public ProtocolService(Protocol p) {
	
		this.usedProtocol = p;
	
	}

	public Object sapUpperLayer(Object c) {
	
		return null;
	
	}

	public Object sapLowerLayer(Object c) {
	
		System.out.println("[ProtocolService] sapLowerLayer()");
	
		Object r = null;
		
		try {
		
			ByteSequence p = (ByteSequence)c;
		
			/* use Protocol object to assemble the message */
			Message m = this.usedProtocol.assemble(p.getSequence(), p.getLength());
		
			/* pass to upper layer if existing */
			if ( this.nextService != null )
				r = this.nextService.sapLowerLayer(m);
		
		}
		catch(ProtocolException e) {		
			System.out.println(e.getMessage());
		}
				
		return r;
	
	}
	
	

}
