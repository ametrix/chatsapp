/**
 * 
 */
package shared.message;

import java.io.IOException;


/**
 * @author PDimitrov
 */
public class FileMessage implements ClientToClientMessage {

	private static final long serialVersionUID = 1786087639098570208L;
	
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
		this.fileName = "";
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

	public void setTransferId(long transferId) {
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
/*
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeBoolean(last);
		out.writeLong(senderId);
		out.writeLong(receiverId);
		out.writeLong(transferId);
		out.writeInt(bytesFilled);
		out.writeInt(data.length);
		for(int i=0; i<data.length; i++) {
			out.writeByte(data[i]);
		}
		out.writeObject(type);
		out.writeObject(fileName);
	}
	
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
    	last = in.readBoolean();
    	senderId = in.readLong();
    	receiverId = in.readLong();
    	transferId = in.readLong();
    	bytesFilled = in.readInt();
    	int dataLength = in.readInt();
    	data = new byte[dataLength];
		for(int i=0; i<dataLength; i++) {
			data[i] = in.readByte();
		}
		type = (FileMessageType)in.readObject();
		fileName = (String)in.readObject();
    }
	*/
	public void print() {
		System.out.print("transferID: "+transferId);
		System.out.print("  filed: "+bytesFilled);
		System.out.print("  DATA [");
		for(byte b : data) {
			System.out.print(b + ",");
		}
		System.out.print("]");
		System.out.println("  last:"+last);
	}
	
}
