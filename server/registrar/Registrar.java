package registrar;

import broker.object.BrokerCallBack;
import broker.service.com.*;
import broker.service.com.protocol.*;
import broker.service.index.*;
import broker.service.user.*;
import broker.service.com.session.*;

/**
 * Singleton wrapper facade for the different BrokerServices.
 */
public class Registrar {

	/**
	 * static singleton instance
	 */
	private static Registrar instance = null;
	
	/**
	 * The NetworkService used by the Broker
	 */
	private NetworkService netService = null;
	
	/**
	 * The ProtocolService used by the Broker
	 */
	private ProtocolService proService = null;

	/**
	 * The SessionService used by the Broker
	 */
	private SessionService sessionService = null;
	
	/**
	 * The UserService used by the Broker
	 */
	private UserService userService = null;
	
	/**
	 * The IndexService used by the Broker
	 */
	private IndexService indexService = null;
	

	
	/** 
	 * Private constructor, to provide singleton object
	 */
	private Registrar() {

		/* IndexService */
		this.indexService = IndexService.getInstance();
	
		/* User Service */
		this.userService = UserService.getInstance();
	
		/* Session Service */
		this.sessionService = new SessionService(this.indexService);
		this.sessionService.setNextService(this.userService);

		/* Protocol Service */
		this.proService = new ProtocolService();
		this.proService.setNextService(this.sessionService);

		/* NetworkService */
		this.netService = new NetworkService(this.indexService, IndexService.REGISTRAR_PORT, true);
		this.netService.setNextService(this.proService);
		
	}
	
	/**
	 * This method is a synchronized way to create or access a singleton instance of Broker.
	 * @return singleton instance of Broker
	 */
	public synchronized static Registrar getInstance() {
	
		if ( Registrar.instance == null )
			Registrar.instance = new Registrar();
	
		return Registrar.instance;
	
	}
		
}
