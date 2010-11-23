package broker.service.index;

import broker.object.*;
import broker.service.*;
import java.net.*;

/**
 * 
 */
public class DistObject {

	private Integer objectID	= new Integer(-1);
	
	private int sessionID		= -1;
	
	private SocketAddress addr	= null;
	
	public DistObject(int sessionID, int objectID, SocketAddress addr) {
	
		this.addr		= addr;
		this.sessionID	= sessionID;
		this.objectID	= new Integer(objectID);
	
	}
	
	public SocketAddress getAddr() {
	
		return this.addr;
	
	}
	
	public Integer getObjectID() {
	
		return this.objectID;
	
	}
	
}
