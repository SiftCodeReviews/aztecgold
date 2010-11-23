import broker.*;
import broker.object.*;
import broker.service.com.protocol.*;

public class GameServerTest extends BrokerCallBack {

	public Message receive(Message request, Message response) {
	
		System.out.println("[GameServerTest] receive() - Message with "+request.getFieldNumber()+" field(s) received");
		System.out.println(request);
		
		return null;
	
	}
	
	public void objectLeft(int id) {}
	public void objectJoined(int id) {}
	
}
