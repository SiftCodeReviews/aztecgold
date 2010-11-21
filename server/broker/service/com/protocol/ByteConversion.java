package broker.service.com.protocol;

/**
 * 
 */
public class ByteConversion {

	public static int toInteger(byte[] b, int offset, int length) {
	
		int result = 0;
	
		for(int i=0; i < length; i++) {
				
			result = (result<<8);
			result = (result|(b[offset+i]&0xFF));
		
		}
		
		return result;
	
	}

	public static int copyBytes(byte[] from, int fromOffset, byte[] to, int toOffset, int length) {
	
		int done = -1;
	
		//if ( from.length <= (to.length-toOffset) ) {
		
			for(int i=0; i < length; i++) {
			
				to[toOffset+i] = from[i+fromOffset];
			
			}
			
			done = toOffset+from.length;
		
		//}
	
		return done;
	
	}
	
	public static byte[] longToByte(long l, byte[] to, int toOffset) {
	
		byte[] v = to;
		
		if ( v == null )
			v = new byte[8];
		
		for(int i=0; i < v.length; i++) {
		
			v[i+toOffset] = (byte)((l>>>(i*8))&0xFF);
		
		}
		
		return v;
	
	}
	
}

