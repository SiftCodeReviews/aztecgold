package broker.service.com.protocol;

import java.util.*;

/**
 * 
 */
public class LongField extends MessageField {	

	public LongField() {
	
		this.type	= MessageField.TYPE_LONG;	
	
	}

	public LongField(String name, Long value) {
	
		this();
		this.name	= name;
		this.value	= value;
		this.lengthInByte = 8;
		
	}

	public int generateBERData(byte[] data, int offset) {

		int nextIndex = offset;	
		byte[] name = this.name.getBytes();
		
		/* 
		 * 1. TLV for FID 
		 */
			data[nextIndex]		= MessageField.TYPE_STRING;
			data[++nextIndex]	= (byte)(name.length&0x7F);
			nextIndex			= ByteConversion.copyBytes(name, 0, data, ++nextIndex, name.length);
	
		/* 
		 * 2. TLV for Value 
		 */
			data[nextIndex]		= this.type;
			
			/* store number of bytes for the length field */
			data[++nextIndex]	= (byte)this.lengthInByte;
			
			/* copy contents */
			ByteConversion.longToByte( ((Long)this.value).longValue(), this.lengthInByte, data, ++nextIndex );
	
		return nextIndex + this.lengthInByte;
	
	}
	
	
	public int parseBERData(byte[] data, int offset) {
		
		if ( data[offset] == MessageField.TYPE_STRING ) {
		
			/* 
			 * 1. TLV for FID
			 */
			int length = data[offset+1];
			byte[] bname	= new byte[length];
			ByteConversion.copyBytes(data, offset+2, bname, 0, length);
			this.name = new String(bname);
			
			/* 
			 * 2. TLV for Value 
			 */
			offset = offset+2+length;

			if ( data[offset] == this.type ) {
			
				length = data[offset+1];				
				long bvalue = ByteConversion.byteToLong(data, offset+2, length);
				offset = offset+2+length;
				
				this.value = new Long(bvalue);
			
				//System.out.println("[IntegerField] parseBERData() - name="+this.name + "; value="+this.value);
			
			}
			
		}
	
		/* return next position */
		return offset;
	
	}
	
	public int getBERDataSize() {
	
		return 12 + name.length();
	
	}
	
}
