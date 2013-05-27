/**
 * 
 */
package client.comunication;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import shared.MessageCounter;

/**
 * @author PDimitrov
 */
public class ClientConnection {

	public static final String SERVER_HOSTNAME = "localhost";
	public static final int TIMEOUT = 60*1000;
	public static final int SERVER_PORT = 2002;

	private Socket socket;
	private ObjectInputStream mSocketReader;
	private ObjectOutputStream mSocketWriter;
	
	
	private MessageCounter msgCounter = new MessageCounter(){
		private static final long GS_AFTER_INTERACTIONS = 3000;
		
		private long msgInteractions;
		
		@Override
		public synchronized void messageReaded() {
			callGsIfNeeded();
		}

		@Override
		public synchronized void messageWrited() {
			callGsIfNeeded();
		}
		
		private void callGsIfNeeded() {
			msgInteractions++;
			if(msgInteractions < GS_AFTER_INTERACTIONS) {
				return;
			}
			System.gc();
			msgInteractions = 0;
		}
	};
	
	
	public ClientConnection() {
		openConnection();
	}
	
	public void openConnection() {
		// Connect to the chat server
		try {
			socket = new Socket(SERVER_HOSTNAME, SERVER_PORT);
			mSocketWriter = new ObjectOutputStream(socket.getOutputStream());
			mSocketWriter.flush();
			socket.setSoTimeout(TIMEOUT);
			InputStream in = socket.getInputStream();
			mSocketReader = new ObjectInputStream(in);
			
				
			System.out.println("Connected to server " +	SERVER_HOSTNAME + ":" + SERVER_PORT);
		} catch (Exception ioe) {
			System.err.println("Can not connect to " +	SERVER_HOSTNAME + ":" + SERVER_PORT);
			ioe.printStackTrace();
			System.exit(-1);
		}	
	}
	
	public void closeConnection() {
		try {
			if(socket != null) {
				socket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void writeObject(Object msg, boolean resetStream) throws IOException {
		synchronized (mSocketWriter) {
			if(resetStream) {
				mSocketWriter.reset();
			}
			this.mSocketWriter.writeObject(msg);
			this.mSocketWriter.flush();
			
			msgCounter.messageWrited();
		}
	}
	
	public Object readObject() throws ClassNotFoundException, IOException {
		synchronized (mSocketReader) {
			msgCounter.messageReaded();
			
			return mSocketReader.readObject();
			
		}
	}
	
}
