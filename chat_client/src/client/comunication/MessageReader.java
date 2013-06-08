/**
 * 
 */
package client.comunication;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import client.Connector;

/**
 * @author PDimitrov
 *
 */
public abstract class MessageReader implements Runnable {

	public interface CommandListener<CommandType> {
		public Class<CommandType> getTargetCommandClass();
		public boolean useOnceOnly();
		public void commandReceived(CommandType command);
	}
	
	private Map<Class<?>, CommandListener<?>> commandListeners = Collections.synchronizedMap(new HashMap<Class<?>, CommandListener<?>>());
	
	private ClientConnection clientConnection;
	private Connector.IncomeMessageListener incomeClientMsglistener;
	private Thread thread = null;
	
	
	public MessageReader(
			ClientConnection clientConnection
			, Connector.IncomeMessageListener txtMessagelistener
	) {
		this.clientConnection = clientConnection;
		this.incomeClientMsglistener = txtMessagelistener;
	}
	

	public void registerCommandListener(Class<?> clazz, CommandListener<?> listener) {
		synchronized (commandListeners) {
			commandListeners.put(clazz, listener);
		}
	}

	public void start() {
		if(thread != null) {
			thread.interrupt();
		}
		thread = new Thread(this);
		thread.start();
	}
	
	public void interrupt() {
		if(thread != null) {
			thread.interrupt();
			thread = null;
		}
	}
	
	public boolean isInterrupted() {
		if(thread != null) {
			return thread.isInterrupted();
		}
		return true;
	}

	protected Map<Class<?>, CommandListener<?>> getCommandListeners() {
		return commandListeners;
	}

	protected ClientConnection getClientConnection() {
		return clientConnection;
	}

	protected Connector.IncomeMessageListener getIncomeClientMsglistener() {
		return incomeClientMsglistener;
	}
	
}
