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
	 * TEMPORARY SOLUTION, until the indexservice is working
	 */
	private BrokerCallBack callBackObject;


	
	/** 
	 * Private constructor, to provide singleton object
	 */
	private Broker() {

		/* Message Router Service */
		this.routService = new MessageRouterService();

		/* Communication Service */
		this.comService = new CommunicationService();
		this.comService.setNextService(this.routService);

		/* Protocol Service */
		this.proService = new ProtocolService();
		this.proService.setNextService(this.comService);

		/* NetworkService */
		this.netService = new NetworkService();
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
	
		this.netService.createSocket();
	
	
	}
	
	/**
	 * The method stores a callback object at the Broker, it will get informed about
	 * received Messages and in case of joining or leaving objects.
	 * @param bcb BrokerCallBack object
	 */
	public void registerCallBack(BrokerCallBack bcb) {
	
		bcb.setBroker(this);
		this.callBackObject = bcb;
	
	}
	
	/**
	 * The method sends the Message to it's receiver, the destination will be determined by the
	 * objectID field in the Message header.
	 * @param m, Message that shall be send
	 */
	public void send(Message m) {
	
	
	}
	
	/**
	 * The method sends the Message to all available receiver objects, this is used to inform
	 * all objects about changes in one object, therefore the objectID shall contain the value
	 * of the object that changed.
	 * @param m, Message that shall be broadcasted
	 */	
	public void sendBroadcast(Message m) {
	
	
	}
	
}
