package broker;

import broker.service.com.*;
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
		this.netService.createSocket();
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
	
}
