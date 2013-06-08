/**
 * 
 */
package client.comunication.socket;

import java.io.IOException;
import java.net.SocketTimeoutException;

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
class ClientMessageReaderThred extends MessageReader {
	
	
	public ClientMessageReaderThred(
			ClientConnection clientConnection
			, Connector.IncomeMessageListener txtMessagelistener
	) {
		super(clientConnection, txtMessagelistener);
	}
	
	
	
	/**
	* Until interrupted reads a text line from the reader
	* and sends it to the writer.
	*/
	@SuppressWarnings("unchecked")
	public void run() {
		System.out.println("ClientMessageReaderThred   Started, Cnnection:"+getClientConnection().getClass().getName());
		try {
			while (!isInterrupted()) {
				try{
					Object data = getClientConnection().readObject();
					System.out.println("ClientMessageReader raded:"+data.getClass().getName());
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
				} catch(SocketTimeoutException e) {
					getClientConnection().writeObject(KeepAliveMessage.INSTANCE, false);
				}
			}
		
		} catch (IOException ioe) { 
			System.err.println("Lost connection to server.");
			ioe.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
