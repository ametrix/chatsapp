package server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Vector;

import shared.DefenceUtil;
import shared.MessageCounter;
import shared.message.Message;

import common.server.UserRegistry;



/**
* Sends messages to the client. Messages waiting to be sent
* are stored in a message queue. When the queue is empty,
* ClientSender falls in sleep until a new message is arrived
* in the queue. When the queue is not empty, ClientSender
* sends the messages from the queue to the client socket.
*/
public class ClientSender extends Thread  {
	
	//TODO replace the Vector with synchronized LinkedList for better performance
	private Vector<Message> mMessageQueue = new Vector<Message>();
	private ClientData mClient;
	private ObjectOutputStream output;
	private UserRegistry<ClientData> userRegistry;
	private MessageCounter msgCounter;
	
	public ClientSender(MessageCounter msgCounter, ClientData aClient, ObjectOutputStream out, UserRegistry<ClientData> userRegistry) throws IOException {
		DefenceUtil.enshureArgsNotNull("The constructor arguments cant be Null!" , msgCounter, aClient,out,userRegistry);
		
		this.msgCounter = msgCounter;
		mClient = aClient;
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
	* @return and deletes the next message from the message
	* queue. If the queue is empty, falls in sleep until
	* notified for message arrival by sendMessage method.
	*/
	private synchronized Message getNextMessageFromQueue() throws InterruptedException {
		while (mMessageQueue.size()==0)
			wait();
		
		Message message = mMessageQueue.remove(0);
		return message;
	}
	
	/**
	* Sends given message to the client's socket.
	*/
	private void sendMessageToClient(Message aMessage) {
		try {
			output.writeObject(aMessage);
			output.flush();
			System.out.println("Sended to:"+mClient.getUsername()+"  type:"+aMessage.getClass());
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
				msgCounter.messageWrited();
			}
		} catch (Exception e) {
		// Communication problem
		}
		userRegistry.deleteClient(mClient); // removes this client, and interrupts his threads 
	}
	
}
