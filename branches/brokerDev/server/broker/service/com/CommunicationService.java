package broker.service.com;

import broker.service.com.session.*;
import broker.service.com.protocol.*;
import broker.service.*;
import broker.service.crown.*;
import broker.service.index.*;
import broker.service.user.*;
import broker.object.*;
import java.net.*;
import java.math.*;
import java.util.*;
import java.security.*;

/**
 * The communication service provides communication services like setting up a session with the registrar,
 * accepting and authenticating new clients.
 */
public class CommunicationService extends BrokerServiceWrapper {

	/**
	 * modulus used to validate tickets
	 */
	private BigInteger pks_modulus = new BigInteger("8974dc150978e7967af0b875f16fe0c1eb82ea56544a11cca5e691b01542781ffa0eb2730300d450c1b9f7452784ca0d7afc73bf28d5981eb971ebc573c057f02c50096f5ecf9af7d48544f51b130b493be6e02e6b878b6a6bf8ff9ba8ff630589635bcdc31beabdc3f5d005c6137ef2a855fdbec2da715d3ef3a3385e95ac51", 16);

	/**
	 * reference to the IndexService
	 */
	private IndexService index = null;
	
	/**
	 * external IP address received through STUN like service offered by the registrar (only different to internal in case of NAT)
	 */
	private String externalIPAddress = null;

	/**
	 * external Port address received through STUN like service offered by the registrar (only different to internal in case of NAT)
	 */	
	private int externalPort = 0;
	
	/**
	 * The ticket assigned by the registrar during session setup, used to authenticate against all clients
	 */
	private String ticket = "";
	
	/**
	 * Current session id, will be set in every message that gets send.
	 */
	private int sessionID = -1;
	
	/**
	 * Stores the temporary objectID at first but later the actual id
	 */
	private int objectID = -1;
	
	/**
	 * Counter used to determine if the registrar is still online
	 */
	private int keepAliveComp = 0;
	
	/**
	 * indicates wether the registrar is available or not
	 */
	private boolean registrarOnline = false;
	
	/**
	 * counts the requests and is increased on every empty request id
	 */
	private int requestCount = 0;
	
	
	/**
	 * Extended constructor expecting the reference to the IndexService
	 */
	public CommunicationService(IndexService index) {
	
		this.index = index;
		
		CrownService c = CrownService.getInstance();
		c.registerService(this);
	
	}

	/**
	 * Upper layer SAP is used to complete the Message parameter and pass it to the next lower layer.
	 * @param c Message as Object
	 * @return Message to send as Object
	 */
	public Object sapUpperLayer(Object c) {
	
		//System.out.println("[CommunicationService] sapUpperLayer()");
		
		BrokerService p = this.getPreviousService();
		Message m		= (Message)c;
		m.setSessionID(this.sessionID);
		
		if ( m.getRequestID() == 0 )
			m.setRequestID(++this.requestCount);
		
		if ( p != null )
			p.sapUpperLayer(c);
	
		return null;
	
	}

	/**
	 * Lower layer SAP is used to either pass a Message to the upper layer or process it internally.
	 * Message get only processed in case the sender is known, the only exceptions are Messages for
	 * session setup.
	 * @param c Message as Object
	 * @return Message to send as Object
	 */
	public Object sapLowerLayer(Object c) {
		
		Object r = null;
		Message m = (Message)c;
		int requestID = (m.getRequestID()&0x7FFFFFFF);
		SessionObject so = this.index.lookupSessionObject(m.getObjectID());
		
		/* filter messages whose sender is not known, only exception a ClientHello request */
		if ( (so == null && requestID < 2 && requestID > -1) || (so != null) ) {
			
			/* request is no broker internal request */
			if ( (m.getRequestID()&0x80000000) == 0 ) {
			
				if ( this.nextService != null )
					r = this.nextService.sapLowerLayer(c);
			
			}
			
			/* broker internal request */
			else {
			
				//System.out.println("[CommunicationService] Broker internal request. Error: "+m.getError());
				r = this.handleRequest(m, so);			
			
			}
			
		}
		
		/* when Message is refused because it does not math the defined criteria above */
		else {
		
			System.out.println("[CommunicationService] Message refused (id="+m.getObjectID()+", request="+m.getRequestID() +")");
		
		}
		
		return r;
	
	}
	
