package broker;

import broker.object.BrokerCallBack;
import broker.service.com.*;
import broker.service.com.protocol.*;
import broker.service.index.*;

/**
 * Singleton wrapper facade for the different BrokerServices.
 */
public class Broker {

	/**
	 * static singleton instance
	 */
	private static Broker instance = null;
	
	/**
	 * The NetworkService used by the Broker
	 */
	private NetworkService netService = null;
	
	/**
	 * The ProtocolService used by the Broker
	 */
	private ProtocolService proService = null;

	/**
	 * The CommunicationService used by the Broker
	 */
	private CommunicationService comService = null;

	/**
	 * The CommunicationService used by the Broker
	 */
	private MessageRouterService routService = null;
	
	/**
	 * The IndexService used by the Broker
	 */
	private IndexService indexService = null;
	

	
	/** 
	 * Private constructor, to provide singleton object
	 */
	private Broker() {

		/* IndexService */
		this.indexService = IndexService.getInstance();

		/* Message Router Service */
		this.routService = new MessageRouterService(this.indexService);

		/* Communication Service */
		this.comService = new CommunicationService(this.indexService);
		this.comService.setNextService(this.routService);

		/* Protocol Service */
		this.proService = new ProtocolService();
		this.proService.setNextService(this.comService);

		/* NetworkService */
		this.netService = new NetworkService(this.indexService);
		this.netService.setNextService(this.proService);
		
	}
	
	/**
	 * This method is a synchronized way to create or access a singleton instance of Broker.
	 * @return singleton instance of Broker
	 */
	public synchronized static Broker getInstance() {
	
		if ( Broker.instance == null )
			Broker.instance = new Broker();
	
		return Broker.instance;
	
	}
	
	/**
	 * This method needs to be called to initialize the communication with the registrar
	 * and to enable the Broker to communicate with other objects.
	 */
	public void init() {
	
		/* send ObjectHello to registrar, the rest of the communication will be handled in the CommunicationService */
		Message m = new Message(0x80000000,0x80000000,0x80000000);
		this.send(m);
		
	}
	
	/**
	 * The method stores a callback object at the Broker, it will get informed about
	 * received Messages and in case of joining or leaving objects.
	 * @param bcb BrokerCallBack object
	 */
	public void registerCallBack(BrokerCallBack bcb) {
	
		bcb.setBroker(this);
		this.indexService.registerBrokerCallBack(bcb);
	
	}
	
	/**
	 * The method sends the Message to it's receiver, the destination will be determined by the
	 * objectID field in the Message header.
	 * @param m, Message that shall be send
	 */
	public void send(Message m) {
	
		if ( this.routService != null )
			this.routService.sapUpperLayer(m);
	
	}
	
	/**
	 * The method sends the Message to all available receiver objects, this is used to inform
	 * all objects about changes in one object, therefore the objectID shall contain the value
	 * of the object that changed.
	 * @param m, Message that shall be broadcasted
	 */	
	public void sendBroadcast(Message m) {
	
		if ( this.routService != null ) {
		
			m.setRequestID(0x7FFFFFFF);
			this.routService.sapUpperLayer(m);
			
		}
	
	}
	
	/**
	 * The method sets the authentication data needed to authenticate against the registrar.
	 * @param username name of the user
	 * @param password user password
	 */
	public void setAuthenticationData(String username, String password) {
	
		this.indexService.setAuthenticationData(username,password);
	
	}
	
}
