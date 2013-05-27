/**
 * 
 */
package client;

import static client.filetransfer.FileTransferStatus.*;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.border.LineBorder;

import client.UIFileMessage.Type;
import client.filetransfer.FileTransfer;

/**
 * @author PDimitrov
 *
 */
public class ChatHistoryList extends JList<UIMessage> {

	private static final long serialVersionUID = -7104061549968327433L;
	
	private Map<Long, Integer> iuFileMessageIndexes = new HashMap<Long, Integer>();
	
	
	
	public ChatHistoryList() {
		super(new DefaultListModel<UIMessage>());
		
		this.setBackground(FriendsListWindow.SECOND_COLOR);
		this.setBorder(new LineBorder(new Color(0, 0, 0)));
	}
	
	@Override
	public DefaultListModel<UIMessage> getModel() {
		return (DefaultListModel<UIMessage>)super.getModel();
	}
	
	/**
	 * Adds the UIMEssage to the list and returns the index of the message position in the list
	 * @param msg
	 * @return
	 */
	public void addUIFileMessage(UIFileMessage msg) {
		getModel().addElement(msg);
		iuFileMessageIndexes.put(msg.getTransferId(),getModel().lastIndexOf(msg));
		scrollDown();
	}
	
	public void addUIMessage(UIMessage msg) {
		getModel().addElement(msg);
		scrollDown();
	}
	
	private UIFileMessage getUIFileMessageByTransferId(long transferId) {
		Integer index = iuFileMessageIndexes.get(transferId);
		if(index == null) {
			return null;
		}
		
		UIMessage msg = getModel().getElementAt(index);
		if(msg != null && msg instanceof UIFileMessage) {
			return (UIFileMessage)msg;
		}
		
		return null;
	}
	
	public void updateTransferMsg(FileTransfer tr, String userName) {
		
		UIFileMessage oldmsg = getUIFileMessageByTransferId(tr.getTransferId());
		
		if(tr.getStatus() == IN_PROCESS) {
			
			if(oldmsg.getAutor().equals(userName)) { // this side is the sender
				changeFileMsg(oldmsg, tr, Type.SENDING);
			} 
			else { // this side is the receiver
				changeFileMsg(oldmsg, tr, Type.RECEIVING);
			}
		}
		else if(tr.getStatus() == COMPLETED) {
			changeFileMsg(oldmsg, tr, Type.COMPLETED);
		}
		else if(tr.getStatus() == FAILED) {
			changeFileMsg(oldmsg, tr, Type.FAILD);
		}
	}
	
	private void changeFileMsg(UIFileMessage oldMsg, FileTransfer tr, Type newType) {
		UIFileMessage newMsg = oldMsg.copyForType(tr.getStatus(), newType);
		getModel().set(iuFileMessageIndexes.get(tr.getTransferId()), newMsg);
	}
	
	//scrolls down the passed JList<String> variable
	public void scrollDown() {
		int lastIndex = getModel().getSize() - 1;
        if (lastIndex >= 0) {
           ensureIndexIsVisible(lastIndex);
        }
	}
}
