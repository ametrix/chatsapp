package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import shared.DefenceUtil;
import shared.MessageCounter;
import shared.message.Message;

import common.server.ClientHolder;
import common.server.DBOperator;
import common.server.UserRegistry;



/**
* Client class contains information about a client,
* connected to the server.
*/
public class ClientData extends ClientHolder {
	
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
					, UserRegistry<ClientData> userRegistry
					, MessageCounter msgCounter
	) throws IOException {
		super(id, username, password);
		
		DefenceUtil.enshureArgsNotNull("The constructor arguments cant be Null!"
				, socket, dbOperator, userRegistry
		);
		
		
		this.socket = socket;
		this.clientListener = new ClientListener(msgCounter, this, in, dbOperator, userRegistry);
		this.clientSender = new ClientSender(msgCounter, this, out, userRegistry);
		clientListener.start();
		clientSender.start();
	}
	

	public Socket getSocket() {
		return socket;
	}


	public ClientData getClientFromDiscussions(Long id) {
		return discussions.get(id);
	}
	
	public void addClientToDiscussions(ClientData client) {
		this.discussions.put(client.getId(), client);
	}
	
	@Override
	public void destroy() {
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


	@Override
	public void addMsgForSending(Message msg) {
		clientSender.sendMessage(msg);
	}


	@Override
	public void handleReceivedMsg(Message msg) {
		// This method will never be invoked, as the CLientListener thread is the one who 
		// gets the receiver messages and handles them 
	}

}