/**
 * 
 */
package client.filetransfer;

import java.util.LinkedList;
import java.util.List;

import client.Connector;
import shared.message.FileMessage;
import static client.filetransfer.FileTransferStatus.*;

/**
 * @author PDimitrov
 */
public abstract class FileTransfer {
	
	private long transferId;
	private long senderId;
	private long receiverId;
	private String originFileName;
	private FileTransferStatus status;
	private Connector connector;
	
	private List<TransferStatusChangedListener> statusListeners = new LinkedList<TransferStatusChangedListener>();
	
	public FileTransfer(long id, Connector connector, long senderId, long receiverId, String originFileName) {
		this.transferId = id;
		this.connector = connector;
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.originFileName = originFileName;
		status = WAITING;
	}
	
	public long getTransferId() {
		return transferId;
	}
	
	public long getSenderId() {
		return senderId;
	}

	public long getReceiverId() {
		return receiverId;
	}

	public Connector getConnector() {
		return connector;
	}

	public FileTransferStatus getStatus() {
		return status;
	}
	
	public String getOriginFileName() {
		return originFileName;
	}

	public void setStatus(FileTransferStatus status) {
		if(this.status == status) {
			return;
		}
		
		this.status = status;

		for(TransferStatusChangedListener lis : statusListeners) {
			lis.statusChanged(this);
		}
	}

	public void addStatusChangeListener(TransferStatusChangedListener listener) {
		if(listener != null) {
			statusListeners.add(listener);
		}
	}
	public void removeStatusChangeListener(TransferStatusChangedListener listener) {
		if(listener != null) {
			statusListeners.remove(listener);
		}
	}
	
	
	public abstract void receivedMessage(FileMessage msg);
	
	public abstract void cancel(boolean sendMessage);
	
}
