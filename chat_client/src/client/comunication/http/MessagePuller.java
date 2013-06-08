/**
 * 
 */
package client.comunication.http;


import shared.DefenceUtil;
import shared.message.KeepAliveMessage;

/**
 * @author PDimitrov
 *
 */
class MessagePuller extends Thread {
	
	private static final int SLEEP = 3000;
	
	private RequestSender requestSender;
	private KeepAliveMessage keepAliveMsg;

	public MessagePuller(RequestSender requestSender, long userId) {
		DefenceUtil.enshureArgsNotNull("", requestSender);
		
		this.requestSender = requestSender;
		keepAliveMsg = new KeepAliveMessage();
		keepAliveMsg.setSenderId(userId);
	}



	@Override
	public void run() {
		try {
			while (!isInterrupted()) {
				requestSender.sendMessage(keepAliveMsg);
			//	System.out.println("Message Puller: send keepAlive");
				sleep(SLEEP);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
