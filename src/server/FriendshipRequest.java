/**
 * 
 */
package server;

import java.util.Date;

/**
 * @author PDimitrov
 *
 */
public class FriendshipRequest {
	
	private String receiverName;
	private Long receiverId;
	private String senderName;
	private Long senderId;
	private String message;
	private Date date;
	
	public FriendshipRequest() {}
	
	public FriendshipRequest(Long receiverId, Long senderId, String message, Date date) {
		super();
		this.receiverId = receiverId;
		this.senderId = senderId;
		this.message = message;
		this.date = date;
	}



	/**
	 * @return the Id of the user(friend) who has to receive the request.
	 */
	public Long getReceiverId() {
		return receiverId;
	}	
	
	public String getReceiverName() {
		return receiverName;
	}


	/**
	 * @return the Id of the user(friend) who send the request.
	 */
	public Long getSenderId() {
		return senderId;
	}
	
	public String getMessage() {
		return message;
	}
	
	public Date getDate() {
		return date;
	}
	
	public String getSenderName() {
		return senderName;
	}
	
}
