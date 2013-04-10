package client.comunication;

import java.io.IOException;
import java.util.Map;


import shared.DefenceUtil;
import shared.SkypeStatus;
import shared.message.FindUsersCommand;
import shared.message.FriendshipRequestCommand;
import shared.message.LogOutCommand;
import shared.message.LoginCommand;
import shared.message.RegisterCommand;
import shared.message.StatusChangedCommand;
import shared.message.TextMessage;

import client.Connector;
import client.comunication.ClientMessageReaderThred.CommandListener;

/**
 * Implementation of the Connector Interface to be used by the client application.
 * @author PDimitrov
 */
public class ConnectorImpl implements Connector {
	
	
	private ClientConnection clientConnection = null;
	
	private ClientMessageReaderThred messageReaderThread = null;

	private IncomeMessageListener incomMsgListener = null;
	private FriendsStatusListener friendsStatusListener = null;
	private FriendshipRequestListener friendshipRequestListener = null;
	
	public ConnectorImpl(FriendsStatusListener statusListener) {
		this.friendsStatusListener = statusListener;
//		this.client = new ClientConnection();
	}

	private void enshureNotNullConnection() {
		if(clientConnection == null) {
			throw new IllegalStateException("ClientConnection in null!");
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
	public void sendMessage(TextMessage message) {
		enshureNotNullConnection();
		try {
			clientConnection.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Long, String> login(String userName, String password) {
		// On new try to login we destroy old connections and create new one
		if(this.clientConnection != null) {
			this.clientConnection.closeConnection();
			this.clientConnection = null;
		}
		
		LoginCommand loginCmd = new LoginCommand(userName, password);
		ClientConnection conn = new ClientConnection();
		Map<Long, String> resMap = null;
		
		try {
			conn.writeObject(loginCmd);
		
			Object res = conn.readObject();
			if(res instanceof LoginCommand) {
				resMap = ((LoginCommand)res).getLoginResult();
				// If the login is successful keep this connection to be used for communication
				if(!resMap.isEmpty()) {
					clientConnection = conn;
					IncomeMessageListener proxy = makeMessageListenerProxy();
					messageReaderThread = new ClientMessageReaderThred(clientConnection, proxy);
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
			public void massageReceived(TextMessage message) {
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
		
		ClientConnection conn = new ClientConnection();
		try {
			conn.writeObject(regCmd);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		Object serverAnswer = false;
		try {
			serverAnswer = conn.readObject();	
			conn.closeConnection();
		} catch (IOException e) {
			e.printStackTrace();
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
			clientConnection.writeObject(findComm);
		} catch (IOException e) { e.printStackTrace(); } 
		
	}
	
	


	@Override
	public void sendFredshipRequest(FriendshipRequestCommand message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void confirmFrendShip(Long senderId) {
		// TODO Auto-generated method stub
		
	}
	
	public void closeConnection() {
		this.clientConnection.closeConnection();
	}
	public void logOut() {
		enshureNotNullConnection();
		
		try {
			clientConnection.writeObject(new LogOutCommand());
			clientConnection.closeConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	

	

	
}