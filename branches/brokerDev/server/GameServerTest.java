import broker.*;
import broker.object.*;
import broker.service.com.protocol.*;

public class GameServerTest extends BrokerCallBack {

	public Message receive(Message request, Message response) {
	
		System.out.println("[GameServerTest] receive() - Message with "+request.getFieldNumber()+" field(s) received");

		return null;
	
	}
	
	public void objectLeft(int id) {
	
		System.out.println("[GameServerTest] objectLeft() - id="+id);
	
	}

	public void objectJoined(int id) {

		System.out.println("[GameServerTest] objectJoined() - id="+id);
		
		Message m = new Message(id);
		Broker.getInstance().send(m);
	
	}
	
}
