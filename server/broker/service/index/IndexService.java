package broker.service.index;

import broker.service.com.session.SessionObject;
import broker.service.*;
import broker.object.*;
import java.util.*;
import java.net.*;

/**
 * The IndexService is used for broker internal management of Client and Server
 * Objects and their addressing.
 */
public class IndexService extends BrokerServiceWrapper {

	/**
	 * singleton instance for this class
	 */
	private static IndexService instance = null;

	/**
	 * Constant registrars ip
	 */	
	public static String REGISTRAR_IP	= "127.0.0.1";
//	public static String REGISTRAR_IP	= "130.212.3.51";
	
	/**
	 * Constant registrars port
	 */
	public static final int REGISTRAR_PORT	= 0x4931;

	/**
	 * username for authentication against registrar
	 */	
	private String username = "";
	
	/**
	 * password for authentication against registrar
	 */
	private String password = "";
	
	/**
	 * The list stores all registered BrokerCallBacks, they will be
	 * all informed in case of a message.
	 */
	private ArrayList<BrokerCallBack> callBacks;
	
	/**
	 * list of connected distributed objects
	 */
	private Hashtable<Integer,SessionObject> sessionRepository;
	
	/**
	 * list of SocketAddresses mapped to ObjectID's
	 */
	private Hashtable<Integer,SocketAddress> socketaddr;
	
	
	private IndexService() {
	
		this.sessionRepository	= new Hashtable<Integer,SessionObject>();
		this.callBacks			= new ArrayList<BrokerCallBack>();
		this.socketaddr			= new Hashtable<Integer,SocketAddress>();

		/* registering registrar */
		try {
			this.registerSessionObject( new SessionObject(0x80000000, 0x80000000, new InetSocketAddress(InetAddress.getByName(REGISTRAR_IP), REGISTRAR_PORT)) );
		}
		catch(UnknownHostException e) {
			e.printStackTrace();
		}
	
	}
	
	public static synchronized IndexService getInstance() {
	
		if ( IndexService.instance == null )
			IndexService.instance = new IndexService();
			
		return IndexService.instance;
	
	}
	
	
	public String getUsername() {
	
		return this.username;
	
	}
	
	public String getPassword() {
	
		return this.password;
	
	}
	
	public void setAuthenticationData(String username, String password) {
	
		this.username = username;
		this.password = password;
	
	}
				
	
	public synchronized void registerBrokerCallBack(BrokerCallBack bcb) {
	
		this.callBacks.add(bcb);
		
	}
	
	public ArrayList<BrokerCallBack> getBrokerCallBacks() {
	
		return this.callBacks;
	
	}
	
	
	
	public SessionObject lookupSessionObject(int objectID) {
	
		return this.sessionRepository.get(new Integer(objectID));
	
	}
	
	public Enumeration<SessionObject> getAllSessionObjects() {
	
		return this.sessionRepository.elements();
	
	}
	
	public synchronized void registerSessionObject(SessionObject d) {
	
		this.sessionRepository.put(new Integer(d.getObjectID()), d);
		this.registerSocketAddress(d.getObjectID(), d.getSocketAddress());
	
	}
	
	public synchronized void unregisterSessionObject(int objectID) {
	
		this.sessionRepository.remove(new Integer(objectID));
		this.unregisterSocketAddress(objectID);
	
	}
	
	public synchronized void reRegisterSessionObject(int oldObjectID, int newObjectID) {
	
		SessionObject so = this.lookupSessionObject(oldObjectID);
	
		if ( so != null ) {
		
			so.setObjectID(newObjectID);
			this.unregisterSessionObject(oldObjectID);
			this.registerSessionObject(so);
			
			//System.out.println(String.format("[IndexService] reRegisterSessionObject() - from 0x%X to 0x%X@"+so, oldObjectID, newObjectID));
		
		}
	
	}




	public SocketAddress lookupSocketAddress(int objectID) {
	
		return this.socketaddr.get(new Integer(objectID));
	
	}
	
	public boolean lookupSocketAddress(SocketAddress addr) {
	
		return this.socketaddr.containsValue(addr);
	
	}
	
	public synchronized void registerSocketAddress(int objectID, SocketAddress sa) {
	
		this.socketaddr.put(new Integer(objectID), sa);
		//System.out.println(String.format("[IndexService] registerSocketAddress() - 0x%X@" +sa,objectID));
	
	}
	
	public synchronized void reRegisterSocketAddress(int oldObjectID, int newObjectID) {
	
		SocketAddress sa = this.lookupSocketAddress(oldObjectID);
		
		if ( sa != null) {

			this.unregisterSocketAddress(oldObjectID);
			this.socketaddr.put(new Integer(newObjectID), sa);
			//System.out.println(String.format("[IndexService] reregisterSocketAddress() - from 0x%X to 0x%X@"+sa, oldObjectID, newObjectID));
			
		}
		
	}

	public synchronized void unregisterSocketAddress(int objectID) {
	
		this.socketaddr.remove(new Integer(objectID));
	
	}
	
	public Enumeration<SocketAddress> getAllSocketAddresses() {
	
		return this.socketaddr.elements();
	
	}

	
}
