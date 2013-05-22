/**
 * 
 */
package filetransfer;

import static filetransfer.FileTransferStatus.COMPLETED;
import static filetransfer.FileTransferStatus.FAILED;
import static filetransfer.FileTransferStatus.IN_PROCESS;
import static filetransfer.FileTransferStatus.WAITING;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import shared.message.FileMessage;

import client.Connector;

/**
 * @author PDimitrov
 *
 */
public class FileTransferReceiver extends FileTransfer{

	private FileOutputStream outputFileStream;
	
	
	public FileTransferReceiver(long id, Connector connector, long senderId, long receiverId) {
		super(id, connector, senderId, receiverId);
	}

	
	public void userConfirm(File outputFile) {
		if(getStatus() != WAITING) { // the user can confirm only WAITING transfer in which he is the receiver
			return;
		}
		
		if(outputFile == null || !outputFile.exists()) {
			throw new RuntimeException(" The output file for this transfer does not exist");
		}
		
		try {
			outputFileStream = new FileOutputStream(outputFile);
		} catch (FileNotFoundException e) {
			setStatus(FAILED);
			e.printStackTrace();
			return;
		}
		
		setStatus(IN_PROCESS);
		// 		the places of the sender and receiver ids are switched because the (file receiver) is the sender of this message
		FileMessage msg = new FileMessage(getReceiverId(), getSenderId(), getTransferId(), FileMessage.FileMessageType.CONFIRM, null, 0);
		getConnector().sendClientToClientMessage(msg);
	}
	
	void receiveData(byte[] data, int filledBytes, boolean last) {
		try {
			if(filledBytes != 0 && filledBytes != data.length) {
				outputFileStream.write(data, 0, filledBytes);
			}
			else {
				outputFileStream.write(data);
			}
			
			if(last) {
				// TODO notifi the chatwindow that the transefer is completed so it can be removed from fileTransfers
				outputFileStream.flush();
				outputFileStream.close();
				setStatus(COMPLETED);
				System.out.println("Transfer completed");
			}
		} catch (IOException e) {
			cancel(true);
			e.printStackTrace();
		}
	}
	
	public void cancel(boolean sendMessage) {
		try {
			if(outputFileStream != null) {
				outputFileStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
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
	
}
