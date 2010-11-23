package broker.service.com;

import java.net.*;
import broker.service.*;
import broker.service.com.net.*;
import broker.service.index.*;

/**
 * The NetworkService is the lowest layer within the Broker stack.
 * It is directly placed on the Transport layer, utilizing UDP/IP.
 * It is therefore the applications entry and exit point for data
 * in direction to and from the network.
 */
public class NetworkService extends BrokerServiceWrapper {

	/**
	 * SocketWorker 
	 */
	private SocketWorker socketworker = null;

	private IndexService index = null;

	/**
	 * Default constructor
	 */
	public NetworkService(IndexService index) {
	
		this.createSocket();
		this.index = index;
	
	}
	
	public Object sapUpperLayer(Object c) {
	
		if ( c != null ) {

			//System.out.println("[NetworkService] sapUpperLayer()");

			ByteSequence s	= (ByteSequence)c;
			int objectID	= s.getObjectID();
			DistObject dstO = this.index.lookupDistObject(objectID);
			
			if ( dstO != null ) {
				
				try {
				
					DatagramPacket p = new DatagramPacket(s.getSequence(), 0, s.getLength(), dstO.getAddr());
					this.socketworker.send(p);
					
				}
				catch(Exception e) {
				
					e.printStackTrace();
				
				}
				
			}
		
		}
	
		return null;
	
	}


	
	/**
	 * This method can be used to create a new socket.
	 */
	private int createSocket() {
	
		boolean created = false;
		int port = 0;
		int retries = 10;
	
		/* try to create a socket repeatedly through random port selection */
		while ( !created ) {
		
			if ( retries < 0 ) {
			
				port = -1;
				break;
			
			}
			
			//port = (int)(1025 + Math.random() * 64512);
			port = 2038;
		
			/* creating a default socket */
			try {
			
				this.socketworker = new SocketWorker(port, this);
				created = true;	
				
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
			retries--;
	
		}
	
		return port;
	
	}

}
