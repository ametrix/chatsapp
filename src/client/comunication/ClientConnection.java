/**
 * 
 */
package client.comunication;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import shared.message.Message;

/**
 * @author gmt3
 */
public class ClientConnection {

	public static final String SERVER_HOSTNAME = "localhost";
	public static String KEEP_ALIVE_MESSAGE = "!keep-alive";
	public static final int TIMEOUT = 30000;
	public static final int SERVER_PORT = 2002;

	private Socket socket;
	private ObjectInputStream mSocketReader;
	private ObjectOutputStream mSocketWriter;
	
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
	
//	public ObjectOutputStream getWriter() { return this.mSocketWriter; } 
//	public ObjectInputStream getReader() { return this.mSocketReader; } 
	
	public void writeObject(Object msg) throws IOException {
		synchronized (mSocketWriter) {
			this.mSocketWriter.writeObject(msg);
			this.mSocketWriter.flush();
		}
	}
	
	public Object readObject() throws ClassNotFoundException, IOException {
		synchronized (mSocketReader) {
			return mSocketReader.readObject();
		}
	}
	
}
