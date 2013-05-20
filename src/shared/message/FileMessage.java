/**
 * 
 */
package shared.message;


/**
 * @author PDimitrov
 */
public class FileMessage implements ClientToClientMessage {

	private static final long serialVersionUID = 3172303575650676195L;
	
	private Long senderId;
	private Long receiverId;
	
	private String fileName;
	private int transferId;
	
	private boolean last;
	private boolean ask;
	private boolean confirmed;
	
	private byte[] data;
	
	
	public FileMessage(Long senderId, Long receiverId, int transferId, boolean last, byte[] data) {
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.transferId = transferId;
		this.last = last;
		this.data = data;
	}

	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public Long getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(Long receiverId) {
		this.receiverId = receiverId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public boolean isLast() {
		return last;
	}

	public void setLast(boolean last) {
		this.last = last;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public int getTransferId() {
		return transferId;
	}

	public void setTransferId(int transferId) {
		this.transferId = transferId;
	}

	public boolean isAsk() {
		return ask;
	}

	public void setAsk(boolean ask) {
		this.ask = ask;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}
	
}
