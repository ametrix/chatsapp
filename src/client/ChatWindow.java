package client;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JTextArea;

import filetransfer.FileTransfer;
import filetransfer.FileTransferReceiver;
import filetransfer.FileTransferSender;
import filetransfer.FileTransferStatus;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.TreeMap;

import shared.message.ClientToClientMessage;
import shared.message.FileMessage;
import shared.message.TextMessage;

import static shared.message.FileMessage.FileMessageType.*;

@SuppressWarnings("serial")
public class ChatWindow extends JFrame {

	private String userName;
	private Long userId;
	private String friendName;
	private Long friendId;
	
	private JPanel contentPane;
	private JTextArea inputTextArea;
	private JList<UIMessage> historyMsglist;
	private Connector con;
	
	/**
	 * Contains the active FileTransferSender instances
	 */
	private TreeMap<Long, FileTransfer> fileTransfers = new TreeMap<Long, FileTransfer>();
	
	
	
	/**
	 * Create the frame.
	 */
	public ChatWindow(Connector connector, final String userName,Long id, String selected_friend, Long friendId) {
		this.con = connector;
		this.userName = userName;
		this.userId = id;
		this.friendName = selected_friend;
		this.friendId = friendId;
		
		setTitle(FriendsListWindow.TITLE);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				for(FileTransfer fileTr : fileTransfers.values()) {
					fileTr.cancel(true);
				}
			}
		});
		
		setBounds(100, 100, 497, 353);
		
		//initialize the contentPane
		initContentPanel();
		
		
		historyMsglist = new JList<UIMessage>(new DefaultListModel<UIMessage>());  
		historyMsglist.setBackground(FriendsListWindow.SECOND_COLOR);
		historyMsglist.setBorder(new LineBorder(new Color(0, 0, 0)));
		historyMsglist.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				UIMessage smsg = historyMsglist.getSelectedValue();
				
				if(!(smsg instanceof UIFileMessage) || smsg.getAutor().equals(userName)) {
					return;
				}
				
				FileTransferReceiver tr = getFileTransferReceiver((UIFileMessage)smsg);
				if(tr == null || tr.getStatus() != FileTransferStatus.WAITING) {
					return;
				}
				
				int choice = showFileAcceptDialog();
				
				if(choice == JOptionPane.YES_OPTION) {
					File outputFile = chooseOutputFile();
					if(outputFile != null) {
						try {
							outputFile.createNewFile();// creates a new file if file with that name does not exist
							tr.userConfirm(outputFile);
						} catch (IOException e1) {
							e1.printStackTrace();
						} 
					}
							
				} else if(choice == JOptionPane.NO_OPTION) {
					tr.cancel(true);
				}
				
			//	((DefaultListModel<String>)historyMsglist.getModel()).set(index, element);
				
			}
		});
		
		JScrollPane historyMsglistScrollbar = new JScrollPane(historyMsglist);
		historyMsglistScrollbar.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		initInputTextArea();
		JScrollPane sbrText = new JScrollPane(inputTextArea);
		sbrText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		JPanel chatPanel = makeChatPanel(historyMsglistScrollbar, sbrText);
		chatPanel.setBackground(FriendsListWindow.BACKGROUND);
		contentPane.add(chatPanel, BorderLayout.CENTER);
		
		contentPane.add(makeCommandPanel(), BorderLayout.SOUTH);
	}
	
	private void initContentPanel() {
		contentPane = new JPanel();
		contentPane.setBackground(FriendsListWindow.BACKGROUND);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(5, 5));
		setContentPane(contentPane);
	}
	
	private JPanel makeChatPanel(JScrollPane historyScroll, JScrollPane inputScroll) {
		TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLUE), "Chat with "+ friendName+":");
		JPanel chatPanel = new JPanel();
		chatPanel.setBorder(title);
		chatPanel.setLayout(new BorderLayout(5,5));
		chatPanel.setPreferredSize(new Dimension(250,200));
		
		chatPanel.add(historyScroll, BorderLayout.CENTER);
		chatPanel.add(inputScroll, BorderLayout.SOUTH);
		return chatPanel;
	}
	
	private void initInputTextArea() {
		//TextArea
		inputTextArea = new JTextArea();
		inputTextArea.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				inputTextArea.setText(null);
			}
		});
		inputTextArea.setText("Type your message here ... then press ENTER ...");
		inputTextArea.addKeyListener(makeInputTextAreaListener());
		inputTextArea.setLineWrap(true);
		inputTextArea.setRows(5);
		inputTextArea.setWrapStyleWord(true);
	}
	
	private KeyAdapter makeInputTextAreaListener() {
		return new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent keyEvent) {
				int key = keyEvent.getKeyCode();
				if (key == KeyEvent.VK_ENTER) {
					
					TextMessage msg = sendMessaga(inputTextArea.getText());
					
					inputTextArea.setCaretPosition(0);
	        	    inputTextArea.setText(null);
	        	    addMessageToHistoryList(ChatWindow.this.userName, msg);
	           }
			}
		};
	}
	
	private JPanel makeCommandPanel() {
		JPanel panel = new JPanel();
		panel.setBackground(FriendsListWindow.BACKGROUND);
		JButton sendButton = new JButton("Send file");
		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				int returned = fc.showOpenDialog(ChatWindow.this);
				if(returned == JFileChooser.APPROVE_OPTION) {
					sendFile(fc.getSelectedFile());
				}
				
			}
		});
		panel.add(sendButton);
		return panel;
	}
	
	private File chooseOutputFile() {
		
		JFileChooser fc = new JFileChooser();
		int returned = fc.showSaveDialog(ChatWindow.this);
		if(returned == JFileChooser.APPROVE_OPTION) {
			return fc.getSelectedFile();
		}
		return null;
	} 
	
	private void sendFile(File file) {
		TextMessage m = new TextMessage(0L, 0L, "*Waiting to send File* "+file.getName(), new Date());
		addMessageToHistoryList(userName, m);
		
		FileTransferSender fileTr = new FileTransferSender(file, getNextFileTransferId(), con, userId, friendId);
		fileTransfers.put(fileTr.getTransferId(), fileTr);
		FileMessage fileMsg = new FileMessage(userId, friendId, fileTr.getTransferId(), ASK, null, 0);
		fileMsg.setFileName(file.getName());
		
		con.sendClientToClientMessage(fileMsg);
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
	
	private int showFileAcceptDialog() {
		//Custom button text
		Object[] options = {"Yes",
		                    "No",
		                    "Close"};
		return JOptionPane.showOptionDialog(
							ChatWindow.this,
						    "Do you want to accept this file? ",
						    "Permision for transfer",
						    JOptionPane.YES_NO_CANCEL_OPTION,
						    JOptionPane.QUESTION_MESSAGE,
						    null,
						    options,
						    options[2]
		    		);
		
	}
	
	private FileTransferReceiver getFileTransferReceiver(UIFileMessage msg) {
		FileTransfer tr = fileTransfers.get(msg.getTransferId());
		if(tr instanceof FileTransferReceiver) {
			return (FileTransferReceiver)tr;
		}
		return null;
	}
	
 	private TextMessage sendMessaga(String text) {
		// create and initialize new message
		 TextMessage message = new TextMessage ();
		 message.setSenderId(this.userId);
		 message.setReceiverId(this.friendId);
		 message.setMessage(text);
		 Date date = new Date();
		 message.setDate(date);
		 // send the message
		 con.sendClientToClientMessage(message);
		 
		 return message;
	}
	
	private void addMessageToHistoryList(String senderName, TextMessage msg) {
		UIMessage message = new UIMessage(msg.getMessage(), senderName, new Date());
		
		((DefaultListModel<UIMessage>)historyMsglist.getModel()).addElement(message);
		scrollDown(historyMsglist);
	}
	
	
	//scrolls down the passed JList<String> variable
	private void scrollDown(JList<UIMessage> jl) {
		int lastIndex = jl.getModel().getSize() - 1;
        if (lastIndex >= 0)
        {
           jl.ensureIndexIsVisible(lastIndex);
        }
	}

	
	public String getUserName() {
		return userName;
	}

	public Long getId() {
		return userId;
	}

	public String getFriendName() {
		return friendName;
	}

	public Long getFriendId() {
		return friendId;
	}

	public void setOnOffStyle(boolean online) {
		if(online) { 
			// if the friend is online now, but was offline before that clear the text 
			setOnlineStyle(!inputTextArea.isEnabled());
		} else {
			setOfflineStyle();
		}
	}
	private void setOfflineStyle() {
		inputTextArea.setEnabled(false);
		
		inputTextArea.setBackground(new Color(20,120,10));
		inputTextArea.setText(friendName+" is offline!");
	}
	private void setOnlineStyle(boolean clearText) {
		if(clearText) {
			inputTextArea.setText("");
		}
		inputTextArea.setEnabled(true);
	}

	public void receiveMessage(ClientToClientMessage msg) {
		if(msg instanceof TextMessage) {
			addMessageToHistoryList(friendName, (TextMessage)msg);
		
		} 
		else if(msg instanceof FileMessage) {
			
			FileMessage fileMsg = (FileMessage)msg;
			System.out.println("USER:"+userName+" receive:"+fileMsg.getType().name());
			
			if(fileMsg.getType() == ASK) {
				FileTransferReceiver rec = new FileTransferReceiver(fileMsg.getTransferId(), con, friendId, userId);
				fileTransfers.put(fileMsg.getTransferId(), rec);
				
				UIFileMessage uiMsg= new UIFileMessage(fileMsg.getTransferId(), fileMsg.getFileName(), friendName, new Date());
				((DefaultListModel<UIMessage>)historyMsglist.getModel()).addElement(uiMsg);
				scrollDown(historyMsglist);
			}
			else if(fileMsg.getType() == CANCEL) {
				
				FileTransfer tr = fileTransfers.remove(fileMsg.getTransferId());
				if(tr != null) {
					tr.cancel(false);
				}
				
			}
			else {
				FileTransfer tr = fileTransfers.get(fileMsg.getTransferId());
				tr.receivedMessage(fileMsg);
			}
		}
		
	}
}