	/**
	 * This method is used to handle internal request, that means requests that need to be processed in the Communication
	 * Service itself.
	 * @param request request id
	 * @param m Message to process
	 * @return return Message, that contains a repsonse to the received Message
	 */
	private Message handleRequest(Message m, SessionObject so) {
	
		Message r = null;
		boolean handled = true;
		int request = m.getRequestID();
	
		switch(request) {
			
			case 0x80000001: r = this.handleRegistrarHello(m, so); break;
			case 0x80000003: r = this.handleRegistrarAuth(m, so); break;
			case 0x80000000: r = this.handleClientHello(m, so); break;
			case 0x8000000A: r = this.handleKeepAlive(m, so); break;
			default: handled = false; break;
		
		}
				
		return r;
	
	}

	/**
	 * The method handles a ClientHello request and responses with it's own ticket. ClientHello to the server
	 * is the last step of the session setup, the BrokerCallBacks will be infromed about the new joined object.
	 * @param m Message to process
	 * @return response Message (ServerHello) or null
	 */
	private Message handleClientHello(Message m, SessionObject so) {
		
		Message response = new Message(m);
		response.setRequestID(0x80000004);
		response.setSessionID(this.sessionID);
	
		/* check ticket from client in order to authenticate him */
		if ( this.validateTicket(m) ) {
			
			try {
				
				/* after successfull validation of the ticket the client can be registered */
				this.index.registerSessionObject( new SessionObject(m.getObjectID(), m.getSessionID(), new InetSocketAddress(InetAddress.getByName(m.getString("ip")), m.getInteger("port"))) );

				/* inform callback about new client with objectJoined() */
				ArrayList<BrokerCallBack> cb = this.index.getBrokerCallBacks();
				for(int i=0; i < cb.size(); i++) {
					cb.get(i).objectJoined(m.getObjectID());
				}
				
				/* return own ticket in order to provide bidirectional authentication */
				response.setString("ticket", this.ticket);
				return response;

				
			}
			catch(UnknownHostException e) {
			
				e.printStackTrace();
				return null;
				
			}
				
			
		}
		else
			return null;
				
	}


	/**
	 * The method handles KeepAlive Messages from the clients and the registrar.
	 * @param m Message to process
	 * @return response KeepAlive Message
	 */
	private Message handleKeepAlive(Message m, SessionObject so) {

		/* keep alive from arbitrary object/client */
		if ( so.getObjectID() != this.objectID ) {
			
			so.touch();
			Message response = new Message(m);
			return response;
			
		}
		
		/* keep alive message from registrar */
		else {
		
			this.keepAliveComp--;
		
		}
		
		return null;
						
	}
	
	/**
	 * The message handles a RegistrarHello, that contains a STUN response and the challenge for authentication.
	 * @param m Message to process
	 * @return authentication response
	 */
	private Message handleRegistrarHello(Message m, SessionObject so) {
		
		long challenge			= m.getLong("challenge");
		this.externalIPAddress	= m.getString("ip");
		this.externalPort		= m.getInteger("port");
		this.sessionID			= m.getSessionID();
		this.objectID			= m.getObjectID();
		this.registrarOnline	= true;
		
		String digest = UserService.createResponse(this.index.getUsername(), this.index.getPassword(), challenge);
		
		/* map temporary object id to the registrars session */
		this.index.reRegisterSessionObject(0x80000000, this.objectID);

		/* create response message */
		Message response = new Message(m);
		response.setRequestID(0x80000002);
		response.setString("user", this.index.getUsername());
		response.setString("response", digest);
	
		return response;
				
	}
	
