package broker.service;

/**
 * Wrapper class for the BrokerService interface.
 */
public abstract class BrokerServiceWrapper implements BrokerService {

	/**
	 * next service means the service on above this. Assumning layered view.
	 */
	protected BrokerService nextService;

	/**
	 * next service means the service below this. Assumning layered view.
	 */	
	protected BrokerService previousService;


	public void setNextService(BrokerService bs) {
	
		this.nextService = bs;
		this.nextService.assignPreviousService(this);
	
	}
	
	public void assignPreviousService(BrokerService bs) {
	
		this.previousService = bs;		
	
	}
		
	public BrokerService getNextService() {
	
		return this.nextService;
	
	}
	
	public BrokerService getPreviousService() {
	
		return this.previousService;	
	
	}
	
	public Object sapUpperLayer(Object c) {return null;}

	public Object sapLowerLayer(Object c) {return null;}
	
	public void crownCallBack() {}

}
