package broker.service.crown;

import broker.service.*;
import java.util.*;

/**
 * The CrownService is a simple service providing periodic callbacks for periodic 
 * operations in other services. It uses a global time interval that is not exactly
 * timed, this means callbacks can occur earlier or later.
 */
public class CrownService extends BrokerServiceWrapper implements Runnable {

	/**
	 * singleton instance for this class
	 */
	private static CrownService instance = null;
	
	/**
	 * list of BrokerServices that will be periodically invoked
	 */
	private ArrayList<BrokerService> callbacklist;
	
	/**
	 * interval for periodically execution of callbacks in ms
	 */
	private long interval = 500;
	
	
	private CrownService() {
	
		this.callbacklist = new ArrayList<BrokerService>();
	
		Thread t = new Thread(this);
		t.start();
	
	}
	
	public static CrownService getInstance() {
	
		if ( CrownService.instance == null )
			CrownService.instance = new CrownService();
			
		return CrownService.instance;
	
	}
	
	public synchronized void registerService(BrokerService s) {
	
		this.callbacklist.add(s);
	
	}
	
	public void run() {
	
		while(true) {
		
			try {
		
				Thread.sleep(this.interval);
				
				synchronized(this) {
				
					for(int i=0; i < this.callbacklist.size(); i++) {
					
						this.callbacklist.get(i).crownCallBack();
					
					}
				
				}
				
			}
			catch(InterruptedException e) {}
		
		}
	
	}
	
	
}
