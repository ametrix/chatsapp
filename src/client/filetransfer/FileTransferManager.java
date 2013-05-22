/**
 * 
 */
package client.filetransfer;

import static shared.message.FileMessage.FileMessageType.ASK;
import static shared.message.FileMessage.FileMessageType.CANCEL;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import javax.swing.DefaultListModel;

import client.UIFileMessage;
import client.UIMessage;

import shared.message.FileMessage;

/**
 * @author PDimitrov
 *
 */
public class FileTransferManager {

	
	/**
	 * Contains the active FileTransferSender instances
	 */
	private TreeMap<Long, FileTransfer> fileTransfers = new TreeMap<Long, FileTransfer>();
	
	private List<FileTransfersObserver> observrs = new LinkedList<FileTransfersObserver>();
	
	
	private TransferStatusChangedListener statusListener = new TransferStatusChangedListener() {
		
		@Override
		public void statusChanged(FileTransfer transfer) {
			for(FileTransfersObserver obs : observrs) {
				obs.fileTransferStatusChanged(transfer);
			}
		}
	};
	
	/**
	 * FIXME moje da vuznikne koliziq ako user1 zapo4ne transfer kum user2 
	 * i predi user2 da e polu4il suob6tenieto su6to zapo4ne transfer kum use1
	 * @return
	 */
	private long getNextFileTransferId() {
		if(fileTransfers.isEmpty()) {
			return 1;
		}
		return fileTransfers.lastKey() + 1;
	}
	
	
	public void receiveFileMessage(FileMessage msg) {
	//	System.out.println("USER:"+userName+" receive:"+fileMsg.getType().name());
		
		if(msg.getType() == ASK) {
		/*	FileTransferReceiver rec = new FileTransferReceiver(msg.getTransferId(), con, friendId, userId);
			rec.addStatusChangeListener(statusListener);
			fileTransfers.put(msg.getTransferId(), rec);
			
			
			for(FileTransfersObserver obs : observrs) {
				obs.fileTransferCreated(rec);
			}  */
		}
		else if(msg.getType() == CANCEL) {
			
			FileTransfer tr = fileTransfers.remove(msg.getTransferId());
			if(tr != null) {
				tr.cancel(false);
			}
			
			for(FileTransfersObserver obs : observrs) {
				obs.fileTransferRemoved(tr);
			}
		}
		else {
			FileTransfer tr = fileTransfers.get(msg.getTransferId());
			tr.receivedMessage(msg);
		}
	}
	
	public void confirmTransferReceive(long transferId, File file) {
		
	}
	
	
	public void addFileTransferObserver(FileTransfersObserver observer) {
		if(observer != null) {
			observrs.add(observer);
		}
	}
	public void removeFileTransferObserver(FileTransfersObserver observer) {
		if(observer != null) {
			observrs.remove(observer);
		}
	}
}
