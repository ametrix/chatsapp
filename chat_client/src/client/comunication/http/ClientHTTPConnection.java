/**
 * 
 */
package client.comunication.http;

import java.io.IOException;
import java.io.Serializable;

import client.Connector.IncomeMessageListener;
import client.comunication.ClientConnection;
import client.comunication.MessageReader;



/**
 * @author PDimitrov
 */
public class ClientHTTPConnection implements ClientConnection {
	
	public static final String URL = "http://localhost:8080/skype_ee/front";
	
	
	private ClientMessageReader messageReader;
	private RequestSender requestSender;
	private MessagePuller msgPuller;
	
	public ClientHTTPConnection() {
		messageReader = new ClientMessageReader(this, null);
		requestSender = new RequestSender(messageReader);
	
	}
	
	
	public synchronized Object readObject() throws ClassNotFoundException, IOException {
		try {
			return messageReader.getNextMessageFromQueue();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	@Override
	public void writeObject(Serializable msg, boolean resetStream) throws IOException {
		requestSender.sendMessage(msg);
	}

	@Override
	public Object writeRead(Serializable msg, boolean resetStream) throws ClassNotFoundException, IOException {
		return requestSender.writeRead(msg);
	}

	@Override
	public void closeConnection() {
		msgPuller.interrupt();
		requestSender.interrupt();
		messageReader.interrupt();
	}

	@Override
	public MessageReader makeMessageReader(IncomeMessageListener listener, long userId) {
		msgPuller = new MessagePuller(requestSender, userId);
		return new ClientMessageReader(this, listener);
	}
	
	synchronized void registerMessageReader(ClientMessageReader reader) {
		reader.addQueue(messageReader);
		this.messageReader = reader;
		this.requestSender.setReceivedStorrage(reader);
		msgPuller.start();
		requestSender.start();
	}
	
}
