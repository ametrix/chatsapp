/**
 * 
 */
package client.comunication.http;

import java.io.Serializable;
import java.util.LinkedList;


import client.Connector;
import client.comunication.ClientConnection;
import client.comunication.MessageReader;

import shared.message.ClientToClientMessage;
import shared.message.CommandMessage;
import shared.message.KeepAliveMessage;

/**
 * @author PDimitrov
 *
 */
class ClientMessageReader extends MessageReader implements ReceivedStorrage {
	
	private LinkedList<Serializable> readedQueue = new LinkedList<Serializable>();
	
	
	public ClientMessageReader(
			ClientConnection clientConnection
			, Connector.IncomeMessageListener txtMessagelistener
	) {
		super(clientConnection ,txtMessagelistener);
	}
	
	
	public synchronized void addQueue(ClientMessageReader queue) {
		readedQueue.addAll(queue.readedQueue);
		notify();
	}
	
	/**
	* Adds given message to the message queue and notifies
	* this thread (actually getNextMessageFromQueue method)
	* that a message is arrived. sendMessage is always called
	* by other threads (ServerDispatcher).
	*/
	@Override
	public synchronized void store(Serializable received) {
		readedQueue.add(received);
	//	System.out.println("MessageReader: Strored");
		notify();
	}
	/**
	* @return and deletes the next message from the message
	* queue. If the queue is empty, falls in sleep until
	* notified for message arrival by sendMessage method.
	*/
	protected synchronized Serializable getNextMessageFromQueue() throws InterruptedException {
		while (readedQueue.size()==0) {
			wait();
		}
		Serializable message = readedQueue.remove(0);
		return message;
	}
	
	
	/**
	* Until interrupted reads a text line from the reader
	* and sends it to the writer.
	*/
	@SuppressWarnings("unchecked")
	public void run() {
		try {
			while (!isInterrupted()) {
					Serializable data = getNextMessageFromQueue();
					if (data instanceof KeepAliveMessage) {
						
					} 
					else if(data instanceof ClientToClientMessage) {
						getIncomeClientMsglistener().massageReceived((ClientToClientMessage)data);
					}
					else if(data instanceof CommandMessage) {
						System.out.println("ClientReaderThread: readCommand class:"+data.getClass());
					
						synchronized (getCommandListeners()) {
							
							@SuppressWarnings("rawtypes")
							CommandListener listener = getCommandListeners().get(data.getClass());
							if(listener == null) {
								continue;
							}
							if(listener.useOnceOnly()) {
								 getCommandListeners().remove(listener.getClass());
							}
							listener.commandReceived(data);
						}
					}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start() {
		((ClientHTTPConnection)getClientConnection()).registerMessageReader(this);
		super.start();
	}

	
}
