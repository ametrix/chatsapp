package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Map;

import shared.DefenceUtil;
import shared.message.FindUsersCommand;
import shared.message.KeepAliveMessage;
import shared.message.LogOutCommand;
import shared.message.TextMessage;



/**
* ClientListener class listens for client messages and
* forwards them to ServerDispatcher.
*/
public class ClientListener extends Thread {

//	private ServerDispatcher mServerDispatcher;
	private ClientData mClient;
	private ObjectInputStream mSocketReader;
	private DBOperator dbOperator;
	private UserRegistry userRegistry;
	
	public ClientListener(ClientData aClient, ObjectInputStream in, DBOperator dbOperator, UserRegistry userRegistry) throws IOException {
		DefenceUtil.enshureArgsNotNull("The constructor arguments cant be Null!"
				, aClient, in, dbOperator, userRegistry
		);
		
		mClient = aClient;
//		mServerDispatcher = aSrvDispatcher;
		aClient.getSocket().setSoTimeout(Server.CLIENT_READ_TIMEOUT);
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
					System.out.println("ClientListener_readed:"+message.getClass());
				
					
					if(message instanceof KeepAliveMessage) {
						
					} else if(message instanceof TextMessage) {
						TextMessage txtMessage = (TextMessage)message;
						ClientData client = findClient(txtMessage.getReceiverId());
						
						// TODO check if the client is still active before send a message,
						// what happens if it is not
						client.getClientSender().sendMessage(txtMessage);
					
					} else if(message instanceof FindUsersCommand) {
						handleFindUsersCommand((FindUsersCommand)message);
					} else if(message instanceof LogOutCommand) {
						break;
					}
					
				//	mServerDispatcher.dispatchMessage(mClient, message);
				} catch (SocketTimeoutException ste) {
					mClient.getClientSender().sendKeepAlive();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					mClient.getClientSender().sendKeepAlive();
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
	private ClientData findClient(Long id) {
		ClientData client = this.mClient.getClientFromDiscussions(id);
		if(client == null) {
			client = userRegistry.getClient(id);
			this.mClient.addClientToDiscussions(client);
		}
		return client;
	}

	
	private void handleFindUsersCommand(FindUsersCommand findComm) {
		Map<Long, String> foundUsers = dbOperator.findUsers(findComm.getCriteria());
				
		FindUsersCommand resultCommand = new FindUsersCommand();
		resultCommand.setCriteria(findComm.getCriteria());
		resultCommand.setFoundUsers(foundUsers);
		mClient.getClientSender().sendMessage(resultCommand);
	}
	
	private void handleLogoutCommand() {
		userRegistry.deleteClient(this.mClient);
	}


}
