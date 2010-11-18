package broker.service.com;

import broker.service.*;

/**
 * 
 */
public class MessageRouterService extends BrokerServiceWrapper {

	public Object sapUpperLayer(Object c) {
	
		return null;
	
	}

	public Object sapLowerLayer(Object c) {
	
		System.out.println("[MessageRouterService] sapLowerLayer()");
	
		Object r = (new String("Test")).getBytes();
	
		//if ( this.nextService != null )
		//	r = this.nextService.sapLowerLayer(c);
	
		return r;
	
	}
}
