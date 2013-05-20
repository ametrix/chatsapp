package client;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import shared.message.ClientToClientMessage;
import shared.message.FileMessage;
import shared.message.TextMessage;


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
		
		setTitle(FriendsListWindow.TITLE);
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
		 con.sendTextMessage(message);
		 
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

	public void receiveMessage(ClientToClientMessage msg) {
		if(msg instanceof TextMessage) {
			addMessageToHistoryList(friendName, (TextMessage)msg);
		
		} else if(msg instanceof FileMessage) {
			
		}
		
	}
}
