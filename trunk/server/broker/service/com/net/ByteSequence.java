package broker.service.com.net;

import broker.service.com.protocol.*;

public class ByteSequence {

	private byte[] sequence;
	
	private int length;
	
	private int objectID = -1;
	
	private int requestID = -1;
	
	public ByteSequence(byte[] sequence, int length) {
	
		this.sequence	= sequence;
		this.length		= length;
	
	}
	
	public int getLength() {
		return this.length;
	}
	
	public byte[] getSequence() {
		return this.sequence;
	}
	
	public void setObjectID(int objectID) {
	
		this.objectID = objectID;
	
	}
	
	public int getObjectID() {
	
		return this.objectID;
	
	}
	
	public void setRequestID(int requestID) {
	
		this.requestID = requestID;
	
	}
	
	public int getRequestID() {
	
		return this.requestID;
	
	}
			
}
