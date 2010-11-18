package broker.service.com;

import broker.service.*;

/**
 * 
 */
public class CommunicationService extends BrokerServiceWrapper {

	public Object sapUpperLayer(Object c) {
	
		return null;
	
	}

	public Object sapLowerLayer(Object c) {
	
		System.out.println("[CommunicationService] sapLowerLayer()");
	
		Object r = null;
	
		if ( this.nextService != null )
			r = this.nextService.sapLowerLayer(c);
	
		return r;
	
	}


}
