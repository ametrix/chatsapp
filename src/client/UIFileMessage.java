/**
 * 
 */
package client;

import java.util.Date;

/**
 *  Represents a message for file transfer shown in the chat window
 * @author PDimitrov
 */
public class UIFileMessage extends UIMessage {
	
	private long transferId;
	
	public UIFileMessage(long fileTransferID, String fileName, String autor, Date date) {
		super("*Waiting to send File* "+fileName, autor, date);
		this.transferId = fileTransferID;
	}
	
	public long getTransferId() {
		return transferId;
	}
	
}
