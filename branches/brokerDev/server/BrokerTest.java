import broker.service.index.*;
import broker.service.com.*;
import broker.service.com.net.*;
import broker.*;
import java.net.*;
import broker.service.com.protocol.*;

public class BrokerTest {

	public static void main(String[] args) {
		
		/* hard coded binding of the client, this will be done later automatically */
		IndexService s = IndexService.getInstance();
		try {
			s.registerDistObject( new DistObject(0xFF00FF00, 103884, new InetSocketAddress(InetAddress.getByName((args.length>0)?args[0]:"127.0.0.1"), 2040)) );
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		
		
		/* this needs to be done */
		Broker b = Broker.getInstance();
		b.registerCallBack(new GameServerTest());
		b.init();
		
		
		
		/* demo for sending a message */
		Message m = new Message(0xFF00FF00,0x11EE11EE,103884);
		m.setString("mid","moveReq");
		m.setString("long text","abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopqrstuvwxyz0123456789");
		m.setDouble("double 10.321",10.321);
		m.setDouble("double -201.2",-201.2);
		m.setInteger("int -10",-10);
		m.setInteger("int 2018",2018);
		m.setLong("long 3123128",3123128);
		m.setLong("long -3123128",-3123128);
		m.setByte("byte 123",(byte)123);
		m.setByte("byte -23",(byte)-23);
		m.setShort("byte 2012",(short)2012);
		m.setShort("byte -32000",(short)-32000);
		m.setBoolean("boolean true",true);
		m.setBoolean("boolean false",false);
		b.send(m);
																																																															
	}

}
