import static shared.message.FileMessage.FileMessageType.DATA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.junit.Test;

import shared.message.FileMessage;

/**
 * 
 */

/**
 * @author PDimitrov
 *
 */
public class FIleMessageSerializeTest {

	private long tr = 0;
	private long transferId = 5;
	private long senderID = 10;
	private long receiverID = 11; 
	private byte currVlue = 0;
	
	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	public void setUp() {
		PipedOutputStream pout = new PipedOutputStream();
		PipedInputStream pin = new PipedInputStream();
		
		try {
			pout.connect(pin);
			
			out = new ObjectOutputStream(pout);
			in = new ObjectInputStream(pin); 
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Seted up \n");
	}
	
	private byte[] generateBytes(byte[] arr) {
		for(int i=0; i<arr.length; i++) {
			arr[i] = currVlue;
			currVlue++;
		}
		return arr;
	}
	
	private void printArray(byte[] array) {
		System.out.print("[");
		for(byte b : array)
			System.out.print(b + ",");
		
		System.out.print("]");
	}
	
	public void testSerialize() {
		setUp();
		
		byte[] data = new byte[10];
		FileMessage msg = new FileMessage(
				senderID
				, receiverID
				, transferId
				, DATA
				, data
				, 0
		 );
		
		for(int i=0; i<11; i++) {
			
		//	msg.setTransferId(tr++);
			generateBytes(data);
			int readed = data.length;
			
			
			System.out.print("reded bytes: "); printArray(data);
			System.out.println();
			
			msg.setBytesFilled(readed);
			
			if(i == 24) {
				msg.setLast(true);
				System.out.println(" Last Sended ");
			}
			
			write(msg);
			FileMessage rMSg = read();
			System.out.print("Readed ");
			rMSg.print();
			System.out.println();
		}
	}
	
	private void write(FileMessage msg) {
		try {
			System.out.print("Write: ");
			msg.print();
			out.reset();
			out.writeObject(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private FileMessage read() {
		try {
			return (FileMessage)in.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		FIleMessageSerializeTest t = new FIleMessageSerializeTest();
		t.testSerialize();
	}
	
}
