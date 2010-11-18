package broker.service.com.net;

public class ByteSequence {

	private byte[] sequence;
	
	private int length;
	
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
			
}
