package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Map;

import shared.DefenceUtil;
import shared.MessageCounter;
import shared.message.ClientToClientMessage;
import shared.message.FindUsersCommand;
import shared.message.FriendshipRequestCommand;
import shared.message.KeepAliveMessage;
import shared.message.LogOutCommand;
import shared.message.StatusChagedCommandFactory;
import shared.message.StatusChangedCommand;

import common.server.DBOperator;
import common.server.UserRegistry;



/**
* ClientListener class listens for client messages and
* forwards them to ServerDispatcher.
*/
public class ClientListener extends Thread {

	private ClientData mClient;
	private ObjectInputStream mSocketReader;
	private DBOperator dbOperator;
	private UserRegistry<ClientData> userRegistry;
	private MessageCounter msgCounter;
	
	public ClientListener(MessageCounter msgCounter, ClientData aClient, ObjectInputStream in, DBOperator dbOperator, UserRegistry<ClientData> userRegistry) throws IOException {
		DefenceUtil.enshureArgsNotNull("The constructor arguments cant be Null!"
				,msgCounter , aClient, in, dbOperator, userRegistry
		);
		
		this.msgCounter = msgCounter;
		mClient = aClient;
		mClient.getSocket().setSoTimeout(Server.CLIENT_READ_TIMEOUT);
		mSocketReader = in;
		this.dbOperator = dbOperator;
		this.userRegistry = userRegistry;
	}
	
	/**
	* Until interrupted, reads messages from the client
	* socket, forwards them to the server dispatcher's
	* queue and notifies the server dispatcher.
	*/
	public void run() {
		try	{
			while(!isInterrupted()) {
				try {
					Object message = mSocketReader.readObject();
					msgCounter.messageReaded();
					
					if(message instanceof KeepAliveMessage) {
						
					} else if(message instanceof ClientToClientMessage) {
						handleClientToClientMessage((ClientToClientMessage)message);
					
					} else if(message instanceof FindUsersCommand) {
						
						handleFindUsersCommand((FindUsersCommand)message);
						
					} else if(message instanceof FriendshipRequestCommand) {
						
						handleFriendshipCommand((FriendshipRequestCommand)message);
						
					} else if(message instanceof LogOutCommand) {
						break;
					}
				} catch (SocketTimeoutException ste) {
					mClient.addMsgForSending(KeepAliveMessage.INSTANCE);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					mClient.addMsgForSending(KeepAliveMessage.INSTANCE);
				}
			}
		} catch (SocketException e) {
			System.out.println(" Connection reset: "+mClient.getUsername());
		} catch (Exception ioex) {
			// Problem reading from socket (broken connection)
			ioex.printStackTrace();
		}
		// Communication is broken. Interrupt both listener and sender threads
		handleLogoutCommand();
	}
	
	
	private void handleClientToClientMessage(ClientToClientMessage ctcMessage) {
		ClientData client = findClient(ctcMessage.getReceiverId());
		if(client != null) {
			client.addMsgForSending(ctcMessage);
		}
	}
	
	private void handleFriendshipCommand(FriendshipRequestCommand command) {
		if(command.isAccepted()) {
			dbOperator.addFriendship(command.getSenderId(), command.getReceiverId());
			// send status change commands
			ClientData receiver = mClient; // if a user is accepted the request the current user is the receiver
			ClientData sender = userRegistry.getClient(command.getSenderId());
			
			if(receiver != null) {// send status change to the receiver
				StatusChangedCommand statusComm = sender != null 
					? StatusChagedCommandFactory.makeOFFToONCommand(command.getSenderId())
					: StatusChagedCommandFactory.makeONToOFFCommand(command.getSenderId());
				
				receiver.addMsgForSending(statusComm);
			}
			
			if(sender != null) {
				StatusChangedCommand statusComm = receiver != null 
						? StatusChagedCommandFactory.makeOFFToONCommand(command.getReceiverId())
						: StatusChagedCommandFactory.makeONToOFFCommand(command.getReceiverId());
					
				sender.addMsgForSending(statusComm);
			}
		} else if(command.isDenied()) {
			dbOperator.deleteFriendshipRequests(command.getSenderId(), command.getReceiverId());
		} else {
			dbOperator.addFriendshipRequest(
					command.getSenderId()
					, command.getSenderName()
					, command.getReceiverId()
					, command.getReceiverName()
					, command.getMessage()
					, command.getDate()
			);
			// if the receiver is online send him the request
			ClientData receiver = userRegistry.getClient(command.getReceiverId());
			if(receiver != null) {
				receiver.addMsgForSending(command);
			}
		}
	}

	private ClientData findClient(Long id) {
		ClientData client = this.mClient.getClientFromDiscussions(id);
		if(client == null) {
			client = userRegistry.getClient(id);
			if(client == null) {
				return null;
			}
			this.mClient.addClientToDiscussions(client);
		}
		return client;
	}

	
	private void handleFindUsersCommand(FindUsersCommand findComm) {
		Map<Long, String> foundUsers = dbOperator.findUsers(findComm.getCriteria());
				
		FindUsersCommand resultCommand = new FindUsersCommand();
		resultCommand.setCriteria(findComm.getCriteria());
		resultCommand.setFoundUsers(foundUsers);
		mClient.addMsgForSending(resultCommand);
	}
	
	private void handleLogoutCommand() {
		userRegistry.deleteClient(this.mClient);
	}


}
