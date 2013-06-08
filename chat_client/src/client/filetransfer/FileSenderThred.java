/**
 * 
 */
package client.filetransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static shared.message.FileMessage.FileMessageType.DATA;
import shared.message.FileMessage;

/**
 * @author PDimitrov
 *
 */
public class FileSenderThred extends Thread{
	
	private static int CHUNK_SIZE = 2048;
	
	private FileTransferSender transfer;
	private FileInputStream inputStream;
	
	
	public FileSenderThred(File file, FileTransferSender transfer) {
		this.transfer = transfer;
		
		if(file == null || !file.exists()) {
			throw new RuntimeException("Not existing file to read from!");
		}
		
		try {
			inputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}



	@Override
	public void run() {
		try {
			
			if(inputStream == null) {
				return;
			}
			
			
			
			while (!isInterrupted()) {
				byte[] bytes = new byte[CHUNK_SIZE];
				FileMessage msg = new FileMessage(
						transfer.getSenderId()
						, transfer.getReceiverId()
						, transfer.getTransferId()
						, DATA
						, bytes
						, 0
				);
				int readed = inputStream.read(bytes);
			//	System.out.println("reded bytes: "); printArray(bytes);
				msg.setBytesFilled(readed);
				
				if(readed < CHUNK_SIZE) {
					msg.setLast(true);
					interrupt();
					System.out.println(" Last Sended ");
				}
		//		System.out.print("Sended: "); msg.print();
				transfer.getConnector().sendClientToClientMessage(msg, true);
				if(readed < CHUNK_SIZE) {
					transfer.completed();
				}
			}
		} catch(Exception e) {}
	}
	
	private void clearArray(byte[] array) {
		for(int i=0; i<CHUNK_SIZE; i++) {
			array[i] = 0;
		}
	}
	private void printArray(byte[] array) {
		System.out.print("[");
		for(byte b : array)
			System.out.print(b + ",");
		
		System.out.print("]");
	}
}
