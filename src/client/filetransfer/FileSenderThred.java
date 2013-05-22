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
	
	private static int CHUNK_SIZE = 1024;
	
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
				int readed = inputStream.read(bytes);
				FileMessage msg = new FileMessage(
											transfer.getSenderId()
											, transfer.getReceiverId()
											, transfer.getTransferId()
											, DATA
											, bytes
											, readed
									  );
				
				if(readed < CHUNK_SIZE) {
					msg.setLast(true);
					interrupt();
				}
				
				transfer.getConnector().sendClientToClientMessage(msg);
				transfer.completed();
			}
		} catch(Exception e) {}
	}
}
