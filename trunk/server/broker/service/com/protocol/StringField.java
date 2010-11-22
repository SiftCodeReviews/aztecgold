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

	public byte[] generateBERData() {
	
		byte[] name = this.name.getBytes();
		int bytesForLength = 0;
		
		/* ceil( log2(lengthInByte)/8 ) */
		if ( this.lengthInByte > 127 )
			bytesForLength = (int)Math.ceil((Math.log(this.lengthInByte)/0.301029995) / 8);
				
		int length = 4 + bytesForLength + name.length + this.lengthInByte;
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
			
			/* small length field only 1 Byte, length in byte between 0 - 127 */
			if ( this.lengthInByte < 128 ) {
			
				res[++nextIndex]	= (byte)(this.lengthInByte&0x7F);
				
			}
			
			/* long length, first field bit8=1, bit7-1=numberOfBytesForLength, subsequent bytes contain the length */
			else {
				
				/* store number of bytes for the length field */
				res[++nextIndex]	= (byte)(bytesForLength&0x7F);
				
				/* insert the length in the subsequent fields */
				for(int i=1; i <= bytesForLength; i++) {
				
					res[++nextIndex] = (byte)((bytesForLength >> ((bytesForLength-i)*8)) & 0xFF);
				
				}
				
			}

			/* copy contents */
			byte[] bvalue = ((String)this.value).getBytes();
			ByteConversion.copyBytes(bvalue, 0, res, ++nextIndex, bvalue.length);
	
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
	
}
