package broker.service.com;

import java.math.*;
import java.util.*;
import java.net.*;
import java.security.*;
import broker.service.*;
import broker.service.index.*;
import broker.service.crown.*;
import broker.service.com.protocol.*;
import broker.service.com.session.*;
import broker.service.user.*;

/**
 * Registrar exclusive!!!
 * The session service establishes the communication parameter and sets up a session.
 * It uses Challenge Response Authentication based on the sha1 message digest algorithm.
 */
public class SessionService extends BrokerServiceWrapper {

	/**
	 * Enables cli and message debugging
	 */
	private final boolean DEBUG = true;

	/**
	 * private key used for creating a RSA signature = ticket
	 */
	private BigInteger pks_pk = new BigInteger("72db6d133f09022ae1abc7d6f5d9d06af9ca38d9ce30de52acc5cf32dd70df43890542e38bbce3ac5f546f641fb37bca6bdc0b33ce813c4d6013eb38e091f668047e268daa9663a5f57cb8eae8d375b9c5e74621fddd5a522f3bf7e2897aa9c8102de25ce8d2486cb252d622bfd5a02976a9ab3b19c4f067b5992be2d2cb8f89", 16);

	/**
	 * Modulus used for creating a RSA signature = ticket
	 */
	private BigInteger pks_modulus = new BigInteger("8974dc150978e7967af0b875f16fe0c1eb82ea56544a11cca5e691b01542781ffa0eb2730300d450c1b9f7452784ca0d7afc73bf28d5981eb971ebc573c057f02c50096f5ecf9af7d48544f51b130b493be6e02e6b878b6a6bf8ff9ba8ff630589635bcdc31beabdc3f5d005c6137ef2a855fdbec2da715d3ef3a3385e95ac51", 16);

	/**
	 * easy access to the IndexService
	 */
	private IndexService index = null;
	
	/**
	 * easy access to the UserService
	 */
	private UserService userService = null;
	
	/**
	 * list of sessions mapped by their object id, this is at the beginning a temporary id.
	 */
	private Hashtable<Integer,SessionObject> initSessionList;
	
	

	public SessionService(IndexService index) {
	
		this.index			= index;
		this.initSessionList= new Hashtable<Integer,SessionObject>();
		this.userService	= UserService.getInstance();

		CrownService c		= CrownService.getInstance();
		c.registerService(this);
		
	}

	public Object sapUpperLayer(Object c) {
	
		//System.out.println("[SessionService] sapUpperLayer()");
		
		BrokerService p = this.getPreviousService();
		
		if ( p != null )
			p.sapUpperLayer(c);
	
		return null;
	
	}

