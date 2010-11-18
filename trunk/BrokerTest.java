import broker.service.com.*;
import broker.service.com.net.*;
import broker.*;
import java.net.*;
import broker.service.com.protocol.*;

public class BrokerTest {

	public static void main(String[] args) {
	
		//new NetworkService();
		Message m = new Message();
		m.addString("name","Peter");
		
		
		
		if ( args[0].equals("server") ) {
			
			Broker b = Broker.getInstance();

		}
		
		else {
		
			try {
			
				DatagramSocket cs = new DatagramSocket(2038);
				DatagramPacket dp = new DatagramPacket(new byte[1024], 1024, InetAddress.getLocalHost(), 2034);
							
				byte[] sendM = {/* protocol id */ (byte)0x44,(byte)0x50, /* flags */ (byte)0xF8, /* padding */ (byte)0x0, /* session id */(byte)0x7F,(byte)0xFF,(byte)0xFF,(byte)0xFF, /* object id */(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0, /* hash */(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x0, /*Data: name=peter*/(byte)0x21,(byte)0x4,(byte)0x6E,(byte)0x61,(byte)0x6D,(byte)0x65,(byte)0x21,(byte)0x5,(byte)0x50,(byte)0x65,(byte)0x74,(byte)0x65,(byte)0x72, /* data2 */ (byte)0x21,(byte)0x5,(byte)'l',(byte)0x6E,(byte)0x61,(byte)0x6D,(byte)0x65,(byte)0x21,(byte)0x81,(byte)136,(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r'};
				dp.setData(sendM);
				cs.send(dp);
				
				dp = new DatagramPacket(new byte[1024], 1024);
				cs.receive(dp);
				
				System.out.println("[Client] Response from Server: " + new String(dp.getData()));
				
			}
			catch(Exception e) {
			
				e.printStackTrace();
			
			}
		
		}
																																																															
	}

}
