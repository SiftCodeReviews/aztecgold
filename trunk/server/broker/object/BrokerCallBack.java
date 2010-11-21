package broker.object;

import broker.service.com.protocol.Message;
import broker.*;

/**
 * This abstract shall be used by any class that shall be registered for 
 * Message callbacks from the Broker.
 */
public abstract class BrokerCallBack {

	private Broker broker = null;

	public void setBroker(Broker b) {
		
		this.broker = b;
	
	}
	
	protected Broker getBroker() {
		
		return this.broker;
	
	}

	public abstract void receive(Message m);
	
}
