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
	
	private boolean error = false;
	
	
	
	
	public Message() {
	
		this.data	= new Hashtable<String,MessageField>();
	
	}
	
	public Message(Message m) {
	
		this();
		
		this.objectID	= m.objectID;
		this.sessionID	= m.sessionID;
		this.requestID	= m.requestID;
		
	}
	
	public Message(int objectID) {
	
		this();

		this.objectID = objectID;
	
	}

	
	public Message(int sessionID, int requestID, int objectID) {
	
		this();

		this.objectID = objectID;
		this.sessionID = sessionID;
		this.requestID = requestID;
	
	}
	
	public int getObjectID() {
	
		return this.objectID;
	
	}
	
	public int getRequestID() {
	
		return this.requestID;
	
	}
	
	public int getSessionID() {
	
		return this.sessionID;
	
	}
	
	public void setRequestID(int r) {
	
		this.requestID = r;
	
	}

	public void setSessionID(int r) {
	
		this.sessionID = r;
	
	}
	
	public void setObjectID(int r) {
	
		this.sessionID = r;
	
	}



	public void setError() {
	
		this.error = true;
	
	}
	
	public boolean getError() {
	
		return this.error;
	
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
		
		if ( s != null && s.getType() == MessageField.TYPE_STRING ) {
		
				return (String)s.getValue();
		
		}
		
		throw new RuntimeException(String.format("MessageField \"%s\" type String not found.", name));
	
	}
	
	public Long getLong(String name) {
	
		MessageField s = this.data.get(name);
		
		if ( s != null && s.getType() == MessageField.TYPE_LONG ) {
			
			return ((Long)s.getValue()).longValue();
		
		}
		
		throw new RuntimeException(String.format("MessageField \"%s\" type long not found.", name));
	
	}
	
	public int getInteger(String name) {
	
		MessageField s = this.data.get(name);
		
		if ( s != null && s.getType() == MessageField.TYPE_INTEGER ) {
			
			return ((Integer)s.getValue()).intValue();
		
		}
		
		throw new RuntimeException(String.format("MessageField \"%s\" type integer not found.", name));
	
	}
	
	public short getShort(String name) {
	
		MessageField s = this.data.get(name);
		
		if ( s != null && s.getType() == MessageField.TYPE_SHORT ) {
			
			return ((Short)s.getValue()).shortValue();
		
		}
		
		throw new RuntimeException(String.format("MessageField \"%s\" type short not found.", name));
	
	}
	
	public byte getByte(String name) {
	
		MessageField s = this.data.get(name);
		
		if ( s != null && s.getType() == MessageField.TYPE_BYTE ) {
			
			return ((Byte)s.getValue()).byteValue();
		
		}
		
		throw new RuntimeException(String.format("MessageField \"%s\" type byte not found.", name));
	
	}
	
	public boolean getBoolean(String name) {
	
		MessageField s = this.data.get(name);
		
		if ( s != null && s.getType() == MessageField.TYPE_BOOLEAN ) {
			
			return ((Boolean)s.getValue()).booleanValue();
		
		}
		
		throw new RuntimeException(String.format("MessageField \"%s\" type boolean not found.", name));
	
	}
	
	public double getDouble(String name) {
	
		MessageField s = this.data.get(name);
		
		if ( s != null && s.getType() == MessageField.TYPE_DOUBLE ) {
			
			return ((Double)s.getValue()).doubleValue();
		
		}
		
		throw new RuntimeException(String.format("MessageField \"%s\" type double not found.", name));
	
	}
	
	
	void setData(Hashtable<String,MessageField> data) {
	
		this.data = data;
	
	}
	
	Hashtable<String,MessageField> getData() {
	
		return this.data;
	
	}
	
	public int getFieldNumber() {
	
		return this.data.size();
	
	}
	
	public String toString() {
	
		String str = String.format("[Message: HEADER[sessionID=0x%X, requestID=0x%X, objectID=0x%X]; DATA[", this.sessionID, this.requestID, this.objectID);
		
		Enumeration<MessageField> fields = this.data.elements();
		
		while( fields.hasMoreElements() ) {
		
			str += fields.nextElement().toString();
		
		}
		
		str += "]";
	
		return str;
	}
	
}
