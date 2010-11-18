package broker.service.com.protocol;

import java.util.*;

/**
 * Superclass for all different kind of fields in a message, they differ mainly between their datatype.
 * In addition the MessageField offers functions to convert to or from ASN.1 BER encoded byte sequences.
 * This can be utilized when a message leaves the object oriented world into a byte oriented, like sockets
 * are.
 */
public abstract class MessageField {

	public static final byte TYPE_PREBYTE	= 0x20;
	public static final byte TYPE_STRING	= TYPE_PREBYTE | 0x01;
	public static final byte TYPE_INTEGER	= TYPE_PREBYTE | 0x02;
	public static final byte TYPE_DOUBLE	= TYPE_PREBYTE | 0x03;
	public static final byte TYPE_BYTE		= TYPE_PREBYTE | 0x04;
	public static final byte TYPE_SHORT		= TYPE_PREBYTE | 0x05;
	public static final byte TYPE_OID		= TYPE_PREBYTE | 0x06;
	public static final byte TYPE_LONG		= TYPE_PREBYTE | 0x07;
	
	/**
	 * name or field id
	 */
	protected String name;
	
	/** 
	 * field value
	 */
	protected Object value;
	
	/**
	 * length of the field value in bytes, this is for datatypes like INTEGER a fixed value
	 */
	protected int lengthInByte;
	
	/**
	 * numerical encoded type following the ASN.1 coding
	 */
	protected byte type;
	
	/**
	 * The method generates a BER encoded byte sequence using the data from the current object.
	 * One object is represented as a tupel of field id, which is the variable name and the value.
	 * Both are ASN1 BER encoded that means they are existing as TLV values.
	 * @see http://luca.ntop.org/Teaching/Appunti/asn1.html
	 */
	public abstract byte[] generateBERData();
	
	/**
	 * The method parses the given byte array for the needed data to fill the object.
	 * It returns the absolute value of the next field after it's contents within data.
	 * @param data byte array with BER encoded tupeln, FID (Field ID) and Value
	 * @return absolute value of the next field after the data read for the current object
	 * @see http://luca.ntop.org/Teaching/Appunti/asn1.html
	 */
	public abstract int parseBERData(byte[] data, int offset);
	
	public Object getValue() {
	
		return this.value;
	
	}
	
	public byte getType() {
	
		return this.type;
	
	}
	
	public String getName() {
	
		return this.name;
	
	}

	
	public Class getTypeAsClass() {
	
		Class res = null;
		
		switch(this.type) {
			case TYPE_STRING:	res = String.class;		break;
			case TYPE_INTEGER:	res = Integer.class;	break;
			case TYPE_DOUBLE:	res = Double.class;		break;
			case TYPE_BYTE:		res = Byte.class;		break;
			case TYPE_SHORT:	res = Short.class;		break;
			case TYPE_OID:		res = Integer.class;	break;
			case TYPE_LONG:		res = Long.class;		break;
		}
		
		return res;
	
	}
	
	
	
	
	/**
	 * The method can be used to parse a byte sequence with a set of BER encoded MessageFields and
	 * it returns them as MessageFields in a map using the field id or name as key.
	 * @param m byte sequence to parse
	 * @param offset offset value within the sequence, used to skip header information for example
	 * @param actual length of the data, this can differ from the length of the array m
	 * @return map String to MessageField
	 */
	public static Hashtable<String,MessageField> parseBERData(byte[] m, int offset, int length) {
	
		Hashtable<String,MessageField> data = new Hashtable<String,MessageField>();
		MessageField res;
		String name;
		int tlv_type		= 0;
		int tlv_length		= 0;
		int tupelStart		= offset;
		
		for(int i=offset; i < length; ) {
			
			res				= null;
			tlv_type		= m[tupelStart];
			tlv_length		= m[tupelStart+1];
			tlv_type		= m[tupelStart + 2 + tlv_length];
			
			//System.out.println(String.format("[MessageField] parseBERData() - type: 0x%X", tlv_type));

			switch(tlv_type) {
				case TYPE_STRING:	res = new StringField();		
									tupelStart = res.parseBERData(m, tupelStart);
									break;
				/*
				case TYPE_INTEGER:	res = Integer.class;	break;
				case TYPE_DOUBLE:	res = Double.class;		break;
				case TYPE_BYTE:		res = Byte.class;		break;
				case TYPE_SHORT:	res = Short.class;		break;
				case TYPE_OID:		res = Integer.class;	break;
				case TYPE_LONG:		res = Long.class;		break;
				*/
			}
			
			//System.out.println("[MessageField] parseBERData() - tupelStart="+tupelStart+"; length="+length);
			
			if ( res != null )
				data.put(res.getName(), res);
			
			if ( tupelStart >= length ) {
				//System.out.println("[MessageField] parseBERData() - Reached end!");
				break;	
			}
			
		}
	
		System.out.println("[MessageField] parseBERData() - returning " + data.size() +" MessageField object(s)");
	
		return data;
	
	}
	
}
