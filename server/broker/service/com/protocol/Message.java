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
	
	public void addString(String name, String value) {
	
		StringField s = new StringField(name,value);
		this.data.put(name,s);
			
	}	
	
	public String getString(String name) {
	
		MessageField s = this.data.get(name);
		
		if ( s.getTypeAsClass().equals(String.class) ) {
		
			return (String)s.getValue();
		
		}
		
		return null;
	
	}
	
	void setData(Hashtable<String,MessageField> data) {
	
		this.data = data;
	
	}
	
	public int getFieldNumber() {
	
		return this.data.size();
	
	}
	
}
