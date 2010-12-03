package broker.service.com.net;

import broker.service.com.protocol.*;

public class ByteSequence {

	private byte[] sequence;
	
	private int length;
	
	private Message m = null;
	
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
	
	public void setMessage(Message m) {
	
		this.m = m;
	
	}
	
	public int getObjectID() {
	
		return this.m.getObjectID();
	
	}
			
}
