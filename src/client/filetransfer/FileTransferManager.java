/**
 * 
 */
package client.filetransfer;

import static shared.message.FileMessage.FileMessageType.ASK;
import static shared.message.FileMessage.FileMessageType.CANCEL;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import client.ChatInfo;
import client.Connector;

import shared.message.FileMessage;

/**
 * @author PDimitrov
 *
 */
public class FileTransferManager {

	private ChatInfo chatInfo;
	private Connector connector;
	
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
	
	public FileTransferManager(ChatInfo chatInfo, Connector connector) {
		this.chatInfo = chatInfo;
		this.connector = connector;
	}
	
	
	
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
	
	public long sendFile(File file) {
		if(file == null || !file.exists()) { // make sure the file exists!
			return -1;
		}
		
		//create and add new FileTransferSender
		FileTransferSender fileTr = new FileTransferSender(file, getNextFileTransferId(), connector, chatInfo.getUserId(), chatInfo.getFriendId());
		fileTr.addStatusChangeListener(statusListener);
		fileTransfers.put(fileTr.getTransferId(), fileTr);
		
		// send ASK message
		FileMessage fileMsg = new FileMessage(chatInfo.getUserId(), chatInfo.getFriendId(), fileTr.getTransferId(), ASK, null, 0);
		fileMsg.setFileName(file.getName());
		connector.sendClientToClientMessage(fileMsg, false);
		
		return fileTr.getTransferId();
	}
	
	public void receiveFileMessage(FileMessage msg) {
		
		if(msg.getType() == ASK) {
			FileTransferReceiver rec = new FileTransferReceiver(msg.getTransferId(), connector, chatInfo.getFriendId(), chatInfo.getUserId(), msg.getFileName());
			rec.addStatusChangeListener(statusListener);
			fileTransfers.put(msg.getTransferId(), rec);
			
			
			for(FileTransfersObserver obs : observrs) {
				obs.fileTransferCreated(rec);
			}  
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
		FileTransfer tr = fileTransfers.get(transferId);
		if(tr != null && tr instanceof FileTransferReceiver ) {
			((FileTransferReceiver)tr).userConfirm(file);
		}
	}
	
	public void cancelTransferReceive(long transferId) {
		FileTransfer tr = fileTransfers.get(transferId);
		if(tr != null && tr instanceof FileTransferReceiver ) {
			((FileTransferReceiver)tr).cancel(true);
		}
	}
	
	
	public void cancelAllTransfers() {
		for(FileTransfer fileTr : fileTransfers.values()) {
			fileTr.cancel(true);
		}
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
