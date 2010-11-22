package broker.service.com.protocol;

import java.util.*;

/**
 * 
 */
public class Message {
	
	private int objectID	= 0;
	
	private int sessionID	= 0;

	private int requestID	= 0;
	
	private Hashtable<String,MessageField> data;
	
	
	public Message() {
	
		this.data	= new Hashtable<String,MessageField>();
	
	}
	
	public Message(int objectID, int sessionID) {
	
		this.data	= new Hashtable<String,MessageField>();
	
	}
	
	public void setString(String name, String value) {
	
		StringField s = new StringField(name,value);
		this.data.put(name,s);
			
	}	
	
	public void setLong(String name, long value) {
	
		LongField s = new LongField(name,value);
		this.data.put(name,s);
			
	}	
	
	public void setInteger(String name, int value) {
	
		IntegerField s = new IntegerField(name,value);
		this.data.put(name,s);
			
	}	
	
	public void setShort(String name, short value) {
	
		ShortField s = new ShortField(name,value);
		this.data.put(name,s);
			
	}
	
	public void setByte(String name, byte value) {
	
		ByteField s = new ByteField(name,value);
		this.data.put(name,s);
			
	}
	
	public void setBoolean(String name, boolean value) {
	
		BooleanField s = new BooleanField(name,value);
		this.data.put(name,s);
			
	}	
	
	public void setDouble(String name, double value) {
	
		DoubleField s = new DoubleField(name,value);
		this.data.put(name,s);
			
	}	

	
	public String getString(String name) {
	
		MessageField s = this.data.get(name);
		
		if ( s == null || s.getType() == MessageField.TYPE_STRING ) {
		
			return (String)s.getValue();
		
		}
		
		throw new RuntimeException(String.format("MessageField \"%s\" type String not found.", name));
	
	}
	
	public Long getLong(String name) {
	
		MessageField s = this.data.get(name);
		
		if ( s == null || s.getType() == MessageField.TYPE_LONG ) {
			
			return ((Long)s.getValue()).longValue();
		
		}
		
		throw new RuntimeException(String.format("MessageField \"%s\" type long not found.", name));
	
	}
	
	public int getInteger(String name) {
	
		MessageField s = this.data.get(name);
		
		if ( s == null || s.getType() == MessageField.TYPE_INTEGER ) {
			
			return ((Integer)s.getValue()).intValue();
		
		}
		
		throw new RuntimeException(String.format("MessageField \"%s\" type integer not found.", name));
	
	}
	
	public short getShort(String name) {
	
		MessageField s = this.data.get(name);
		
		if ( s == null || s.getType() == MessageField.TYPE_SHORT ) {
			
			return ((Short)s.getValue()).shortValue();
		
		}
		
		throw new RuntimeException(String.format("MessageField \"%s\" type short not found.", name));
	
	}
	
	public byte getByte(String name) {
	
		MessageField s = this.data.get(name);
		
		if ( s == null || s.getType() == MessageField.TYPE_BYTE ) {
			
			return ((Byte)s.getValue()).byteValue();
		
		}
		
		throw new RuntimeException(String.format("MessageField \"%s\" type byte not found.", name));
	
	}
	
	public boolean getBoolean(String name) {
	
		MessageField s = this.data.get(name);
		
		if ( s == null || s.getType() == MessageField.TYPE_BOOLEAN ) {
			
			return ((Boolean)s.getValue()).booleanValue();
		
		}
		
		throw new RuntimeException(String.format("MessageField \"%s\" type boolean not found.", name));
	
	}
	
	public double getDouble(String name) {
	
		MessageField s = this.data.get(name);
		
		if ( s == null || s.getType() == MessageField.TYPE_DOUBLE ) {
			
			return ((Double)s.getValue()).doubleValue();
		
		}
		
		throw new RuntimeException(String.format("MessageField \"%s\" type double not found.", name));
	
	}
	
	
	void setData(Hashtable<String,MessageField> data) {
	
		this.data = data;
	
	}
	
	public int getFieldNumber() {
	
		return this.data.size();
	
	}
	
}
