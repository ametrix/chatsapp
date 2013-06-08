package client.comunication;

import java.io.IOException;
import java.io.Serializable;

import client.Connector.IncomeMessageListener;

public interface ClientConnection {
	
	public void writeObject(Serializable msg, boolean resetStream) throws IOException;
	
	public Object readObject() throws ClassNotFoundException, IOException;
	
	public Object writeRead(Serializable msg, boolean resetStream) throws ClassNotFoundException, IOException;
	
	public MessageReader makeMessageReader(IncomeMessageListener listener, long userId);
	
	public void closeConnection();
}
