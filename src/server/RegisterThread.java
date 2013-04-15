/**
 * 
 */
package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import shared.message.LoginCommand;
import shared.message.RegisterCommand;
import shared.message.StatusChagedCommandFactory;
import shared.message.StatusChangedCommand;

/**
 * @author PDimitrov
 *
 */
public class RegisterThread extends Thread{
	
	private Socket clientSocket;
	private DBOperator dbOperator;
	private UserRegistry userRegistry;
	
	public RegisterThread(Socket clientSocket, DBOperator dbOperator, UserRegistry userRegistry) {
		if(clientSocket == null || dbOperator == null || userRegistry == null) {
			throw new IllegalArgumentException("The socket and the DBOperator cant be Null!");
		}
		this.dbOperator = dbOperator;
		this.clientSocket = clientSocket;
		this.userRegistry = userRegistry;
	}

	
	public void run() {
		try {
			clientSocket.setSoTimeout(30*1000); // timeout 30 seconds
			ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
			out.flush();
			ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
			
			Object obj = in.readObject();
		
			if(obj != null) {
				if(obj instanceof RegisterCommand) {
					RegisterCommand regCom = (RegisterCommand)obj;
					Boolean regSuccess = dbOperator.registerNewUser(regCom.getUserName(), regCom.getPassword());
					out.writeObject(regSuccess);
				
					System.out.println("Registered:"+regCom.getUserName()+"  pass:"+regCom.getPassword());
				} else if(obj instanceof LoginCommand) {
					LoginCommand logCom = (LoginCommand)obj;
					handleLogin(logCom, in, out);
					
					System.out.println("Login:"+logCom.getUserName()+"  pass:"+logCom.getPassword());
				}
				out.flush();
				
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (ClassNotFoundException e) { // required from readObject()
			e.printStackTrace();
		} 
	}
	
	private void handleLogin(LoginCommand logCom, ObjectInputStream in, ObjectOutputStream out) throws IOException {
		Map<Long, String> friendsMap = dbOperator.getUserFriends(logCom.getUserName(), logCom.getPassword());
		

		if(friendsMap == null) { // in login is not successful
			System.out.println("Unsuccessful login=");
			logCom.getLoginResult().clear();
			out.writeObject(logCom);
			out.flush();
		
		} else { 
			
			// login successful
			Long id = findClientId(friendsMap, logCom.getUserName());
			ClientData client = new ClientData(
					id
					, logCom.getUserName()
					, logCom.getPassword()
					, clientSocket
					, in
					, out
					, dbOperator
					, userRegistry
			);
			logCom.getLoginResult().putAll(friendsMap);
			out.writeObject(logCom);
			out.flush();
			
			sendStatusCommands(client, friendsMap.keySet());
			
			userRegistry.addClient(client);
			
			sentWaitingFriendShipRequests(client);
			System.out.println("UserRegistry.size="+userRegistry.getSize());
		}
		
	}
	private void sendStatusCommands(ClientData loggingUser, Collection<Long> friendsIds) {
		for(Long frId : friendsIds) {
			ClientData friend = userRegistry.getClient(frId);
			if(friend == null) continue;
			// notify the logging in user for all his online friends
			
			StatusChangedCommand stComm = StatusChagedCommandFactory.makeOFFToONCommand(friend.getId());
			loggingUser.getClientSender().sendMessage(stComm);
			
			//notify all the friends of the current logging in user that his is now online
			
			StatusChangedCommand stComm2 = StatusChagedCommandFactory.makeOFFToONCommand(loggingUser.getId());
			friend.getClientSender().sendMessage(stComm2);
		}
	}
	
	private void sentWaitingFriendShipRequests(ClientData client) {
		for( FriendshipRequest req : dbOperator.getFriendshipRequestsForUser(client.getId())) {
			client.getClientSender().sendMessage(req.asCommand());
		}
	}
	
	private Long findClientId(Map<Long, String> friendsMap, String userName) {
		for(Entry<Long, String> user : friendsMap.entrySet()) {
			if(user.getValue().equals(userName)) {
				return user.getKey();
			}
		}
		return null;
	}
}
