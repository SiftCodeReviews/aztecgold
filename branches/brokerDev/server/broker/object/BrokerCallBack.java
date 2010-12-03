package broker.object;

import broker.service.com.protocol.Message;
import broker.*;

/**
 * This abstract class shall be used by any class that needs to be registered for 
 * Message callbacks from the Broker.
 */
public abstract class BrokerCallBack {

	/**
	 * The assigned broker, that will call this object in case of messages
	 */
	private Broker broker = null;

	/**
	 * Asssigns the Broker Reference.
	 * @param b Broker to asign
	 */
	public void setBroker(Broker b) {
		
		this.broker = b;
	
	}
	
	/**
	 * Returns the assigned Broker reference.
	 * @return Broker that was assigned if existing
	 */
	protected Broker getBroker() {
		
		return this.broker;
	
	}

	/**
	 * This method will be invoked in case a new client object contacts the server.
	 * @param id lifetime unique objectID
	 */
	public abstract void objectJoined(int id);
	
	/**
	 * This method will be invoked in case a new client object is not there anymore, this can be
	 * caused by logoff or an unreachable state of the client.
	 * @param id lifetime unique objectID
	 */
	public abstract void objectLeft(int id);

	/**
	 * The method will be invoked in case a message for the server is arrived. The request message from the 
	 * client will be given and a prepared response message, in case the server wants to repsond he can
	 * use the response message, fill in the fields needed and in order to signal that the response shall
	 * be send, it returns it. In case no response shall be send, the method shall return Null. In case 
	 * a broadcast shall be send out the method shall return Null but the response message can be used to
	 * call the Broker method sendBroadcast().
	 *
	 * @param request Message from the client
	 * @param response prepared Message that the server shall use to respond to the client
	 * @return the response Message parameter for a unicast response or Null in all other cases
	 */
	public abstract Message receive(Message request, Message response);
	
}
