package broker.service.com.protocol;

import java.util.*;

/**
 * 
 */
public class StringField extends MessageField {	

	public StringField() {
	
		this.type	= MessageField.TYPE_STRING;	
	
	}

	public StringField(String name, String value) {
	
		this();
	
		//if ( name.length() > 127 )
//			throw new Exception("[StringField] Error! The field name is longer then 127 character!");
	
		this.name	= name;
		this.value	= value;
		this.lengthInByte = value.getBytes().length;
		
	}

	public int generateBERData(byte[] data, int offset) {
	
		byte[] name = this.name.getBytes();
		int length = this.getBERDataSize();
		int bytesForLength = length - (4 + name.length + this.lengthInByte);
		int nextIndex = offset;
		
		
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
			
			/* small length field only 1 Byte, length in byte between 0 - 127 */
			if ( this.lengthInByte < 128 ) {
			
				data[++nextIndex]	= (byte)(this.lengthInByte&0x7F);
				
			}
			
			/* long length, first field bit8=1, bit7-1=numberOfBytesForLength, subsequent bytes contain the length */
			else {
				
				/* store number of bytes for the length field */
				data[++nextIndex]	= (byte)((bytesForLength&0x7F) | 0x80);
				
				ByteConversion.longToByte(this.lengthInByte, bytesForLength, data, ++nextIndex);
				nextIndex += bytesForLength-1;
				
			}

			/* copy contents */
			byte[] bvalue = ((String)this.value).getBytes();
			ByteConversion.copyBytes(bvalue, 0, data, ++nextIndex, bvalue.length);
	
		return nextIndex + bvalue.length;
	
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

			if ( data[offset] == MessageField.TYPE_STRING ) {
			
				/* small length */
				length = data[offset+1];
				
				/* long length */
				if ( (length&0x80) != 0 ) {
				
					int bytes = length&0x7F;
					length = (int)ByteConversion.byteToLong(data, offset+2, bytes);
					offset = offset+bytes;
					
					//System.out.println(String.format("length=0x%X, bytes=%d", length, bytes));
					
				}
								
				byte[] bvalue = new byte[length];
				ByteConversion.copyBytes(data, offset+2, bvalue, 0, length);
				offset = offset+2+length;
				
				this.value = new String(bvalue);
			
				//System.out.println("[StringField] parseBERData() - name="+this.name + "; value="+this.value);
			
			}
			
		}
	
		/* return next position */
		return offset;
	
	}
	
	public int getBERDataSize() {
	
		int bytesForLength = 0;
	
		/* ceil( log(lengthInByte)/log(256) ) */
		if ( this.lengthInByte > 127 )
			bytesForLength = (int)Math.ceil((Math.log(this.lengthInByte)/Math.log(256)));
		
		return 4 + bytesForLength + name.length() + this.lengthInByte;
	
	}
	
}
