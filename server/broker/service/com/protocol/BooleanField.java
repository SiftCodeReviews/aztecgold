package broker.service.com.protocol;

import java.util.*;

/**
 * 
 */
public class BooleanField extends MessageField {	

	public BooleanField() {
	
		this.type	= MessageField.TYPE_BOOLEAN;	
	
	}

	public BooleanField(String name, Boolean value) {
	
		this();
		this.name	= name;
		this.value	= value;
		this.lengthInByte = 1;
		
	}

	public byte[] generateBERData() {
	
		byte[] name = this.name.getBytes();
						
		int length = 5 + name.length;
		byte[] res = new byte[length];
		int nextIndex = 0;
		
		/* 
		 * 1. TLV for FID 
		 */
			res[0]				= MessageField.TYPE_STRING;
			res[1]				= (byte)(name.length&0x7F);
			nextIndex			= ByteConversion.copyBytes(name, 0, res, 2, name.length);
	
		/* 
		 * 2. TLV for Value 
		 */
			res[nextIndex]		= this.type;
			
			/* store number of bytes for the length field */
			res[++nextIndex]	= (byte)this.lengthInByte;

			/* copy contents */
			res[++nextIndex]	= (byte)((((Boolean)this.value).booleanValue()==true)?0xFF:0x00);

		return res;
	
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
				boolean bvalue = (data[offset+2]==(byte)0xFF)?true:false;
				offset = offset+2+length;
				
				this.value = new Boolean(bvalue);
			
				//System.out.println("[IntegerField] parseBERData() - name="+this.name + "; value="+this.value);
			
			}
			
		}
	
		/* return next position */
		return offset;
	
	}
	
}
