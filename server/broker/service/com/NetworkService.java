package broker.service.com;

import java.net.*;
import broker.service.*;
import broker.service.com.net.*;

/**
 * The NetworkService is the lowest layer within the Broker stack.
 * It is directly placed on the Transport layer, utilizing UDP/IP.
 * It is therefore the applications entry and exit point for data
 * in direction to and from the network.
 */
public class NetworkService extends BrokerServiceWrapper {

	/**
	 * Default constructor
	 */
	public NetworkService() {
	
	}
	
	public Object sapUpperLayer(Object c) {
	
		return null;
	
	}

	public Object sapLowerLayer(Object c) {
	
		return null;
	
	}
	
	/**
	 * This method can be used to create a new socket.
	 */
	public int createSocket() {
	
		boolean created = false;
		int port = 0;
	
		/* try to create a socket repeatedly through random port selection */
		while ( !created ) {
		
			//port = (int)(1025 + Math.random() * 64512);
			port = 2038;
		
			/* creating a default socket */
			try {
			
				new SocketWorker(port, this);
				created = true;	
				
			}
			catch(Exception e) {
				e.printStackTrace();
			}
	
		}
	
		return port;
	
	}

}
