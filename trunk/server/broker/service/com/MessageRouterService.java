package broker.service.com;

import java.util.*;
import broker.service.*;
import broker.service.index.*;
import broker.service.com.protocol.*;
import broker.object.*;

/**
 * 
 */
public class MessageRouterService extends BrokerServiceWrapper {

	private IndexService index;
	
	public MessageRouterService(IndexService index) {
	
		this.index = index;
	
	}

	public Object sapUpperLayer(Object c) {

		//System.out.println("[MessageRouterService] sapUpperLayer()");
	
		BrokerService p = this.getPreviousService();
		
		if ( p != null ) {
		
			p.sapUpperLayer(c);
			
		}
	
		return null;
	
	}

	public Object sapLowerLayer(Object c) {
	
		//System.out.println("[MessageRouterService] sapLowerLayer()");
		
		if ( this.index != null && c != null ) {
		
			ArrayList<BrokerCallBack> list = this.index.getBrokerCallBacks();
			Message m = (Message)c;
			Message ret;
			
			for(int i=0; i < list.size(); i++) {
			
				Message res = new Message(m);
				ret = list.get(i).receive(m, res);
				
				if ( ret != null )
					this.sapUpperLayer(ret);
			
			}
		
		}
	
		return null;
	
	}
}
