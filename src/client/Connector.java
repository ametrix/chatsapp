package client;

import java.util.Map;

import shared.SkypeStatus;
import shared.message.ClientToClientMessage;
import shared.message.FriendshipRequestCommand;

/**
 * Connector making the connection between the UI module
 *  and the communication module on the client application.
 * @author PDimitrov
 */
public interface Connector {
	
	/**
	 * Listener to be registered into the Connector to listen for incoming messages.
	 * @author PDimitrov
	 */
	public interface IncomeMessageListener {
		
		/**
		 * This method will be called when new message is received.
		 * @param message - the message
		 */
		public void massageReceived(ClientToClientMessage message);
	}

	
	/**
	 * Listener to be registered into the Connector to listen for 
	 * changes of the status of the user's friends.
	 * @author PDimitrov
	 */
	public interface FriendsStatusListener {
		
		/**
		 * This method will be called when there is a change in some friend's status.
		 */
		public void statusChange(Long userId, SkypeStatus newStatus);
	}
	
	/**
	 * Callback to be used to perform some actions when an answer to request is received.
	 * @author PDimitrov
	 *
	 * @param <ResultType>
	 */
	public interface ResultCallBack<ResultType> {
		/**
		 * This method will be called when an answer to request is received.
		 */
		public void resultReady(ResultType result);
	}
	
	/**
	 * Listener to be registered into the Connector to listen 
	 * for invitations for friendship from other people.
	 * @author PDimitrov
	 */
	public interface FriendshipRequestListener {
		/**
		 * This method will be called when invitation for friendship is received.
		 */
		public void frendShipRequestReceived(FriendshipRequestCommand command);
	}
	
	/**
	 * Registers the listener which will be notified when new messages arrives.
	 * @param listener
	 */
	public void registerMessageListener(IncomeMessageListener listener);
	
	/**
	 * Registers the listener which will be notified when there are some statuses changed.
	 * @param listener
	 */
	public void registerFriendsStatusListener(FriendsStatusListener listener);
	
	
	public void registerFriendshipRequestListener(FriendshipRequestListener listener);
	
	/**
	 * Sends message.
	 * @param message
	 */
	public void sendClientToClientMessage(ClientToClientMessage message, boolean resetSream);
	
	
	/**
	 * Calling this method the connector will make attempt to log onto the server. 
	 * If log on is successful a Map (containing this user as a entry and all his friends) will be returned, if not null will be returned.
	 * @param userName
	 * @param password
	 * @return Map containing the friends of this user (the key is friend's ID, the value is friend's name ), 
	 * the map will contain also entry for this user (with this user's username and ID)
	 */
	public Map<Long, String> login(String userName, String password);
	
	/**
	 * Calling this method the connector will make attempt to create new profile onto the server. 
	 * @param userName
	 * @param password
	 * @return true if the profile is created successfully, false otherwise. 
	 */
	public boolean createNewProfile(String userName, String password);
	
	
	/**
	 * Calling this method the connector will ask the server for users whose userNames match the criteria. 
	 * When the server send the answer the resultReady method of the resultCallBack will be called, and 
	 * a result Map (Map containing the users whose names match the criteria (the key is user's ID, the value is user's name ) ) 
	 * from the server will be passed as parameter
	 * @param userName
	 * @param password
	 */
	public void findNewFriends(String criteria, ResultCallBack<Map<Long,String>> resultCallBack);
	
	/**
	 * Sends a request for friendship
	 * @param message - contains the ID of the user who sends the request,
	 *  the ID of the user who will receive the request and a message for the receiver.
	 */
	public void sendFredshipRequest(FriendshipRequestCommand message);
	
}
