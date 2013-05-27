/**
 * 
 */
package client;

/**
 * Contains information about a chat.
 * @author PDimitrov
 *
 */
public class ChatInfo {
	
	private String userName;
	private Long userId;
	private String friendName;
	private Long friendId;
	
	
	public ChatInfo(String userName, Long userId, String friendName, Long friendId) {
		this.userName = userName;
		this.userId = userId;
		this.friendName = friendName;
		this.friendId = friendId;
	}
	
	public String getUserName() {
		return userName;
	}
	public Long getUserId() {
		return userId;
	}
	public String getFriendName() {
		return friendName;
	}
	public Long getFriendId() {
		return friendId;
	}
	
}
