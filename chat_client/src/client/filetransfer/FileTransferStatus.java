
package client.filetransfer;

import shared.message.FileMessage;
import shared.message.FileMessage.FileMessageType;

/**
 * @author PDimitrov
 *
 */
public enum FileTransferStatus {
	WAITING {
		
		/**
		 * In state of waiting the only valid message types for the sender are CONFIRM and CANCEL
		 */
		@Override
		public void receiveMsg(FileMessage msg, FileTransferSender transfer) {
			
			if(msg.getType() == FileMessageType.CONFIRM) {
				transfer.messageConfirm();
			} 
			else if(msg.getType() == FileMessageType.CANCEL) {
				transfer.cancel(false);
			}
			else {
				throw new RuntimeException("FileMessage of type:"+msg.getType()+" received durring state:"+WAITING);
			}			
		}
		
	}
	
	, IN_PROCESS {
		
		/**
		 * In state of processing the only valid message type for the sender is CANCEL
		 */
		@Override
		public void receiveMsg(FileMessage msg, FileTransferSender transfer) {
			if(msg.getType() == FileMessageType.CANCEL) {
				transfer.cancel(false);
			} 
			else {
				throw new RuntimeException("FileMessage of type:"+msg.getType()+" received durring state:"+IN_PROCESS);
			}			
		}
		
		/**
		 * In state of processing the only valid message type for the receiver is DATA
		 */
		public void receiveMsg(FileMessage msg, FileTransferReceiver transfer) {
			if(msg.getType() == FileMessageType.DATA) {
				transfer.receiveData(msg.getData(), msg.getBytesFilled(), msg.isLast());
			}
			else {
				throw new RuntimeException("FileMessage of type:"+msg.getType()+" received durring state:"+IN_PROCESS);
			}	
		}
	}
	
	, COMPLETED {}
	
	, FAILED {}
	;
	
	/**
	 * if not overridden - means in that state can't be received any messages
	 */
	public void receiveMsg(FileMessage msg, FileTransferSender transfer) {
		throw new RuntimeException("FileMessage of type:"+msg.getType()+" received durring state:"+this.name());
	}
	
	/**
	 * if not overridden - means in that state can't be received any messages
	 */
	public void receiveMsg(FileMessage msg, FileTransferReceiver transfer) {
	//	throw new RuntimeException("FileMessage of type:"+msg.getType()+" received durring state:"+this.name());
	}
}
