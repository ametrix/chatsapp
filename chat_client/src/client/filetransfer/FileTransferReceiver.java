/**
 * 
 */
package client.filetransfer;

import static client.filetransfer.FileTransferStatus.*;

import java.io.BufferedOutputStream;
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

	
	private BufferedOutputStream outputFileStream;
	
	
	public FileTransferReceiver(long id, Connector connector, long senderId, long receiverId, String originFileName) {
		super(id, connector, senderId, receiverId, originFileName);
	}

	
	public void userConfirm(File outputFile) {
		if(getStatus() != WAITING) { // the user can confirm only WAITING transfer in which he is the receiver
			return;
		}
		
		if(outputFile == null || !outputFile.exists()) {
			throw new RuntimeException(" The output file for this transfer does not exist");
		}
		
		try {
			outputFileStream = new BufferedOutputStream(new FileOutputStream(outputFile));
		} catch (FileNotFoundException e) {
			setStatus(FAILED);
			e.printStackTrace();
			return;
		}
		
		setStatus(IN_PROCESS);
		// 		the places of the sender and receiver ids are switched because the (file receiver) is the sender of this message
		FileMessage msg = new FileMessage(getReceiverId(), getSenderId(), getTransferId(), FileMessage.FileMessageType.CONFIRM, null, 0);
		getConnector().sendClientToClientMessage(msg, false);
	}
	
	void receiveData(byte[] data, int filledBytes, boolean last) {
	//	printReceived(data);
		try {
			if(filledBytes != 0 && filledBytes != data.length) {
				outputFileStream.write(data, 0, filledBytes);
				outputFileStream.flush();
			}
			else {
				outputFileStream.write(data);
				outputFileStream.flush();
			}
			
			if(last) {
				
				// TODO notifi the chatwindow that the transefer is completed so it can be removed from fileTransfers
				outputFileStream.flush();
				outputFileStream.close();
				setStatus(COMPLETED);
				System.out.println("Last received Transfer completed");
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
			getConnector().sendClientToClientMessage(msg, false);
		}
	}
	
	@Override
	public void receivedMessage(FileMessage msg) {
		getStatus().receiveMsg(msg, this);
	}
	
	private void printReceived(byte[] array) {
		System.out.print("Received [");
		for(byte b : array)
			System.out.print(b + ",");
		
		System.out.print("]");
	}
}
