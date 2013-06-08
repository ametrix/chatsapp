package common.server;

import java.util.Date;
import java.util.List;
import java.util.Map;

import shared.message.FriendshipRequest;

/**
 * Knows how to interact with the database.
 * @author PDimitrov
 */
public interface DBOperator {

	/**
	 * Attempt to register new user.
	 * @param userName
	 * @param password
	 * @return true if the registration is successful, false otherwise
 	 */
	public boolean registerNewUser(String userName, String password);
	
	/**
	 * Calling this method will cause a request to the database to check if there is user registered with
	 * this @param userName, and if there is such user the user will be added as entry in the resulting map
	 * as all of his friends found will be added too. 
	 * If there is not user registered with this @param userName null will be returned.
	 * @param userName
	 * @param password
	 * @return Map containing entry for this user (with this user's username and ID) and 
	 * entries for the friends of this user (the key is friend's ID, the value is friend's name ), 
	 * or null if there is not registered user with this @param userName
	 */
	public Map<Long, String> getUserFriends(String userName, String password);
	
	/**
	 * Tries to add new friendship between two users.
	 * @param user_1_id
	 * @param user_2_id
	 * @return true in success, false otherwise.
	 */
	public boolean addFriendship(long user_1_id, long user_2_id);
    
	/**
	 * Searches for users whose username contains the criteria parameter.
	 * @param criteria
	 * @return Map that contains the id and username of all the users whose username contains the criteria parameter
	 */
    public Map<Long, String> findUsers(String criteria);
    
    /**
     * Add a request with the given parameters. If a request from the sender to the receiver 
     * already exists the request parameters will be updated with the current parameters. 
     * @param senderId
     * @param senderName
     * @param receiverID
     * @param receiverName
     * @param msg
     */
    public void addFriendshipRequest(long senderId, String senderName, long receiverId, String receiverName, String msg, Date date);

    /**
     * Delete a friendship requests with the given parameters. If there are two requests 
     * one from user1 to user2 and one from user2 to user1 - then delete both.
     * @param senderId
     * @param receiverId
     */
    public void deleteFriendshipRequests(long userId1, long userId2);
    
    /**
     * Searches for all requests where the receiver is the user whose id is passed as parameter.
     * @param userId
     * @return list of found requests.
     */
    public List<FriendshipRequest> getFriendshipRequestsForUser(long userId);
    
}
