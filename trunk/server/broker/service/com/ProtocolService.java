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
	
		//System.out.println("[ProtocolService] sapUpperLayer()");
	
		BrokerService p = this.getPreviousService();
		byte[] mb		= this.usedProtocol.disassemble((Message)c);
		
		if ( p != null && mb != null ) {
		
			Message m = (Message)c;
			ByteSequence bs = new ByteSequence(mb, mb.length);
			bs.setObjectID(m.getObjectID());
			bs.setRequestID(m.getRequestID());
			
			p.sapUpperLayer(bs);
			
		}
	
		return null;
	
	}

	public Object sapLowerLayer(Object c) {
	
		//System.out.println("[ProtocolService] sapLowerLayer()");
	
		Object r = null;
		
		try {
				
			/* use Protocol object to assemble the message */
			Message m = this.usedProtocol.assemble((ByteSequence)c);
		
			/* pass to upper layer if existing */
			if ( this.nextService != null ) {

				Message m2		= (Message)this.nextService.sapLowerLayer(m);
				
				/* disassemble response message if one gets returned */
				if ( m2 != null ) {
				
					byte[] mb		= this.usedProtocol.disassemble(m2);
					r				= new ByteSequence(mb, mb.length);
					
				}
				
			}
		
		}
		catch(ProtocolException e) {		
			System.out.println(e.getMessage());
		}
				
		return r;
	
	}
	
	

}
