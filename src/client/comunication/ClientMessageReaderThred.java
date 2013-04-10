/**
 * 
 */
package client.comunication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


import client.Connector;

import shared.message.CommandMessage;
import shared.message.KeepAliveMessage;
import shared.message.TextMessage;

/**
 * @author PDimitrov
 *
 */
public class ClientMessageReaderThred extends Thread {
	
	public interface CommandListener<CommandType> {
		public Class<CommandType> getTargetCommandClass();
		public boolean useOnceOnly();
		public void commandReceived(CommandType command);
	}
	
	private Map<Class<?>, CommandListener<?>> commandListeners = Collections.synchronizedMap(new HashMap<Class<?>, CommandListener<?>>());
	
	private ClientConnection clientConnection;
	private Connector.IncomeMessageListener txtMessagelistener;
	
	
	public ClientMessageReaderThred(
			ClientConnection clientConnection
			, Connector.IncomeMessageListener txtMessagelistener
	) {
		this.clientConnection = clientConnection;
		this.txtMessagelistener = txtMessagelistener;
	}
	

	public void registerCommandListener(Class<?> clazz, CommandListener<?> listener) {
		commandListeners.put(clazz, listener);
	}
	
	
	/**
	* Until interrupted reads a text line from the reader
	* and sends it to the writer.
	*/
	public void run() {
		try {
			while (!isInterrupted()) {
				try{
					Object data = clientConnection.readObject();
					if (data instanceof KeepAliveMessage) {
						
					} else if(data instanceof TextMessage) {
						txtMessagelistener.massageReceived((TextMessage)data);
					} else if(data instanceof CommandMessage) {
						System.out.println("ClientReaderThread: readCommand class:"+data.getClass());
						CommandListener listener = commandListeners.get(data.getClass());
						if(listener == null) {
							continue;
						}
						if(listener.useOnceOnly()) {
							commandListeners.remove(listener.getClass());
						}
						listener.commandReceived(data);
					}
				} catch(SocketTimeoutException e) {
					clientConnection.writeObject(new KeepAliveMessage());
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