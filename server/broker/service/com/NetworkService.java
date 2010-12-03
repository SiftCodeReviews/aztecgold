package broker.service.com;

import java.util.*;
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

	/**
	 * global IndexService
	 */
	private IndexService index = null;
	
	/**
	 * contains a fix socket number to use or -1 in case random selection shall be used
	 */
	private int fixSocket = -1;
	
	/**
	 * indicates wether the NetworkService shall accept and register new 
	 */
	private boolean accept = false;

	


	/**
	 * Default constructor
	 */
	public NetworkService(IndexService index) {
	
		this.init(index);

	}
	
	/**
	 * Default constructor
	 */
	public NetworkService(IndexService index, int fixSocket, boolean accept) {
	
		this.fixSocket	= fixSocket;
		this.accept		= accept;
		this.init(index);
				
	}
	
	/**
	 * The method performs common initialization functions
	 */
	private void init(IndexService index) {

		this.index		= index;
		this.createSocket();
	
	}
	
	/**
	 * The method returns the value of the accept flag, that indicates wether the NetworkService
	 * accepts and registeres new SocketAddr. This function is used for the registrar application.
	 */
	public boolean accepts() {
	
		return this.accept;
	
	}

	
	public IndexService getIndexService() {
	
		return this.index;
	
	}

	
	public Object sapUpperLayer(Object c) {
	
		if ( c != null ) {

			//System.out.println("[NetworkService] sapUpperLayer()");

			ByteSequence s	= (ByteSequence)c;
			int objectID	= s.getObjectID();
			int requestID	= s.getRequestID();
			SocketAddress dst0;
			
			/* unicast message */
			if ( requestID != 0x7FFFFFFF ) {
			
				dst0 = this.index.lookupSocketAddress(objectID);
				this.sendToSocket(dst0, s);
					
			}
			
			/* requestID 0xFFFFFFn indicates a broadcast message */
			else  {
			
				System.out.println("BROADCAST");
				Enumeration<SocketAddress> socketlist = this.index.getAllSocketAddresses();
				
				while(socketlist.hasMoreElements()) {
				
					dst0 = socketlist.nextElement();
					this.sendToSocket(dst0, s);
				
				}
			
			}
			
		}
	
		return null;
	
	}


	private void sendToSocket(SocketAddress dst0, ByteSequence s) {
	
		if ( dst0 != null ) {
				
				try {
				
					DatagramPacket p = new DatagramPacket(s.getSequence(), 0, s.getLength(), dst0);
					this.socketworker.send(p);
					
				}
				catch(Exception e) {
				
					e.printStackTrace();
				
				}
				
			}
			
			else {
			
				throw new RuntimeException(String.format("[NetworkService] Unknown Destination [objectID=0x%X]", s.getObjectID()));
			
			}
	
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
			
			if ( fixSocket < 0 )
				port = (int)(1025 + Math.random() * 64512);
				//port = 2038;
			else
				port = fixSocket;
		
			/* creating a default socket */
			try {
			
				this.socketworker = new SocketWorker(port, this);
				created = true;	
				
			}
			catch(Exception e) {
				System.out.println(port);
				e.printStackTrace();
			}
			
			retries--;
	
		}
	
		return port;
	
	}

}
