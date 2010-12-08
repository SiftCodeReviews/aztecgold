package broker.service.cron;

import broker.service.*;
import java.util.*;

/**
 * The CronService is a simple service providing periodic callbacks for periodic 
 * operations in other services. It uses a global time interval that is not exactly
 * timed, this means callbacks can occur earlier or later.
 */
public class CronService extends BrokerServiceWrapper implements Runnable {

	/**
	 * singleton instance for this class
	 */
	private static CronService instance = null;
	
	/**
	 * list of BrokerServices that will be periodically invoked
	 */
	private ArrayList<BrokerService> callbacklist;
	
	/**
	 * interval for periodically execution of callbacks in ms
	 */
	private long interval = 500;
	
	
	private CronService() {
	
		this.callbacklist = new ArrayList<BrokerService>();
	
		Thread t = new Thread(this);
		t.start();
	
	}
	
	public static CronService getInstance() {
	
		if ( CronService.instance == null )
			CronService.instance = new CronService();
			
		return CronService.instance;
	
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
					
						this.callbacklist.get(i).cronCallBack();
					
					}
				
				}
				
			}
			catch(InterruptedException e) {}
		
		}
	
	}
	
	
}
