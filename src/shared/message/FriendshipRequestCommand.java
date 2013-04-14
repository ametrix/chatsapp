package shared.message;

import java.util.Date;

public class FriendshipRequestCommand implements CommandMessage {
	
	private static final long serialVersionUID = 3821940748185921156L;
	
	private String receiverName;
	private Long receiverId;
	private String senderName;
	private Long senderId;
	private String message;
	private Date date;
	private boolean accepted;
	private boolean denied;
	
	public FriendshipRequestCommand() {}
	
	public FriendshipRequestCommand(Long receiverId, Long senderId, String message, Date date) {
		super();
		this.receiverId = receiverId;
		this.senderId = senderId;
		this.message = message;
		this.date = date;
	}



	/**
	 * @return the Id of the user(friend) who has to receive the message.
	 */
	public Long getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(Long receiverId) {
		this.receiverId = receiverId;
	}
	
	public String getReceiverName() {
		return receiverName;
	}
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	/**
	 * @return the Id of the user(friend) who send the message
	 */
	public Long getSenderId() {
		return senderId;
	}
	public void setSenderId(Long userId) {
		this.senderId = userId;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}

	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	
	/**
	 * @return true if this is answer, and the request is accepted
	 */
	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	/**
	 * @return true if this is answer and the request is denied
	 */
	public boolean isDenied() {
		return denied;
	}

	public void setDenied(boolean denied) {
		this.denied = denied;
	}
	
}
