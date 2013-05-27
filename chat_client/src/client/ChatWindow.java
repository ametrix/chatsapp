package client;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JTextArea;

import client.filetransfer.FileTransfer;
import client.filetransfer.FileTransferManager;
import client.filetransfer.FileTransfersObserver;

import static client.filetransfer.FileTransferStatus.*;

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

import shared.message.ClientToClientMessage;
import shared.message.FileMessage;
import shared.message.TextMessage;


@SuppressWarnings("serial")
public class ChatWindow extends JFrame {

	private ChatInfo chatInfo;
	
	private JPanel contentPane;
	private JTextArea inputTextArea;
	private ChatHistoryList historyMsglist;
	private Connector con;
	
	private FileTransferManager transferManager;
	
	
	
	FileTransfersObserver transfrObserver = new FileTransfersObserver() {
		
		@Override
		public void fileTransferStatusChanged(FileTransfer tr) {
			historyMsglist.updateTransferMsg(tr, chatInfo.getUserName());
		}
		
		@Override
		public void fileTransferRemoved(client.filetransfer.FileTransfer tr) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void fileTransferCreated(client.filetransfer.FileTransfer rec) {

			UIFileMessage uiMsg= new UIFileMessage(
											rec.getTransferId()
											, rec.getOriginFileName()
											, chatInfo.getFriendName()
											, new Date()
											, WAITING
											, UIFileMessage.Type.WAITING
									);
			historyMsglist.addUIFileMessage(uiMsg);
		}
	};
	
	/**
	 * Create the frame.
	 */
	public ChatWindow(Connector connector, final ChatInfo chatInfo) {
		this.con = connector;
		this.chatInfo = chatInfo;
		this.transferManager = new FileTransferManager(chatInfo, connector);
		transferManager.addFileTransferObserver(transfrObserver);
		setTitle(FriendsListWindow.TITLE);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				transferManager.cancelAllTransfers();
			}
		});
		
		setBounds(100, 100, 497, 353);
		
		//initialize the contentPane
		initContentPanel();
		
		
		historyMsglist = new ChatHistoryList();  
		
		historyMsglist.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				UIMessage smsg = historyMsglist.getSelectedValue();
				
				if(!(smsg instanceof UIFileMessage)) {
					return;
				}
				UIFileMessage fUiMsg = (UIFileMessage)smsg;
				
				if(fUiMsg.getAutor().equals(chatInfo.getUserName())) { // this side is the sender
					
				}
				else { // this side is the receiver
					
					int choice = showFileAcceptDialog();
					
					if(choice == JOptionPane.YES_OPTION) {
						File outputFile = chooseOutputFile();
						if(outputFile != null) {
							try {
								outputFile.createNewFile();// creates a new file if file with that name does not exist
								transferManager.confirmTransferReceive(fUiMsg.getTransferId(), outputFile);
							} catch (IOException e1) {
								e1.printStackTrace();
							} 
						}
								
					} else if(choice == JOptionPane.NO_OPTION) {
						transferManager.cancelTransferReceive(fUiMsg.getTransferId());
					}
				}
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
		TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLUE), "Chat with "+ chatInfo.getFriendName()+":");
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
	        	    addMessageToHistoryList(ChatWindow.this.chatInfo.getUserName(), msg);
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
		long transferID = transferManager.sendFile(file);
		
		if(transferID == -1) { // transferSender was not created
			return;
		}
		
		
		UIFileMessage uiMsg = new UIFileMessage(
										transferID
										, file.getName()
										, chatInfo.getUserName()
										, new Date()
										, WAITING
										, UIFileMessage.Type.WAITING
									);	
		
		historyMsglist.addUIFileMessage(uiMsg);
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
	
	
 	private TextMessage sendMessaga(String text) {
		// create and initialize new message
		 TextMessage message = new TextMessage ();
		 message.setSenderId(this.chatInfo.getUserId());
		 message.setReceiverId(this.chatInfo.getFriendId());
		 message.setMessage(text);
		 message.setDate(new Date());
		 // send the message
		 con.sendClientToClientMessage(message, false);
		 
		 return message;
	}
	
	private void addMessageToHistoryList(String senderName, TextMessage msg) {
		
		UIMessage message = new UIMessage(msg.getMessage(), senderName, new Date());
		historyMsglist.addUIMessage(message);
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
		inputTextArea.setText(chatInfo.getFriendName()+" is offline!");
	}
	private void setOnlineStyle(boolean clearText) {
		if(clearText) {
			inputTextArea.setText("");
		}
		inputTextArea.setEnabled(true);
	}

	
	
	public void receiveMessage(ClientToClientMessage msg) {
		if(msg instanceof TextMessage) {
			addMessageToHistoryList(chatInfo.getFriendName(), (TextMessage)msg);
		
		} 
		else if(msg instanceof FileMessage) {
			FileMessage fileMsg = (FileMessage)msg;
			transferManager.receiveFileMessage(fileMsg);
		}
		
	}

	public ChatInfo getChatInfo() {
		return chatInfo;
	}

	public void setChatInfo(ChatInfo chatInfo) {
		this.chatInfo = chatInfo;
	}
	
}
