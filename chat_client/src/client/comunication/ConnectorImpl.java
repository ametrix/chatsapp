package client.comunication;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;


import shared.DefenceUtil;
import shared.SkypeStatus;
import shared.message.ClientToClientMessage;
import shared.message.FindUsersCommand;
import shared.message.FriendshipRequestCommand;
import shared.message.LogOutCommand;
import shared.message.LoginCommand;
import shared.message.Message;
import shared.message.RegisterCommand;
import shared.message.StatusChangedCommand;

import client.Connector;
import client.comunication.MessageReader.CommandListener;

/**
 * Implementation of the Connector Interface to be used by the client application.
 * @author PDimitrov
 */
public class ConnectorImpl implements Connector {
	
	
	private ClientConnection clientConnection = null;
	
	private MessageReader messageReaderThread = null;

	private IncomeMessageListener incomMsgListener = null;
	private FriendsStatusListener friendsStatusListener = null;
	private FriendshipRequestListener friendshipRequestListener = null;
	
	private ConnectionType connectionType;
	
	public ConnectorImpl(FriendsStatusListener statusListener, ConnectionType connectionType) {
		this.friendsStatusListener = statusListener;
		this.connectionType = connectionType;
	}
	

	private void enshureNotNullConnection() {
		if(clientConnection == null) {
			throw new IllegalStateException("ClientConnection in null!");
		}
	}
	
	private void sendMessage(Message message, boolean resetStream) {
		enshureNotNullConnection();
		try {
			
			clientConnection.writeObject(message, resetStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void registerMessageListener(IncomeMessageListener listener) {
		incomMsgListener = listener;
	}

	@Override
	public void registerFriendsStatusListener(FriendsStatusListener listener) {
		this.friendsStatusListener = listener;
	}
	
	
	@Override
	public void registerFriendshipRequestListener(FriendshipRequestListener listener) {
		this.friendshipRequestListener = listener;
	}

	
	@Override
	public void sendClientToClientMessage(ClientToClientMessage message, boolean resetStream) {
		sendMessage(message, resetStream);
	}

	@Override
	public Map<Long, String> login(String userName, String password) {
		// On new try to login we destroy old connections and create new one
		if(this.clientConnection != null) {
			this.clientConnection.closeConnection();
			this.clientConnection = null;
		}
		
		LoginCommand loginCmd = new LoginCommand(userName, password);
		ClientConnection conn = ConnectionFactory.makeConnection(connectionType);
		
		Map<Long, String> resMap = null;
		
		try {
			Object res = conn.writeRead(loginCmd, false);
			
			if(res instanceof LoginCommand) {
				resMap = ((LoginCommand)res).getLoginResult();
				// If the login is successful keep this connection to be used for communication
				if(!resMap.isEmpty()) {
					long userId =  findId(((LoginCommand) res).getUserName(), resMap);
					clientConnection = conn;
					IncomeMessageListener proxy = makeMessageListenerProxy();
					messageReaderThread = clientConnection.makeMessageReader(proxy, userId); 
					messageReaderThread.registerCommandListener(FriendshipRequestCommand.class, makeFriendshipRequestListenerProxy());
					messageReaderThread.registerCommandListener(StatusChangedCommand.class, makeStatusListenerProxy());
					messageReaderThread.start();
				} else {
					conn.closeConnection();
					resMap = null;
				}
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			clientConnection.closeConnection();
			clientConnection = null;
		}
		
		return resMap;
	}
	private IncomeMessageListener makeMessageListenerProxy() {
		return new IncomeMessageListener(){
			@Override
			public void massageReceived(ClientToClientMessage message) {
				if(ConnectorImpl.this.incomMsgListener != null) {
					ConnectorImpl.this.incomMsgListener.massageReceived(message);
				}
			}

		
		};
	}
	private CommandListener<FriendshipRequestCommand> makeFriendshipRequestListenerProxy() {
		return new CommandListener<FriendshipRequestCommand>(){
			@Override public Class<FriendshipRequestCommand> getTargetCommandClass() {return FriendshipRequestCommand.class;}
			@Override public boolean useOnceOnly() {return false;}

			@Override
			public void commandReceived(FriendshipRequestCommand command) {
				if(ConnectorImpl.this.friendshipRequestListener != null) {
					ConnectorImpl.this.friendshipRequestListener.frendShipRequestReceived(command);
				}
			}
			
		};
	}
	private CommandListener<StatusChangedCommand> makeStatusListenerProxy() {
		return new CommandListener<StatusChangedCommand>(){
			@Override public Class<StatusChangedCommand> getTargetCommandClass() {return StatusChangedCommand.class;}
			@Override public boolean useOnceOnly() {return false;}

			@Override
			public void commandReceived(StatusChangedCommand command) {
				if(ConnectorImpl.this.friendsStatusListener != null) {
					ConnectorImpl.this.friendsStatusListener.statusChange(
							command.getUserId()
							, SkypeStatus.converFromName(command.getNewStatus())
					);
				}
			}
			
		}; 
	}
	
	
	@Override
	public boolean createNewProfile(String userName, String password) {
		RegisterCommand regCmd = new RegisterCommand(userName, password);
		
		ClientConnection conn = ConnectionFactory.makeConnection(connectionType);
		Object serverAnswer = false;
		try {
			serverAnswer = conn.writeRead(regCmd, false);
			conn.closeConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return (Boolean)serverAnswer;
	}

	@Override
	public void findNewFriends(String criteria, final ResultCallBack<Map<Long, String>> resultCallBack){// throws Exception {
		enshureNotNullConnection();
		DefenceUtil.enshureArgsNotNull(" resultCallBack can't be Null!", resultCallBack);
		
		FindUsersCommand findComm = new FindUsersCommand();
		findComm.setCriteria(criteria == null ? "" : criteria);
		
		CommandListener<FindUsersCommand> callback = new CommandListener<FindUsersCommand>(){
			@Override public Class<FindUsersCommand> getTargetCommandClass() {return FindUsersCommand.class;}
			@Override public boolean useOnceOnly() {return true; }
			@Override public void commandReceived(FindUsersCommand command) {
				resultCallBack.resultReady(command.getFoundUsers());
			}
		};
		try {
			messageReaderThread.registerCommandListener(FindUsersCommand.class, callback);
			clientConnection.writeObject(findComm, false);
		} catch (IOException e) { e.printStackTrace(); } 
		
	}
	
	


	@Override
	public void sendFredshipRequest(FriendshipRequestCommand message) {
		sendMessage(message, false);
	}

	
	public void closeConnection() {
		this.clientConnection.closeConnection();
	}
	public void logOut() {
		try {
			sendMessage(new LogOutCommand(), false);
			clientConnection.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private Long findId(String name, Map<Long,String> frMap) {
		for(Entry<Long, String> entry : frMap.entrySet()) {
			if(entry.getValue().equals(name)) {
				return entry.getKey();
			}
		}
		return null;
	}
}
