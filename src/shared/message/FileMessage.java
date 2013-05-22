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
	private long transferId;
	
	private boolean last;
	private FileMessageType type;
	
	private byte[] data;
	private int bytesFilled;
	
	
	public enum FileMessageType {
		ASK
		, CONFIRM
		, CANCEL
		, DATA
	}
	
	
	public FileMessage(Long senderId, Long receiverId, long transferId, FileMessageType type, byte[] data, int bytesFilled) {
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.transferId = transferId;
		this.type = type;
		this.data = data;
		this.bytesFilled = bytesFilled;
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

	public long getTransferId() {
		return transferId;
	}

	public void setTransferId(int transferId) {
		this.transferId = transferId;
	}

	public FileMessageType getType() {
		return type;
	}

	public int getBytesFilled() {
		return bytesFilled;
	}

	public void setBytesFilled(int bytesFilled) {
		this.bytesFilled = bytesFilled;
	}

	
}
