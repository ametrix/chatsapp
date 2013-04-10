package server;

import java.util.Map;

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
        
        public Map<Long, String> findUsers(String criteria);
}
