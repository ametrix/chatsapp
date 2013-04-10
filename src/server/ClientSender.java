package server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

import shared.message.KeepAliveMessage;
import shared.message.Message;



/**
* Sends messages to the client. Messages waiting to be sent
* are stored in a message queue. When the queue is empty,
* ClientSender falls in sleep until a new message is arrived
* in the queue. When the queue is not empty, ClientSender
* sends the messages from the queue to the client socket.
*/
public class ClientSender extends Thread {
	
	private Vector<Message> mMessageQueue = new Vector<Message>();
//	private ServerDispatcher mServerDispatcher;
	private ClientData mClient;
	private ObjectOutputStream output;
	private UserRegistry userRegistry;
	
	
	public ClientSender(ClientData aClient, ObjectOutputStream out, UserRegistry userRegistry) throws IOException {
		if(aClient == null || out == null || userRegistry == null) {
			throw new IllegalArgumentException("The constructor arguments cant be Null!");
		}
		
		mClient = aClient;
//		mServerDispatcher = aServerDispatcher;
		this.output = out;
		this.userRegistry = userRegistry;
	}
	
	
	/**
	* Adds given message to the message queue and notifies
	* this thread (actually getNextMessageFromQueue method)
	* that a message is arrived. sendMessage is always called
	* by other threads (ServerDispatcher).
	*/
	public synchronized void sendMessage(Message message) {
		System.out.println("Sended msg to:"+mClient.getUsername()+"  type:"+message.getClass());
		mMessageQueue.add(message);
		notify();
	}
	
	/**
	* Sends a keep-alive message to the client to check if
	* it is still alive. This method is called when the client
	* is inactive too long to prevent serving dead clients.
	*/
	public void sendKeepAlive() throws IOException{
		sendMessage(new KeepAliveMessage());
	}
	
	
	
	/**
	* @return and deletes the next message from the message
	* queue. If the queue is empty, falls in sleep until
	* notified for message arrival by sendMessage method.
	*/
	private synchronized Message getNextMessageFromQueue() throws InterruptedException {
		while (mMessageQueue.size()==0)
			wait();
		Message message = mMessageQueue.get(0);
		mMessageQueue.removeElementAt(0);
		return message;
	}
	
	/**
	* Sends given message to the client's socket.
	*/
	private void sendMessageToClient(Message aMessage) {
		try {
			output.writeObject(aMessage);
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	* Until interrupted, reads messages from the message queue
	* and sends them to the client's socket.
	*/
	public void run() {
		try {
			while (!isInterrupted()) {
				Message message = getNextMessageFromQueue();
				sendMessageToClient(message);
			}
		} catch (Exception e) {
		// Commuication problem
		}
		// Communication is broken. Interrupt both listener
		// and sender threads
	//	mClient.getClientListener().interrupt();
		userRegistry.deleteClient(mClient); // removes this client, and interrupts his threads 
	}
	
}
