package broker.service.com.net;

import broker.service.*;
import broker.service.com.*;
import broker.service.index.*;
import java.net.*;

/**
 * The PacketWorker is a Thread that exclusively handles one incoming packet
 * and processes all layers and the optional response in its context. It is part of
 * the NetworkService, it gets started through the Socketworker which is responsible
 * for receiving the packets.
 */
public class PacketWorker implements Runnable {

	/**
	 * Packet to process
	 */
	private DatagramPacket packet;
	
	/**
	 * SocketWorker who started the PacketWorker, this is the connection to the NetworkService
	 */
	private SocketWorker socketworker;
	
	/**
	 * keeps the reference to the networkservice, to speed up the access
	 */
	private NetworkService service;
	
	/**
	 * keeps the reference to the indexService, to speed up the access
	 */
	private IndexService index;
	
							
	/**
	 * Default constructor
	 */
	public PacketWorker(DatagramPacket p, SocketWorker socketworker) {
	
		this.packet			= p;
		this.socketworker	= socketworker;
		this.service		= socketworker.getService();
		this.index			= this.service.getIndexService();
		
	}
	
	/**
	 * The method retrieves the data from the newly arrived packet and passes it to the upper
	 * layer and in case of a response, packs this and sends this.
	 */
	public void run() {
	
		SocketAddress sa = this.packet.getSocketAddress();
	
		//System.out.println("[PacketWorker] run() - processing packet from " + sa);
				
		/* get upper layer from NetworkService */
		BrokerService nbs = this.service.getNextService();
		
		/* only process in case there is an upper layer existing */
		if ( nbs != null ) {

			/* pass data to upper layer service and receive response as a return value */
			ByteSequence s = new ByteSequence(this.packet.getData(), this.packet.getLength());
			
			/* registeres the socket address */
			if ( this.service.accepts() && !this.index.lookupSocketAddress(sa) ) {
				
				int objectID;
				
				do {
					objectID = (-1) * (int)(100+Math.random()*400000);
				}
				while ( this.index.lookupSocketAddress(objectID) != null );
				
				this.index.registerSocketAddress(objectID, sa);
				s.setObjectID(objectID);
										
			}
			
			Object oreturn = nbs.sapLowerLayer(s);
			
			/* in case the return value from the upper services is not null a response will be generated */
			if ( oreturn != null ) {
									
				try {
					
					ByteSequence mbreturn = (ByteSequence)oreturn;
					DatagramPacket preturn = new DatagramPacket(mbreturn.getSequence(), mbreturn.getLength(), this.packet.getSocketAddress());
					int retries = 0;
					
					/* retry sending for 5 times */
					while( !this.socketworker.send(preturn) && retries < 5  ) {				
						retries++;
					}
			
				}
				catch(Exception e) {
				
					e.printStackTrace();
				
				}
		
			}

		}
	
	}
	
}
