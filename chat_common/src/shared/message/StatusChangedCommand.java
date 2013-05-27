/**
 * 
 */
package shared.message;


/**
 * @author PDimitrov
 *
 */
public class StatusChangedCommand implements CommandMessage{

	private static final long serialVersionUID = 4254711115136986698L;

	private Long userId;
	private String oldStatus = "";
	private String newStatus = "";
	
	
	protected StatusChangedCommand() {	}
	
	protected StatusChangedCommand(Long userId, String oldStatus,	String newStatus) {
		this.userId = userId;
		this.oldStatus = oldStatus;
		this.newStatus = newStatus;
	}
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getOldStatus() {
		return oldStatus;
	}
	public void setOldStatus(String oldStatus) {
		this.oldStatus = oldStatus;
	}
	public String getNewStatus() {
		return newStatus;
	}
	public void setNewStatus(String newStatus) {
		this.newStatus = newStatus;
	}
	
}
