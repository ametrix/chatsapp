package client.comunication.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import shared.message.FileMessage;



class RequestSender extends Thread {
	
	//TODO replace the Vector with synchronized LinkedList for better performance
	private Vector<Serializable> mMessageQueue = new Vector<Serializable>();
	private ReceivedStorrage receivedStorrage;
	
	
	public RequestSender(ReceivedStorrage receivedStorrage) {
		this.receivedStorrage = receivedStorrage;
	}

	
	protected synchronized ReceivedStorrage getReceivedStorrage() {
		return receivedStorrage;
	}

	protected synchronized void setReceivedStorrage(ReceivedStorrage receivedStorrage) {
		this.receivedStorrage = receivedStorrage;
	}



	/**
	* Adds given message to the message queue and notifies
	* this thread (actually getNextMessageFromQueue method)
	* that a message is arrived. sendMessage is always called
	* by other threads (ServerDispatcher).
	*/
	public synchronized void sendMessage(Serializable message) {
	//	System.out.println("Sended msg to:"+mClient.getUsername()+"  type:"+message.getClass());
		mMessageQueue.add(message);
		notify();
	}
		
	
	/**
	* @return and deletes the next message from the message
	* queue. If the queue is empty, falls in sleep until
	* notified for message arrival by sendMessage method.
	*/
	private synchronized Serializable getNextMessageFromQueue() throws InterruptedException {
		while (mMessageQueue.size()==0)
			wait();
		
		Serializable message = mMessageQueue.remove(0);
		return message;
	}
	
	/**
	* Sends given message to the client's socket.
	*/
	private void handleMessage(Serializable aMessage) {
	//	System.out.println("Sender: sneds:");
		List<Serializable> incoms = execUrlRequest(aMessage, false);
		System.out.println("Sender: received:"+incoms.size()+"  receivedStorrage=null:" + (receivedStorrage==null));
		if(receivedStorrage == null) {
			return;
		}
		
		for(Serializable msg : incoms) {
			receivedStorrage.store(msg);
		}
	}
	
	/**
	* Until interrupted, reads messages from the message queue
	* and handles them
	*/
	@Override
	public void run() {
		try {
			while (!isInterrupted()) {
				Serializable message = getNextMessageFromQueue();
				handleMessage(message);
			}
		} catch (Exception e) {
		// Communication problem
			e.printStackTrace();
		}
	}
	
	public Serializable writeRead(Serializable msg) {
		List<Serializable> incoms = execUrlRequest(msg, true);
		return incoms.get(0);
	}
	
	private List<Serializable> execUrlRequest(Serializable msg, boolean expectSingleResult) {
		HttpURLConnection connection = createConnection();
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		try {
			//Send request
			out = new ObjectOutputStream(connection.getOutputStream ());
			out.writeBoolean(expectSingleResult);
			out.writeObject(msg);
			out.flush();
			connection.connect();
			
				//Get Response	
			InputStream inStr = connection.getInputStream();
			in = new ObjectInputStream(inStr);
			int count = 0;
			if(in.available() > 0) {
				count = in.readInt();
			}
			if(count < 0) {
				throw new RuntimeException("Messages Count was:"+count); 
			}
			else if(count == 0) {
				return new ArrayList<Serializable>(1);
			}
			
			List<Serializable> incomingMsgs = new ArrayList<Serializable>(count);
			for(int i=0; i<count; i++) {
				incomingMsgs.add((Serializable)in.readObject());
			}
			
			inStr.close();
			return incomingMsgs;

	    } catch (Exception e) {

    		e.printStackTrace();
    		return null;
    	} finally {
    		
    		closeResources(connection, out, in);
    	}
	}
	
	private HttpURLConnection createConnection() {
		URL url;
		HttpURLConnection connection = null;  
		try {
	      //Create connection
			url = new URL(ClientHTTPConnection.URL);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		connection.setRequestProperty("Content-Type", "application/octet-stream");//"application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Length", "" + 100);
		connection.setRequestProperty("Content-Language", "en-US");  
	
		connection.setUseCaches (false);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		
		return connection;
	}

	private void closeResources(HttpURLConnection con, ObjectOutputStream out, ObjectInputStream in) {
		try {
			if(in != null) {
				in.close();
			}
			if(out != null) {
    			out.close();
    		}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(con != null) {
			con.disconnect(); 
		}
	}
	
}
