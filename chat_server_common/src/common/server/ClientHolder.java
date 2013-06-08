/**
 * 
 */
package common.server;

import shared.DefenceUtil;
import shared.message.Message;

/**
 * @author PDimitrov
 *
 */
public abstract class ClientHolder {
	
	private long id;
	private String username;
	private String password;
	
	
	public ClientHolder(long id, String username, String password) {
		DefenceUtil.enshureArgsNotNull("The constructor arguments cant be Null!"
				, id, username, password
		);
		
		this.id = id;
		this.username = username;
		this.password = password;
	}

	public long getId() {return id;}
	
	public String getUsername() {return username;}

	public String getPassword() {return password;}

	public void destroy() {}
	
	
	/**
	 * Add a message to the queue of waiting messages 
	 * to be send to the client this instance is representing
	 * @param msg - the message
	 */
	public abstract void addMsgForSending(Message msg);
	
	/**
	 * Handles received message from the client this instance is representing
	 * @param msg - the message
	 */
	public abstract void handleReceivedMsg(Message msg);
}
