package broker.service.user;

import broker.service.com.session.*;
import broker.service.com.protocol.*;
import broker.service.*;
import broker.object.*;
import java.security.*;
import java.util.*;
import java.net.*;

/**
 * The UserService is used to manage user related data, this includes active sessions, authentication data...
 * It provides functionality for user lookup and offers a authentication validation through it's lower layer
 * Service Access Point.
 */
public class UserService extends BrokerServiceWrapper {

	/**
	 * Singleton instance
	 */
	private static UserService instance = null;
	
	/**
	 * list of users represented through their UserData object
	 */
	private ArrayList<UserData> userList;
	
	/**
	 * Private default constructor
	 */
	private UserService() {
		
		this.userList = new ArrayList<UserData>();
		
		// TEMPORARY static user
		this.userList.add(new UserData("test1","test", 80));
		this.userList.add(new UserData("test2","test", 82));
		this.userList.add(new UserData("test3","test", 83));
		this.userList.add(new UserData("test4","test", 84));
		this.userList.add(new UserData("test5","test", 85));
		this.userList.add(new UserData("test6","test", 86));
		this.userList.add(new UserData("test7","test", 87));
		this.userList.add(new UserData("test8","test", 88));
		this.userList.add(new UserData("test9","test", 89));
		this.userList.add(new UserData("test10","test", 90));		
		this.userList.add(new UserData("AztecServer","test", 81, true));
		
	}
	
	/**
	 * The method is used to create a Singleton instance. It has to be used instead of any constructor.
	 * @return Singleton UserService object
	 */
	public static UserService getInstance() {
	
		if ( UserService.instance == null )
			UserService.instance = new UserService();
			
		return UserService.instance;
	
	}
	
	/**
	 * The lower layer SAP provides the user authentication, it expects a SessionObject containing a username
	 * and the authResponse of a client/server in general an object.
	 * @param c, SessionObject 
	 * @return it return c in case of successfull authentication or null in any other case.
	 */
	public Object sapLowerLayer(Object c) { 
			
		Object result		= null;
		SessionObject so	= (SessionObject)c;
		String username		= so.getUsername();
		String authResponse = so.getAutResponse();
		
		for(int i=0; i < this.userList.size(); i++) {
		
			UserData d = this.userList.get(i);
			
			/* if username matches a user in the list */
			if ( d.getUsername().equals(username) ) {
			
				String pw = d.getPassword();
				String authResponseCompare = createResponse(username,pw,so.getChallenge());

				/* compare authResponse from client with a self genereated response */
				if ( authResponse.equals(authResponseCompare) && !d.hasActiveSession() ) {
				
					System.out.println("[UserService] sapLowerLayer() - Successfull authenticated (name="+username+", id="+d.getObjectID()+")");
				
					d.assignSessionObject(so);
					result = c;
					
				}
			
			}
			
		}

		if ( result == null )
			System.out.println("[UserService] sapLowerLayer() - Authentication FAILED (name="+username+")");		
	
		return result;
	
	}
	
	/**
	 * The method is used to look up the object id for a user name, this will only work in case the user is publicly accessible.
	 * @param username username to look up
	 * @return object id or -1
	 */
	public int getObjectID(String username) {
	
		UserData d = this.getUser(username);
		int objectID = -1;
	
		if ( d != null )
			objectID = d.getObjectID();
				
		return objectID;
	
	}
	
	/**
	 * The method returns the session associated to a username.
	 * @param username, name of the user who's session shall be returned
	 * @return SessionObject of the user or null
	 */
	public SessionObject getSession(String username) {
	
		UserData d = this.getUser(username);
		SessionObject session = null;
	
		if ( d != null )
			session = d.getSession();
		
		//System.out.println("[UserService] getSession() - username="+username+"; session="+session);
		
		return session;
	
	}
	
	/**
	 * The method returns the session associated to a object id.
	 * @param object id, object id the session is associated to
	 * @return SessionObject of the user or null
	 */
	public SessionObject getSession(int objectID) {
	
		UserData d;
		SessionObject session = null;
	
		for(int i=0; i < this.userList.size(); i++) {
		
			d = this.userList.get(i);
			
			if ( d.getObjectID() == objectID ) {
			
				session = d.getSession();
				break;
			
			}
			
		}
		
		//System.out.println("[UserService] getSession() - username="+username+"; session="+session);
		
		return session;
	
	}

	
	/**
	 * The method returns a UserData object associated with the username.
	 * @param username, name of the user to look up
	 * @return UserData of the user or null in case of a non existing name or a not public user.
	 */
	private UserData getUser(String username) {
	
		UserData d;
		UserData res = null;
	
		for(int i=0; i < this.userList.size(); i++) {
		
			d = this.userList.get(i);
				
			if ( d.getUsername().equals(username) && d.isPublicAccessible() ) {
			
				res = d;
				break;
			
			}
					
		}

		return res;
	
	}
	
	/**
	 * The method removes sessions that are timed out.
	 */
	public synchronized void clean() {
	
		UserData d;
	
		for(int i=0; i < this.userList.size(); i++) {
		
			d = this.userList.get(i);
				
			if ( d.getSession() != null && d.getSession().isTimedout(1500) ) {
			
				d.unassignSessionObject();
				System.out.println("[UserService] clean() - session timed out for (name=" + d.getUsername() + ", id=" + d.getObjectID() + ")");
										
			}
					
		}
	
	}
	
	/**
	 * The method creates a authentication response based on a username, password and a challenge.
	 * @param username, username string
	 * @param password, password string 
	 * @param challenge, random long value provided by the server
	 * @return SHA hashed response or null in case of an error
	 */
	public static String createResponse(String username, String password, long challenge) {

		try {
	
			byte[] challengeArray = ByteConversion.longToByte(challenge, 8, null, 0);
						
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(challengeArray);
			md.update(username.getBytes());
			md.update(password.getBytes());
			String digest = new String(md.digest());
		
			return digest;
		
		}
		catch(NoSuchAlgorithmException e) {
		
			e.printStackTrace();
			return null;
		
		}
	
	}
	
}
