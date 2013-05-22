/**
 * 
 */
package filetransfer;

import static filetransfer.FileTransferStatus.*;

import java.io.File;

import shared.message.FileMessage;
import client.Connector;

/**
 * @author PDimitrov
 *
 */
public class FileTransferSender extends FileTransfer {

	private FileSenderThred senderThread;
	private File file;
	
	public FileTransferSender(File file, long id, Connector connector, long senderId, long receiverId) {
		super(id, connector, senderId, receiverId);
		
		if(file == null || !file.exists()) {
			throw new RuntimeException("Not existing file to read from!");
		}
		
		this.file = file;
	}

	void messageConfirm() {
		
		// start the sender thread
		senderThread = new FileSenderThred(file, this);
		senderThread.start();
		
		setStatus(IN_PROCESS);
	}
	
	
	@Override
	public void cancel(boolean sendMessage) {
		if(senderThread != null) {
			senderThread.interrupt();
		}
		if(getStatus() == COMPLETED || getStatus() == FAILED) {
			sendMessage = false;
		}
		setStatus(FAILED);
		if(sendMessage) {
			FileMessage msg = new FileMessage(getSenderId(), getReceiverId(), getTransferId(), FileMessage.FileMessageType.CANCEL, null, 0);
			getConnector().sendClientToClientMessage(msg);
		}
	}

	@Override
	public void receivedMessage(FileMessage msg) {
		getStatus().receiveMsg(msg, this);
	}

	public void completed() {
		// TODO
	}
	
}