	/**
	 * The method handles a RegistrarAuth Message, this message indicates wether the authentication was successfull or not.
	 * It contains the actual object id and the ticket for the session in case of a successfull authentication.
	 * @param m Message to process
	 * @return null, since there is no reponse Message required
	 */
	private Message handleRegistrarAuth(Message m, SessionObject so) {

		if ( m.getBoolean("auth") ) {
		
			int objectid	= m.getInteger("objectid");
			this.ticket		= m.getString("ticket");
			
			/* remap temporary to real object id */
			this.index.reRegisterSessionObject(this.objectID, objectid);
			this.objectID	= objectid;
						
			System.out.println(String.format("[CommunicationService] handleRegistrarAuth() - Authenticated with ObjectID=%d", objectid));
		
		}
		else {
		
			System.out.println(String.format("[CommunicationService] handleRegistrarAuth() - Authentication declined!"));		
		
		} 
		
		return null;
	
	}
	
	
	public void crownCallBack() { 
			
		/* if registrar is not responding to keep alives it is assumed that it is offline */
		if ( this.keepAliveComp > 1 ) {
			this.setRegistrarOffline();
			this.keepAliveComp = 0;
		}
		
		/*
		 * check all Sessions if they are still valid.
		 */
		Enumeration<SessionObject> eso = this.index.getAllSessionObjects();
		SessionObject tso;
		
		/* timeout in session initialization list */		
		while(eso.hasMoreElements()) {
		
			tso = eso.nextElement();
			
			if ( tso.getObjectID() != this.objectID ) {
				
				/* if timeouted remove session */
				if ( tso.isTimedout(2000) ) {
				
					/* inform callback about new client with objectJoined() */
					ArrayList<BrokerCallBack> cb = this.index.getBrokerCallBacks();
					for(int i=0; i < cb.size(); i++) {
						cb.get(i).objectLeft(tso.getObjectID());
					}

					this.index.unregisterSessionObject(tso.getObjectID());
											
				}
							
			}
			
		}
				
		/*
		 * send an own keep alive to the registrar
		 */
		
		if ( this.registrarOnline ) {
			
			eso = this.index.getAllSessionObjects();
			int esoSize = 0;
			Message keepAlive = new Message(this.sessionID, 0x8000000A, this.objectID);
			
			/* timeout in session initialization list */		
			while(eso.hasMoreElements()) {
			
				tso = eso.nextElement();
				keepAlive.setInteger("obj"+esoSize, tso.getObjectID());
				esoSize++;
				
			}
			
			keepAlive.setInteger("objSize", esoSize);
			this.sapUpperLayer(keepAlive);
			this.keepAliveComp++;
			
		}
	
	}
	
	private void setRegistrarOffline() {
				
		System.out.println("[CommunicationService] setRegistrarOffline	() - Registrar not reachable.");
		this.registrarOnline = false;
	
	}
	
	
	/**
	 * The method is used to validate a received ticket Message from a client. Validation is based
	 * on a RSA Signature.
	 * @param message Message that contains the authentication ticket from a client
	 * @return true in case of successfull authentication, else false
	 */
	private boolean validateTicket(Message message) {
	
		try {
		
			BigInteger c	= new BigInteger(1,message.getString("ticket").getBytes());
			BigInteger m	= c.modPow(new BigInteger("10001",16), this.pks_modulus);

			byte[] to				= new byte[(4+4+4)];
			int sessionID			= message.getSessionID();
			InetAddress ia			= InetAddress.getByName(message.getString("ip"));
			byte[] ip				= ia.getAddress();
			
			ByteConversion.copyBytes(ip, 0, to, 0, 4);		
			ByteConversion.longToByte(message.getInteger("port"), 4, to, 4);
			ByteConversion.longToByte(sessionID, 4, to, 8);			
				
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(to);
			to = md.digest();
		
			/*
			for(int i=0; i < to.length; i++)
				System.out.print(to[i] + " ");
			System.out.println();
			
			
			to = m.toByteArray();
												
			System.out.println("--");
			for(int i=0; i < to.length; i++)
				System.out.print(to[i] + " ");
			System.out.println();
			*/
			
			return true;

			/*

			if ( isa1.equals(isa2) && sessionID == message.getSessionID() ) {
			
				System.out.println("[CommunicationService] validateTicket() - Ticket from " + message.getObjectID() + " positive validated!");
				return true;
				
			}
*/
		}
		catch(UnknownHostException e) {}
		catch(NoSuchAlgorithmException e) {		
				e.printStackTrace();
			}

		System.out.println("[CommunicationService] validateTicket() - Ticket from " + message.getObjectID() + " NOT validated!");		
		return false;
	
	}


}
