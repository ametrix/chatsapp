/**
 * 
 */
package client;

import java.util.Date;

import client.filetransfer.FileTransferStatus;

/**
 *  Represents a message for file transfer shown in the chat window
 * @author PDimitrov
 */
public class UIFileMessage extends UIMessage {
	
	public enum Type {
		WAITING {
			protected String getText() {
				return "*Waiting to send File*";
			}
		}
		, SENDING {
			protected String getText() {
				return "*Sending File*";
			}
		}
		, RECEIVING {
			protected String getText() {
				return "*Receiving File*";
			}
		}
		, COMPLETED {
			protected String getText() {
				return "*Transfer Completed*";
			}
		}
		, FAILD {
			protected String getText() {
				return "*Transfer Faild*";
			}
		} ;
		
		protected abstract String getText();
	}
	
	
	private long transferId;
	private FileTransferStatus status;
	private String fileName;
	
	public UIFileMessage(long fileTransferID, String fileName, String autor, Date date, FileTransferStatus status, Type type) {
		super(type.getText()+" "+fileName, autor, date);
		this.transferId = fileTransferID;
		this.fileName = fileName;
		this.status = status;
	}
	
	public long getTransferId() {
		return transferId;
	}

	public FileTransferStatus getStatus() {
		return status;
	}

	public String getFileName() {
		return fileName;
	}
	
	public UIFileMessage copyForType(FileTransferStatus status, Type type) {
		return new UIFileMessage(transferId, fileName, getAutor(), getDateTime(), status, type);
	}
}
