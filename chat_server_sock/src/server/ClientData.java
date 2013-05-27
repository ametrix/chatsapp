package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import shared.DefenceUtil;
import shared.MessageCounter;



/**
* Client class contains information about a client,
* connected to the server.
*/
public class ClientData {
	private long id;
	private String username;
	private String password;
	private Socket socket = null;
	private ClientListener clientListener = null;
	private ClientSender clientSender = null;
	
	private Map<Long, ClientData> discussions = new HashMap<Long, ClientData>();
	
	
	public ClientData(Long id
					, String username
					, String password
					, Socket socket
					, ObjectInputStream in
					, ObjectOutputStream out
					, DBOperator dbOperator
					, UserRegistry userRegistry
					, MessageCounter msgCounter
	) throws IOException {
		
		DefenceUtil.enshureArgsNotNull("The constructor arguments cant be Null!"
				, id, username, password, socket, dbOperator, userRegistry
		);
		
		
		this.id = id;
		this.username = username;
		this.password = password;
		this.socket = socket;
		this.clientListener = new ClientListener(msgCounter, this, in, dbOperator, userRegistry);
		this.clientSender = new ClientSender(msgCounter, this, out, userRegistry);
		clientListener.start();
		clientSender.start();
	}
	

	public Socket getSocket() {
		return socket;
	}

	public ClientListener getClientListener() {
		return clientListener;
	}

	
	public ClientSender getClientSender() {
		return clientSender;
	}

	public long getId() {return id;}
	
	public String getUsername() {return username;}

	public String getPassword() {return password;}


	public ClientData getClientFromDiscussions(Long id) {
		return discussions.get(id);
	}
	
	public void addClientToDiscussions(ClientData client) {
		this.discussions.put(client.getId(), client);
	}
	
	public void stop() {
		clientListener.interrupt();
		clientSender.interrupt();
		try {
			if(socket != null && socket.isClosed()) {
				socket.close();
			}
		} catch (IOException e) {
			System.out.println("Error stopping client");
			e.printStackTrace();
		}
		
	}
}