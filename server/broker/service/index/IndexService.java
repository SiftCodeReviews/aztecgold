package broker.service.index;

import broker.service.*;
import broker.object.*;
import java.util.*;

/**
 * The IndexService is used for broker internal management of Client and Server
 * Objects and their addressing.
 */
public class IndexService extends BrokerServiceWrapper {

	private static IndexService instance = null;
	
	/**
	 * The list stores all registered BrokerCallBacks, they will be
	 * all informed in case of a message.
	 */
	private ArrayList<BrokerCallBack> callBacks;
	
	/**
	 * list of connected distributed objects
	 */
	private Hashtable<Integer,DistObject> objectRepository;
	
	
	
	
	
	private IndexService() {
	
		this.objectRepository	= new Hashtable<Integer,DistObject>();
		this.callBacks			= new ArrayList<BrokerCallBack>();
	
	}
	
	public static IndexService getInstance() {
	
		if ( IndexService.instance == null )
			IndexService.instance = new IndexService();
			
		return IndexService.instance;
	
	}
	
	public void registerBrokerCallBack(BrokerCallBack bcb) {
	
		this.callBacks.add(bcb);
		
	}
	
	public ArrayList<BrokerCallBack> getBrokerCallBacks() {
	
		return this.callBacks;
	
	}
	
	
	public DistObject lookupDistObject(int objectID) {
	
		return this.objectRepository.get(new Integer(objectID));
	
	}
	
	public void registerDistObject(DistObject d) {
	
		this.objectRepository.put(d.getObjectID(), d);
	
	}
	
	public void unregisterDistObject(int objectID) {
	
		this.objectRepository.remove(new Integer(objectID));
	
	}

	
}
