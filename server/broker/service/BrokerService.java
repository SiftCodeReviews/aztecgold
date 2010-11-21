package broker.service;

/**
 * The Interface provides the basic service methods. Services are
 * structured in a layer architecture and can be combined to a stack
 * of service, in order to provide special functions. Stacking the
 * services is done through double linking, this means, each layer
 * knows if there is a upper or lower layer existing. Stacking is done
 * from the bottom to the top.
 */
public interface BrokerService {

	/**
	 * The method adds the next upper service layer.
	 * @param bs BrokerService on layer above
	 */
	public void setNextService(BrokerService bs);
	
	/**
	 * The method will be inherently called by lower layer service, in case
	 * there will be the setNextService() method be called.
	 * @param bs BrokerService on layer below
	 */
	public void assignPreviousService(BrokerService bs);
	
	/**
	 * The method represents the Service Access Point from the upper layer.
	 * @param c generic input object
	 */
	public Object sapUpperLayer(Object c);

	/**
	 * The method represents the Service Access Point from the lower layer.
	 * @param c generic input object
	 */
	public Object sapLowerLayer(Object c);
	
	public BrokerService getNextService();
	
	public BrokerService getPreviousService();

}
