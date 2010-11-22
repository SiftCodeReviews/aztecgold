package broker.service.com.net;

import broker.service.com.*;
import java.net.*;
import java.io.*;
import java.util.concurrent.*;

/**
 * The SocketWorker is a concurrent handler for incoming socket data, but it does not process the
 * data itself, it delegates this to a PacketWorker Thread and keeps waiting on the Thread.
 */
public class SocketWorker implements Runnable {

	/**
	 * NetworkService the SocketWorker is running in.
	 */
	private NetworkService service;

	/**
	 * The UDP Socket used for receiving and sending data.
	 */
	private DatagramSocket socket;
	
	/**
	 * flag that can be used to stop listening to the socket.
	 */
	private boolean running = true;
	
	/**
	 * Threadpool used to serve all the incoming packets
	 */
	private ExecutorService threadPool = null;

	
	
	/**
	 * Extended constructor, expects the port on the localhost IP to handle and the NetworkService
	 * Object the worker is running from.
	 */
	public SocketWorker(int port, NetworkService service) throws SocketException {
		
		this.socket		= new DatagramSocket(port);
		this.service	= service;
		this.threadPool = Executors.newCachedThreadPool();
		
		Thread t = new Thread(this);
		t.start();
	
	}
	
	/**
	 * The method stops listening and binding the socket.
	 */
	public void stop() {
	
		this.running = false;
	
	}
	
	/**
	 * The method returns the NetworkService object the worker is running in.
	 */
	NetworkService getService() {
	
		return this.service;
	
	}
	
	/**
	 * The method can be used to send data over the binded socket. 
	 * @param p DatagramPacket to send
	 */
	boolean send(DatagramPacket p) {
	
		try {
			this.socket.send(p);	
		}
		catch(Exception e) {
			return false;
		}
		
		return true;
	
	}
	
	/**
	 * The method listens to the port and in case of an incoming packet, starts a PacketWorker Thread,
	 * using the Threadpool and continues listening.
	 */
	public void run() {
	
		DatagramPacket p;
		PacketWorker pw;
		byte[] buffer;
		int receiveAttempts = 0;
		
		try{
		DatagramPacket dp = new DatagramPacket(new byte[1024], 1024, InetAddress.getByName("127.0.0.1"), 2040);
		byte[] sendM = {/* protocol id */ (byte)0x44,(byte)0x50, /* flags */ (byte)0xFE, /* padding */ (byte)0x0, /* session id */(byte)0x7F,(byte)0xFF,(byte)0xFF,(byte)0xFF, /* request id */(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x1, /* object id */(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x2, /* hash */(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x3, /*Data: name=peter*/(byte)0x21,(byte)0x4,(byte)0x6E,(byte)0x61,(byte)0x6D,(byte)0x65,(byte)0x21,(byte)0x5,(byte)0x50,(byte)0x65,(byte)0x74,(byte)0x65,(byte)0x72, /* data2 */ (byte)0x21,(byte)0x5,(byte)'l',(byte)0x6E,(byte)0x61,(byte)0x6D,(byte)0x65,(byte)0x21,(byte)0x81,(byte)136,(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',/* data3 double */ (byte)0x21,(byte)0x4,(byte)'d',(byte)'o',(byte)'u',(byte)'b',(byte)0x23,(byte)0x8,(byte)0x40,(byte)0x32,(byte)0x66,(byte)0x66,(byte)0x66,(byte)0x66,(byte)0x66,(byte)0x65, /* byte */(byte)0x21,(byte)0x1,(byte)'x',(byte)0x24,(byte)0x01,(byte)0x80};
		dp.setData(sendM);
		this.socket.send(dp);
		}catch(Exception e) {};
	
		while(this.running && receiveAttempts < 10) {
		
			buffer = new byte[1024];
			p = new DatagramPacket(buffer, buffer.length);
			
			System.out.println("[SocketWorker] run() - waiting for packet on socket " + this.socket.getLocalSocketAddress());
			
			/* wait on socket for the next packet */
			try {
			
				this.socket.receive(p);
				
			}
			catch(IOException e) {
				
				receiveAttempts++;
			
			};

			System.out.println("[SocketWorker] run() - packet received "+ p + " on socket " + this.socket.getLocalSocketAddress());

			/* load packet to packetworker and process it in parallel */
			pw = new PacketWorker(p, this);
			this.threadPool.execute(pw);
		
		}
	
		this.socket.close();
	
	}
	
}