	/** 
	 * Handles most of the session set up.
	 */
	public Object sapLowerLayer(Object c) {
	
		//System.out.println("[SessionService] sapLowerLayer()");
		//System.out.println(c);
	
		Message m = (Message)c;
		int requestID = m.getRequestID();
		Integer mobj = new Integer(m.getObjectID());
		
		try {
		
			/*
			 * received ObjectHello
			 */
			if ( requestID == 0x80000000 ) {
				
				SessionObject so;
				
				/* in case of an ObjectHello no SessionObject shall exists */
				if ( (so=this.initSessionList.get(mobj)) == null ) {
				
					/* get associated socket */
					InetSocketAddress isa	= (InetSocketAddress)this.index.lookupSocketAddress(m.getObjectID());
					byte[] ip				= isa.getAddress().getAddress();
					
					/* create session object */
					so = new SessionObject(m.getObjectID(), isa);

					synchronized(this.initSessionList) {
						this.initSessionList.put(mobj, so);
					}
					
					/* creating the response */
					Message response = new Message(so.getSessionID(), (requestID+1), m.getObjectID());
					response.setString("ip", String.format("%d.%d.%d.%d", ByteConversion.byteToUINT(ip[0]), ByteConversion.byteToUINT(ip[1]), ByteConversion.byteToUINT(ip[2]), ByteConversion.byteToUINT(ip[3])));
					response.setInteger("port", isa.getPort());
					response.setLong("challenge", so.getChallenge());
				
					return response;

				
				}
									
			}
			
			/*
			 * received ObjectAuth
			 */
			else if ( requestID == 0x80000002 ) {
			
				SessionObject so;
				
				/* if the SessionObject does not exist or the request is out of order */
				if ( (so=this.initSessionList.get(mobj)) != null && so.nextRequest(requestID) ) {

					Message response = new Message(m.getSessionID(), (requestID+1), m.getObjectID());
					Object userServiceReturn = null;

					/* store username and auth string in SessionObject */
					so.setAuthenticationData(m.getString("user"), m.getString("response"));	
					
					/* pass the SessionObject to the UserService on top, to check the authentication data */
					if ( userService != null )
						userServiceReturn = userService.sapLowerLayer(so);
					
					/* send the authentication successfull message back in case the return value from UserService is equal to the SessionObject */
					if ( userServiceReturn == so ) {
						
						/* set timestamp in session to avoid expiration */
						so.touch();
						
						/* set auth to true, that indicates successfull authentication */
						response.setBoolean("auth", true);
						
						/* return the actual ObjectID, as the message objectID and the integer field */
						int objectID	= so.getObjectID();
						response.setInteger("objectid", objectID);
						response.setObjectID(objectID);
						
						/* remap temporary to real object id */
						this.index.reRegisterSocketAddress(m.getObjectID(), objectID);
											
						/* insert ticket */
						response.setString("ticket", this.createTicket(so) );



						/* prepare for look up */
						SessionObject luSess = so;
						String lookupName = null;
						
						/* try a lookup request if included */
						try {
							lookupName = m.getString("luName");
							luSess = userService.getSession(lookupName);
						}
						catch(RuntimeException e) {}
						
						/* in case the lookup result (luSess) is null, there was no result according to the request, an error message will be send */
						if ( luSess == null )
							throw new RuntimeException("[SessionService] Lookup not successful!");
							
						/* in case luSess is not equal to so, that means the look up was successfull and a proper look up response will be send back  */
						else if ( luSess != so ) {
						
							InetSocketAddress isa	= (InetSocketAddress)luSess.getSocketAddress();
							byte[] ip				= isa.getAddress().getAddress();
						
							response.setString("luName", lookupName);
							response.setString("luIP", String.format("%d.%d.%d.%d",ByteConversion.byteToUINT(ip[0]), ByteConversion.byteToUINT(ip[1]), ByteConversion.byteToUINT(ip[2]), ByteConversion.byteToUINT(ip[3])));
							response.setInteger("luObjID", luSess.getObjectID());
							response.setInteger("luPort", isa.getPort());
							
						}
						
						/* remove Session from init list after successful authentication */
						synchronized(this.initSessionList) {
							this.initSessionList.remove(mobj);
						}
												
					}
					
					/* decline authentication */
					else {
					
						response.setBoolean("auth", false);
										
					}

					return response;
					
				}
				
			}
			
			/*
			 * received KeepAlive
			 */
			else if ( requestID == 0x8000000A ) {

				//System.out.println("[SessionService] sapLowerLayer() - received KeepAlive from " + m.getObjectID());
				SessionObject tso;

				int objSize = m.getInteger("objSize");
				for(int i=0; i < objSize; i++) {
				
					tso = this.userService.getSession(m.getInteger("obj"+i));
					tso.touch();
					
					//System.out.println("[SessionService] sapLowerLayer() - touch id: " + tso.getObjectID());
				
				}

				/* keep alive back to the client is just an empty message */
				return new Message(m);

			}
			
		}
		catch(Exception e) {}
		
		/* in case this code segment is reached an error has occured, so send an error back */
		synchronized(this.initSessionList) {
			this.initSessionList.remove(mobj);
		}
			
		Message response = new Message(m);
		response.setError();
		return response;
	
	}
	
	
	private String createTicket(SessionObject so) {
	
		/* IP PORT SESSIONID */
	
		BigInteger c			= new BigInteger("0",10);
		byte[] to				= new byte[(4+4+4)];
		int sessionID			= so.getSessionID();
		InetSocketAddress isa	= (InetSocketAddress)so.getSocketAddress();
		byte[] ip				= isa.getAddress().getAddress();
		
		ByteConversion.copyBytes(ip, 0, to, 0, 4);		
		ByteConversion.longToByte(isa.getPort(), 4, to, 4);
		ByteConversion.longToByte(sessionID, 4, to, 8);
		
		try {
				
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(to);
			to = md.digest();
/*		
			for(int i=0; i < to.length; i++)
				System.out.print(to[i] + " ");
			System.out.println();
*/		
			BigInteger m = new BigInteger(1,to);
			c = m.modPow(this.pks_pk, this.pks_modulus);

/*						
			BigInteger m2 = c.modPow(new BigInteger("10001",16), this.pks_modulus);
			to = m2.toByteArray();
			System.out.println("--");
			for(int i=0; i < to.length; i++)
				System.out.print(to[i] + " ");
			System.out.println();
*/			
		}
		catch(NoSuchAlgorithmException e) {		
			e.printStackTrace();
		}
		
		return new String(c.toByteArray());
	
	}


	
	public void crownCallBack() {
	
		Enumeration<SessionObject> eso = this.initSessionList.elements();
		SessionObject tso;
				
		/* timeout in session initialization list */		
		while(eso.hasMoreElements()) {
		
			tso = eso.nextElement();
			
			if ( tso.isTimedout(2000) ) {
			
				synchronized(this.initSessionList) {
					this.initSessionList.remove(new Integer(tso.getObjectID()));
				}
			
			}
		
		}
		
		/* invoke clean up in session to user assigment */
		this.userService.clean();
	
	}

}
