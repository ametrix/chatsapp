package shared.message;

import java.util.Date;


/**
 * This class represents a message between two participations it the chat.
 * @author PDimitrov
 */
public class TextMessage implements ClientToClientMessage {

	private static final long serialVersionUID = -527221660651199141L;
	
	
	private Long receiverId;
	private Long senderId;
	private String message;
	private Date date;
	
	public TextMessage() {}
	
	public TextMessage(Long receiverId, Long senderId, String message, Date date) {
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
	
}
