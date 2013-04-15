package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
//import java.awt.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.BorderFactory;
//import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import java.awt.Color;
//import java.awt.FlowLayout;
import javax.swing.JTextArea;
//import javax.swing.AbstractListModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
//import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.swing.border.MatteBorder;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JButton;

import shared.message.TextMessage;


import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;

@SuppressWarnings("serial")
public class ChatWindow extends JFrame {

	private String userName;
	private Long id;
	private String friendName;
	private Long friendId;
	
	private JPanel contentPane;
	private JTextArea inputTextArea;
	private JList<String> historyMsglist;
	private Connector con;
	
	/**
	 * Create the frame.
	 */
	public ChatWindow(Connector connector, String userName,Long id, String selected_friend, Long friendId) {
		this.con = connector;
		this.userName = userName;
		this.id = id;
		this.friendName = selected_friend;
		this.friendId = friendId;
		
		setTitle("PAPS Communicator 0.0.1");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		
		setBounds(100, 100, 467, 353);
		
		//initialize the contentPane
		initContentPanel();
		
	//	makeMenu();
		
		//west panel
		JPanel chatPanel = makeChatPanel();
		chatPanel.setBackground(FriendsListWindow.BACKGROUND);
		contentPane.add(chatPanel, BorderLayout.CENTER);
		
		historyMsglist = new JList<String>(new DefaultListModel<String>());  
		historyMsglist.setBackground(FriendsListWindow.SECOND_COLOR);
		historyMsglist.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		JScrollPane historyMsglistScrollbar = new JScrollPane(historyMsglist);
		historyMsglistScrollbar.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		chatPanel.add(historyMsglistScrollbar, BorderLayout.CENTER);
		
		initInputTextArea();
		JScrollPane sbrText = new JScrollPane(inputTextArea);
		sbrText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		chatPanel.add(sbrText, BorderLayout.SOUTH);
	}
	
	private JPanel makeChatPanel() {
		TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLUE), "Chat with "+ friendName+":");
		JPanel chatPanel = new JPanel();
		chatPanel.setBorder(title);
		chatPanel.setLayout(new BorderLayout(5,5));
		chatPanel.setPreferredSize(new Dimension(250,200));
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
	private TextMessage sendMessaga(String text) {
		// create and initialize new message
		 TextMessage message = new TextMessage ();
		 message.setSenderId(this.id);
		 message.setReceiverId(this.friendId);
		 message.setMessage(text);
		 Date date = new Date();
		 message.setDate(date);
		 // send the message
		 con.sendMessage(message);
		 
		 return message;
	}
	
	private void addMessageToHistoryList(String senderName, TextMessage msg) {
		String line = "<html><font color=\"#779966\">"+senderName+": "+get_Time()+"</font>- "+msg.getMessage()+"</html>";
		((DefaultListModel<String>)historyMsglist.getModel()).addElement(line);
		scrollDown(historyMsglist);
	}
	
	private void initContentPanel() {
		contentPane = new JPanel();
		contentPane.setBackground(FriendsListWindow.BACKGROUND);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(5, 5));
		setContentPane(contentPane);
	}
	
	private void makeMenu() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmFil = new JMenuItem("Connect");
		mnNewMenu.add(mntmFil);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mnNewMenu.add(mntmExit);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
			//default title and icon
			JOptionPane.showMessageDialog(ChatWindow.this, "Eggs are not supposed to be green.");
			}
		});
		mnHelp.add(mntmAbout);
		
	}
	
	//get system time
	public String get_Time() {
		Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    	//System.out.println( sdf.format(cal.getTime()) );
    	return sdf.format(cal.getTime());
	}
	
	//scrolls down the passed JList<String> variable
	public void scrollDown(JList<String> jl) {
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
		return id;
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

	public void receiveMessage(TextMessage msg) {
		addMessageToHistoryList(friendName, msg);
	}
}
