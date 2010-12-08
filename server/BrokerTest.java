import broker.service.index.*;
import broker.service.com.*;
import broker.service.com.net.*;
import broker.service.com.session.*;
import broker.service.com.protocol.*;
import broker.*;
import java.net.*;


public class BrokerTest {

	public static void main(String[] args) {
		
		/* this needs to be done */
		Broker b = Broker.getInstance();
		b.registerCallBack(new GameServerTest());
		b.setAuthenticationData("AztecServer", "test");
		b.init();
																																												
	}

}
