/**
 * 
 */
package client.comunication;

import client.comunication.http.ClientHTTPConnection;
import client.comunication.socket.ClientSocketConnection;

/**
 * @author PDimitrov
 *
 */
public class ConnectionFactory {
	
	public static ClientConnection makeConnection(ConnectionType type) {
		if(type == ConnectionType.SOCKET) {
			return new ClientSocketConnection();
		}
		if(type == ConnectionType.HTTP) {
			return new ClientHTTPConnection();
		}
		
		return null;	
	}
}
