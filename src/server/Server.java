package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;




public class Server {

	public static final int PORT = 2002;
	public static String KEEP_ALIVE_MESSAGE = "!keep-alive";
	public static int CLIENT_READ_TIMEOUT = 50*1000;
	private static ServerSocket mServerSocket;
	private static UserRegistry userRegistry;
	private static DBOperator dbOperator = new DBImpl();
	
	public static void main(String[] args) {
		userRegistry = new UserRegistry(dbOperator);
		
		// Start listening on the server socket
		bindServerSocket();
		
		// Infinitely accept and handle client connections
		handleClientConnections();
	}
	
	private static void bindServerSocket() {
		try {
			mServerSocket = new ServerSocket(PORT);
			System.out.println("Server started on " + "port " + PORT);
		} catch (IOException ioe) {
			System.err.println("Can not start listening on " + "port " + PORT);
			ioe.printStackTrace();
			System.exit(-1);
		}
	}
	
	private static void handleClientConnections() {
		while(true) {
			try {
				
				Socket socket = mServerSocket.accept();
				RegisterThread regThread = new RegisterThread(socket, dbOperator, userRegistry);
				regThread.run();
				System.out.println("RegisterThread run!");
		
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	//	userRegistry.destroy();
	}
	
}
