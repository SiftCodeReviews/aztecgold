package broker.service.com.protocol;

import java.util.*;

/**
 * 
 */
public class IntegerField extends MessageField {	

	public IntegerField() {
	
		this.type	= MessageField.TYPE_INTEGER;	
	
	}

	public IntegerField(String name, Integer value) {
	
		this();
		this.name	= name;
		this.value	= value;
		this.lengthInByte = 4;
		
	}

	public int generateBERData(byte[] data, int offset) {
	
		int nextIndex = offset;
		byte[] name = this.name.getBytes();
				
		/* 
		 * 1. TLV for FID 
		 */
			data[offset]		= MessageField.TYPE_STRING;
			data[offset+1]		= (byte)(name.length&0x7F);
			nextIndex			= ByteConversion.copyBytes(name, 0, data, offset+2, name.length);
	
		/* 
		 * 2. TLV for Value 
		 */
			data[nextIndex]	= this.type;
			
			/* store number of bytes for the length field */
			data[++nextIndex]	= (byte)this.lengthInByte;
			
			/* copy contents */
			ByteConversion.longToByte( ((Integer)this.value).intValue(), this.lengthInByte, data, ++nextIndex );
	
		return nextIndex+this.lengthInByte;
	
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

			if ( data[offset] == MessageField.TYPE_INTEGER ) {
			
				length = data[offset+1];				
				int bvalue = (int)ByteConversion.byteToLong(data, offset+2, length);
				offset = offset+2+length;
				
				this.value = new Integer(bvalue);
			
				//System.out.println("[IntegerField] parseBERData() - name="+this.name + "; value="+this.value);
			
			}
			
		}
	
		/* return next position */
		return offset;
	
	}
	
	public int getBERDataSize() {
	
		return 8 + name.length();
	
	}
	
}
