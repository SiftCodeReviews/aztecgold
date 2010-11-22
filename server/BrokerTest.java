import broker.service.com.*;
import broker.service.com.net.*;
import broker.*;
import java.net.*;
import broker.service.com.protocol.*;

public class BrokerTest {

	public static void main(String[] args) {
		
		String mode = "client";
				
		if ( args.length > 0 )
			mode = args[0];
		
		if ( mode.equals("server") ) {
			
			Broker b = Broker.getInstance();
			b.init();

		}
		
		else {
		
			try {
			
				DatagramSocket cs = new DatagramSocket(2038);
				DatagramPacket dp = new DatagramPacket(new byte[1024], 1024, InetAddress.getByName(args[1]), 2040);
							
				byte[] sendM = {/* protocol id */ (byte)0x44,(byte)0x50, /* flags */ (byte)0xFE, /* padding */ (byte)0x0, /* session id */(byte)0x7F,(byte)0xFF,(byte)0xFF,(byte)0xFF, /* request id */(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x1, /* object id */(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x2, /* hash */(byte)0x0,(byte)0x0,(byte)0x0,(byte)0x3, /*Data: name=peter*/(byte)0x21,(byte)0x4,(byte)0x6E,(byte)0x61,(byte)0x6D,(byte)0x65,(byte)0x21,(byte)0x5,(byte)0x50,(byte)0x65,(byte)0x74,(byte)0x65,(byte)0x72, /* data2 */ (byte)0x21,(byte)0x5,(byte)'l',(byte)0x6E,(byte)0x61,(byte)0x6D,(byte)0x65,(byte)0x21,(byte)0x81,(byte)136,(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',(byte)'S',(byte)'c',(byte)'h',(byte)'a',(byte)'e',(byte)'f',(byte)'e',(byte)'r',/* data3 double */ (byte)0x21,(byte)0x4,(byte)'d',(byte)'o',(byte)'u',(byte)'b',(byte)0x23,(byte)0x8,(byte)0x40,(byte)0x32,(byte)0x66,(byte)0x66,(byte)0x66,(byte)0x66,(byte)0x66,(byte)0x65, /* byte */(byte)0x21,(byte)0x1,(byte)'x',(byte)0x24,(byte)0x01,(byte)0x80};
				dp.setData(sendM);
				cs.send(dp);
				
				dp = new DatagramPacket(new byte[1024], 1024);
				cs.receive(dp);
				
				System.out.println("[Client] Response from Server @ " + dp.getAddress() + " ("+dp.getLength()+" Bytes): " + new String(dp.getData()));
				
			}
			catch(Exception e) {
			
				e.printStackTrace();
			
			}
		
		}
																																																															
	}

}
