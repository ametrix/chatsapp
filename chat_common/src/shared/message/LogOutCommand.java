/**
 * 
 */
package shared.message;

/**
 * @author PDimitrov
 *
 */
public class LogOutCommand implements CommandMessage {

	private static final long serialVersionUID = -1091104421355880864L;
	
	private Long senderId;

	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}
	
}
