package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import shared.MessageCounter;

import common.server.DBOperator;
import common.server.DatabaseOperator;
import common.server.UserRegistry;




public class Server {
	
	public static final int PORT = 2002;
	public static final int CLIENT_READ_TIMEOUT = 60*1000;
	
	private static ServerSocket mServerSocket;
	private static UserRegistry<ClientData> userRegistry;
	private static DBOperator dbOperator = new DatabaseOperator("jdbc:mysql://localhost/db", "pdimitrov", "pdimitrov");
	
	
	private static MessageCounter msgCounter = new MessageCounter(){
		private static final long GS_AFTER_WRITED = 2000;
		
		private long msgWrited;
		
		@Override
		public synchronized void messageReaded() {
		}

		@Override
		public synchronized void messageWrited() {
			msgWrited++;
			if(msgWrited < GS_AFTER_WRITED) {
				return;
			}
			System.gc();
			msgWrited = 0;
		}
	};
	
	
	public static void main(String[] args) {
		userRegistry = new UserRegistry<ClientData>(dbOperator);
		
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
				RegisterThread regThread = new RegisterThread(msgCounter, socket, dbOperator, userRegistry);
				regThread.run();
				System.out.println("RegisterThread run!");
		
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	//	userRegistry.destroy();
	}
	
}
