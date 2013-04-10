package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import client.Connector.FriendsStatusListener;
import client.Connector.IncomeMessageListener;

import shared.SkypeStatus;
import shared.message.TextMessage;


@SuppressWarnings("serial")
public class FriendsListWindow extends JFrame {

	private final long id;
	private final String userName;
	private Map<Long, String> friends;
	private Connector connector;
	
	private JPanel contentPane = new JPanel();
	private JList<Friend> friendsList;
//	private JPanel panel;
	private DefaultListModel<Friend> friendslistModel;
	private List<ChatWindow> chatWindows = new LinkedList<ChatWindow>();
	
	/**
	 * Create the frame.
	 */
	public FriendsListWindow(Connector pConnector, StatusListenerProxy statusListenerProxy, String userName, Long id, Map<Long, String> friendsMap) {
		this.connector = pConnector;
	
		System.out.println("UserName:"+userName+"  id:"+id);
		
		this.id = id;
		this.userName = userName;
		friendsMap.remove(this.id); //remove the current user from the friendsMap so there are only his friends
		this.friends = friendsMap;
		
		setTitle("User:"+userName);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 217, 431);
		//initialize contentPane
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
	
		//add controll panel
		contentPane.add(makeControllPanel(), BorderLayout.NORTH);
		
		//add friends Panel
		JPanel friendsPanel = new JPanel();
		TitledBorder title_1 = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLUE), "Friends:");
		friendsPanel.setBorder(title_1);
		friendsPanel.setLayout(new BorderLayout(5,5));
		friendsPanel.setPreferredSize(new Dimension(200, 50));
		friendslistModel = new DefaultListModel<Friend>();  
		friendsList = new JList<Friend>(friendslistModel);  
		contentPane.add(friendsPanel, BorderLayout.CENTER);
		
		// add the friends to the list
		initFriendsList(friends);
		
		friendsPanel.add(friendsList, BorderLayout.CENTER);
		
		friendsList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				@SuppressWarnings({"unchecked" })
				JList<Friend> list = (JList<Friend>)event.getSource();
				if (event.getClickCount() == 2) {
				   openChatWindow(list.getSelectedValue());
				} 
			}
		});
		friendsList.setPreferredSize(new Dimension(150,200));
		//contentPane.add(panel_1, BorderLayout.EAST);
		
		// add listener last because the use other elements that need to be initialized 
		statusListenerProxy.setProxied(makeFriendsStatusListener());
		statusListenerProxy.statusChange(-1L, SkypeStatus.ONLINE); // flush previous commands
		this.connector.registerMessageListener(makeMessageListener());
	}
	
	private JPanel makeControllPanel() {
		JPanel ctrPanel = new JPanel();
		ctrPanel.setLayout(new FlowLayout());
		JButton searchButton = new JButton("Find Friends");
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SearchFriendsWindow win = new SearchFriendsWindow(connector, id, userName);
				win.setVisible(true);
			}
		});
		ctrPanel.add(searchButton);
		return ctrPanel;
	}
	
	private FriendsStatusListener makeFriendsStatusListener() {
		return new FriendsStatusListener() {
			@Override
			public void statusChange(Long friendId, SkypeStatus newStatus) {
				System.out.println(" !! Status msg receiv: userID:"+friendId+"  newStatus:"+newStatus.name());
				
				switch (newStatus) {
					case OFFLINE : {
						Friend friend = findListEntryById(friendId);
						if(friend != null) {
							friend.setStatus(newStatus);
							friendsList.setModel(friendslistModel);
							ChatWindow win = getActivChatWindow(friendId);
							if(win != null) {
								win.setWindowOnOffStyle(false);
							}
						}
					} break;
					case ONLINE : {
						Friend friend = findListEntryById(friendId);
						if(friend != null) {
							friend.setStatus(newStatus);
							friendsList.setModel(friendslistModel);
							friendsList.repaint();
						//	listModel.addElement(entry);
							ChatWindow win = getActivChatWindow(friendId);
							if(win != null) {
								win.setWindowOnOffStyle(true);
							}
						}
					} break;
				}
			}
		};
	}

	private Friend findListEntryById(Long id) {
		Enumeration<Friend> elements = friendslistModel.elements();
		
		while(elements.hasMoreElements()) {
			Friend friend = elements.nextElement();
			if(friend.getId().equals(id)) {
				return friend;
			}
		}
		return null;
	}
	
	private ChatWindow openChatWindow(Friend selectionFromList) {
		// dont'open more than 1 chat windows for the same friend
		if(selectionFromList == null || getActivChatWindow(selectionFromList.getId()) != null) {
			return null;
		}
		
		ChatWindow chatWindow = new ChatWindow (
					FriendsListWindow.this.connector
				,   FriendsListWindow.this.userName
				, 	FriendsListWindow.this.id
				,	selectionFromList.getName()
				,	selectionFromList.getId()
		);

		chatWindow.addWindowListener(makeChatWindowListener());
		FriendsListWindow.this.chatWindows.add(chatWindow);
		chatWindow.setVisible(true);
		return chatWindow;
	}
	private ChatWindow getActivChatWindow(Long friendId) {
		for(ChatWindow win : chatWindows) { 
			if(win.getFriendId().equals(friendId)) {
				return win;
			}
		}
		return null;
	}
	
	private void initFriendsList(Map<Long, String> friendsMap) {
		for (Entry<Long,String> entry : friendsMap.entrySet()) {
			Friend fr = new Friend(entry.getValue(), entry.getKey());
			friendslistModel.addElement(fr);
		}
	}
	
	private IncomeMessageListener makeMessageListener() {
		return new IncomeMessageListener(){
			@Override
			public void massageReceived(TextMessage message) {
				ChatWindow chatWin = getActivChatWindow(message.getSenderId());
				if(chatWin == null) {
					chatWin = openChatWindow(findListEntryById(message.getSenderId()));
				}
				if(chatWin != null) {
					chatWin.receiveMessage(message);
				}
			}
		};
		
	}

	
	private WindowListener makeChatWindowListener() {
		return new WindowListener() {
			@Override	public void windowOpened(WindowEvent e) {}
			@Override	public void windowIconified(WindowEvent e) {}
			@Override	public void windowDeiconified(WindowEvent e) {}
			@Override	public void windowDeactivated(WindowEvent e) {}
			
			@Override
			public void windowClosing(WindowEvent e) {
				chatWindows.remove((ChatWindow)e.getWindow());
			}
			
			@Override	public void windowClosed(WindowEvent e) {}
			@Override	public void windowActivated(WindowEvent e) {}
		};
	}
}
