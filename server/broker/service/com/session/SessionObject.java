package broker.service.com.session;

import java.net.*;

/**
 * A SessionObject aggregates all information used for establishing a session. It will be created
 * and managed by the SessionService. In order to authenticate the user it is also passed to
 * the UserService, where the user are managed. 
 */
public class SessionObject {

	/**
	 * number of the next request, needs to be bitwise ored with 0x80000000
	 */
	private int nextRequest = 2;
	
	/**
	 * The challenge created for this session
	 */
	private long challenge = -1;

	/**
	 * The associated client socket
	 */
	private InetSocketAddress socket;

	/**
	 * client username
	 */
	private String username;
	
	/**
	 * client response using the username, password and challenge
	 */
	private String authResponse;
	
	/**
	 * timestamp of the last session update
	 */
	private long timestamp = -1;
	
	/**
	 * The assigned object id, assigment will be done through UserService
	 */
	private int objectID = -1;
	
	/**
	 * Current session id
	 */
	private int sessionID = -1;
	

	/**
	 * Default constructor
	 */
	public SessionObject() {
	
		this.challenge	= (long)(Math.random()*Long.MAX_VALUE); 
		this.sessionID	= (int)(Math.random()*Integer.MAX_VALUE); 
		//this.touch();
			
	}
	
	/**
	 * Default constructor
	 */
	public SessionObject(int objectID, int sessionID, InetSocketAddress isa) {
	
		this.sessionID	= sessionID; 
		this.objectID	= objectID;
		this.socket		= isa;
		this.challenge	= (long)(Math.random()*Long.MAX_VALUE); 
		//this.touch();
			
	}
	
	/**
	 * Default constructor
	 */
	public SessionObject(int objectID, InetSocketAddress isa) {
	
		this.objectID	= objectID;
		this.socket		= isa;
		this.challenge	= (long)(Math.random()*Long.MAX_VALUE); 
		this.sessionID	= (int)(Math.random()*Integer.MAX_VALUE); 
		//this.touch();
			
	}


	/** 
	 * Extended constructor, expecting the socket address associated with this session request.
	 * @param isa, internet socket address, contains IP:Port
	 */
	public SessionObject(InetSocketAddress isa) {
	
		this.challenge	= (long)(Math.random()*Long.MAX_VALUE); 
		this.socket		= isa;
		this.sessionID	= (int)(Math.random()*Integer.MAX_VALUE); 
		//this.touch();
			
	}
	
	/**
	 * The method returns the randomly generated challenge.
	 * @return 64 Bit challenge
	 */
	public long getChallenge() {
	
		return this.challenge;
	
	}
	
	/*
	 * The method compares if the request in the parameter is the expected next request updates
	 * the next expected request numer, the timestamp and return true.
	 * @param request request number
	 * @return returns true in case the session is not timeouted and the request number is expected
	 */
	public boolean nextRequest(int request) {
	
		if ( (0x80000000|this.nextRequest) == request && !this.isTimedout(1000) ) {
		
			this.touch();
			this.nextRequest += 2;
			return true;
			
		}
		else {
		
			return false;
			
		}
	
	}
	
	/**
	 * The method checks if the session is to old, that means it has not been updated within 1 second.
	 * @return it returns true in case the session is to old
	 */
	public boolean isTimedout(int milliseconds) {
		
		//System.out.println(((System.nanoTime()-this.timestamp)/1000000.) + " > " +milliseconds );
		
		if ( this.timestamp > -1 ) {
			
			if ( ((System.nanoTime()-this.timestamp)/1000000.) > milliseconds )
				return true;
			else
				return false;
				
		}
		
		return false;
	
	}
	
	/**
	 * The method sets the user authentication data containing the username and the
	 * authResponse which is sha1(username,password,challenge).
	 * @param username string with the users name
	 * @param authResponse sting with the response to the challenge
	 */
	public void setAuthenticationData(String username, String authResponse) {
	
		this.username		= username;
		this.authResponse	= authResponse;
	
	}
	
	/**
	 * The method returns the clients username;
	 * @param username
	 */
	public String getUsername() {
	
		return this.username;
	
	}
	
	/**
	 * The method returns the clients response on the challenge.
	 * @return, string containing a sha1 encoded response of the user
	 */
	public String getAutResponse() {
	
		return this.authResponse;
	
	}
	
	public void setObjectID(int objectID) {
	
		this.objectID = objectID;
	
	}
	
	public int getObjectID() {
	
		return this.objectID;
	
	}

	/**
	 * The method updates the SessionObject timestamp.
	 */
	public void touch() {
	
		this.timestamp	= System.nanoTime();
		
		//System.out.println("[SessionObject] touch() - id="+this.objectID);
	
	}
	
	public SocketAddress getSocketAddress() {
	
		return this.socket;
	
	}

	public int getSessionID() {
	
		return this.sessionID;
	
	}

}
