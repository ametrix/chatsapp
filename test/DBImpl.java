

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import server.DBOperator;
import server.FriendshipRequest;


public class DBImpl implements DBOperator{

	private static Long lastIdGiven = 0L;
	private Map<Long, User> users = new HashMap<Long, User>(); // contains all the users
	
	public DBImpl() {
		users = UsersSource.generateUsers();
		lastIdGiven = (long) users.size();
	}
	
	
	// INterface methods
	@Override
	public boolean registerNewUser(String userName, String password) {
		User u = new User();
		u.username = userName;
		u.password = password;
		u.id = lastIdGiven++;
		for(User user : users.values()) {
			if(user.username.equals(userName)) {
				return false;
			}
		}
		users.put(u.id, u);
		return true;
	}

	@Override
	public Map<Long, String> getUserFriends(String userName, String password) {
		Map<Long, String> result = new HashMap<Long, String>();
		Set<Long> friendSet = null;
		for(User u : users.values()) {
			if(u.username.equals(userName)) {
				result.put(u.id, u.username);
				friendSet = u.friendsIds;
			}
		}
		if(result.size() == 0) {
			return null;
		}
		
		for(Long id : friendSet) {
			if(users.get(id) == null) {
				throw new IllegalStateException("The user:"+userName+" has friend with id:"+id+" that is not registred in the users map!");
			}
			result.put(id, users.get(id).username);
		}
		
		return result;
	}


	@Override
	public boolean addFriendship(long user_1_id, long user_2_id) {
	
		boolean r1 = users.get(user_1_id).friendsIds.add(user_2_id);
		boolean r2 = users.get(user_2_id).friendsIds.add(user_1_id);
		return (r1 && r2);
	}



	// returns Map with all the users, whose username contains the criteria
	@Override
	public Map<Long, String> findUsers(String criteria) {
		Map<Long, String> result = new HashMap<Long, String>();

		for(User u : users.values()) {
			if(u.username.contains(criteria)) {
				result.put(u.id, u.username);
			}
		}
		if(result.size() == 0) {
			return null;
		}
		return result;
	}


	@Override
	public void addFriendshipRequest(long senderId, String senderName,
			long receiverId, String receiverName, String msg, Date date) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deleteFriendshipRequests(long userId1, long userId2) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public List<FriendshipRequest> getFriendshipRequestsForUser(long userId) {
		// TODO Auto-generated method stub
		return null;
	}

}



class User {
	Set<Long> friendsIds = new HashSet<Long>();
	long id;
	String username;
	String password;
	
	public User(){}
	
	public User(long id, String username, String password) {
		this.id = id;
		this.username = username;
		this.password = password;
	}
	
}

class UsersSource {
	public static Map<Long, User> generateUsers() {
		
		
		User u1 = new User(1, "ivan", "ivanP");
		User u2 = new User(2, "dragan", "draganP");
		User u3 = new User(3, "mitko", "mitkoP");
		User u4 = new User(4, "gosho", "goshoP");
		User u5 = new User(5, "mira", "miraP");
		
		// ivan is fried to dragan
		u1.friendsIds.add(2L);
		u2.friendsIds.add(1L);
		//mitko gosho and mira are friends
		u3.friendsIds.add(4L);
		u3.friendsIds.add(5L);
		u4.friendsIds.add(3L);
		u4.friendsIds.add(5L);
		u5.friendsIds.add(3L);
		u5.friendsIds.add(4L);
		
		
		Map<Long, User> users = new HashMap<Long, User>();
		users.put(1L, u1);
		users.put(2L, u2);
		users.put(3L, u3);
		users.put(4L, u4);
		users.put(5L, u5);
		
		return users;	
	}
}
