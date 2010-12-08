package broker.service.user;

import broker.service.com.session.*;


/**
 * This class represents user associated runtime data, this includes the active session.
 */
public class UserData {

	/**
	 * user name
	 */
	private String username;
	
	/**
	 * user password
	 */
	private String password;
	
	/**
	 * unique objectID
	 */
	private int objectID;
	
	/**
	 * Associated SessionObject, can be null in case there is no active session
	 */
	private SessionObject session = null;
	
	/**
	 * indicates wether the user can be looked up and his address will be returned
	 */
	private boolean publicAccess = false;
	
	
	
	public UserData(String username, String password, int objectID) {
	
		this.username	= username;
		this.password	= password;
		this.objectID	= objectID;
	
	}
	
	public UserData(String username, String password, int objectID, boolean publicAccess) {
	
		this(username, password, objectID);
		this.publicAccess = publicAccess;
	
	}
	
	public synchronized void assignSessionObject(SessionObject s) {
		
		this.session = s;
		this.session.setObjectID(this.objectID);
	
	}
	
	public synchronized void unassignSessionObject() {
	
		this.session = null;
	
	}
	
	public boolean hasActiveSession() {
	
		//System.out.println("[UserData] hasActiveSession() - " + this.session);
	
		if ( this.session != null ) 
			return true;
		else
			return false;
	
	}
	
	public String getUsername() {
	
		return this.username;
	
	}
	
	public String getPassword() {
	
		return this.password;
	
	}
	
	public int getObjectID() {
	
		return this.objectID;
	
	}
	
	public boolean isPublicAccessible() {
	
		return this.publicAccess;
	
	}
	
	public SessionObject getSession() {
	
		return this.session;
	
	}
	
}
