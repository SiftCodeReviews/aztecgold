package broker.service.com.net;

import broker.service.*;
import broker.service.com.*;
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
	 * Default constructor
	 */
	public PacketWorker(DatagramPacket p, SocketWorker socketworker) {
	
		this.packet			= p;
		this.socketworker	= socketworker;
		
	}
	
	/**
	 * The method retrieves the data from the newly arrived packet and passes it to the upper
	 * layer and in case of a response, packs this and sends this.
	 */
	public void run() {
	
		System.out.println("[PacketWorker] run() - processing packet " + this.packet);
		
		/* get upper layer from NetworkService */
		BrokerService nbs = this.socketworker.getService().getNextService();
		
		/* only process in case there is an uppe layer existing */
		if ( nbs != null ) {

			/* pass data to upper layer service and receive response as a return value */
			ByteSequence s = new ByteSequence(this.packet.getData(), this.packet.getLength());
			Object oreturn = nbs.sapLowerLayer(s);
			
			/* in case the return value from the upper services is not null a response will be generated */
			if ( oreturn != null ) {
									
				try {
					
					byte[] mbreturn = (byte[])oreturn;
					DatagramPacket preturn = new DatagramPacket(mbreturn, mbreturn.length, this.packet.getSocketAddress());
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
