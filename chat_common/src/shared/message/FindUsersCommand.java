/**
 * 
 */
package shared.message;

import java.util.Map;

/**
 * @author PDimitrov
 *
 */
public class FindUsersCommand implements CommandMessage {

	private static final long serialVersionUID = 5525174608762886485L;
	
	private Long senderId;
	private String criteria;
	private Map<Long, String> foundUsers;

	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	public Map<Long, String> getFoundUsers() {
		return foundUsers;
	}

	public void setFoundUsers(Map<Long, String> foundUsers) {
		this.foundUsers = foundUsers;
	}

	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}
	
}
