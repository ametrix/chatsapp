package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import client.Connector.FriendsStatusListener;
import client.Connector.FriendshipRequestListener;
import client.Connector.IncomeMessageListener;

import shared.SkypeStatus;
import shared.message.ClientToClientMessage;
import shared.message.FriendshipRequestCommand;
import shared.message.TextMessage;


@SuppressWarnings("serial")
public class FriendsListWindow extends JFrame {

	public static final String TITLE = "PAPS";
//	public static final Color BACKGROUND = new Color(175, 113,30);
	public static final Color BACKGROUND = new Color(113, 196,43);
	public static final Color SECOND_COLOR = new Color(242,243,158);
	
	private final long id;
	private final String userName;
	private Map<Long, String> friends;
	private Connector connector;
	
	private JPanel contentPane = new JPanel();
	private JList<Friend> friendsList;
	private DefaultListModel<Friend> friendslistModel;
	private List<ChatWindow> chatWindows = new LinkedList<ChatWindow>();
	private RequestsListWindow requestsListWindow = new RequestsListWindow(connector);
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
		
		setTitle(TITLE + "  user: "+userName);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 250, 431);
		//initialize contentPane
		contentPane.setBackground(BACKGROUND);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
	
		//add controll panel
		contentPane.add(makeControllPanel(), BorderLayout.NORTH);
		
		//add friends Panel
		JPanel friendsPanel = new JPanel();
		friendsPanel.setBackground(BACKGROUND);
		TitledBorder title_1 = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLUE), "Friends:");
		friendsPanel.setBorder(title_1);
		friendsPanel.setLayout(new BorderLayout(5,5));
		friendsPanel.setPreferredSize(new Dimension(200, 50));
		friendslistModel = new DefaultListModel<Friend>();  
		friendsList = new JList<Friend>(friendslistModel); 
		friendsList.setBackground(SECOND_COLOR);
		contentPane.add(friendsPanel, BorderLayout.CENTER);
		
		// add the friends to the list
		initFriendsList(friends);
		
		JScrollPane friendsListScrollbar = new JScrollPane(friendsList);
		friendsPanel.add(friendsListScrollbar, BorderLayout.CENTER);
		
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
		this.connector.registerFriendshipRequestListener(makeFriendshipRequestListener());
	}
	
	private JPanel makeControllPanel() {
		JPanel ctrPanel = new JPanel();
		ctrPanel.setBackground(BACKGROUND);
		ctrPanel.setLayout(new FlowLayout());
		ctrPanel.setSize(90, 65);
		
		BufferedImage searchButtonIcon = null;
		BufferedImage showInvitationsButtonIcon = null;
		try {
			searchButtonIcon = ImageIO.read(new File("images/address-book-search-icon.png"));
			showInvitationsButtonIcon = ImageIO.read(new File("images/comment-user-add-icon.png"));
		} catch (IOException e1) {  
			e1.printStackTrace();
		}
	
		
		JButton searchButton = new JButton("Search",new ImageIcon(searchButtonIcon));
		searchButton.setMargin(new Insets(0,0,0,1));
		searchButton.setFont(new Font(Font.SERIF, Font.PLAIN, 13));
		searchButton.setToolTipText("Find people");
		searchButton.setFocusable(false);
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SearchFriendsWindow win = new SearchFriendsWindow(connector, id, userName);
				win.setVisible(true);
			}
		});
		
		JButton showInvitationsButton = new JButton("Invitations", new ImageIcon(showInvitationsButtonIcon));
		showInvitationsButton.setMargin(new Insets(0,0,0,1));
		showInvitationsButton.setFont(new Font(Font.SERIF, Font.PLAIN, 13));
		showInvitationsButton.setFocusable(false);
		//	showInvitationsButton.setBorder(BorderFactory.createMatteBorder(5, 3, 5, 5, new Color(100,100,100)));
		showInvitationsButton.setMaximumSize(new Dimension(30, 15));
		showInvitationsButton.setToolTipText("Show invitations");
		showInvitationsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				requestsListWindow.setVisible(true);
			}
		});
		
		ctrPanel.add(showInvitationsButton);
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
								win.setOnOffStyle(false);
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
								win.setOnOffStyle(true);
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
		chatWindow.setOnOffStyle(selectionFromList.getStatus().equals(SkypeStatus.ONLINE));
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
			public void massageReceived(ClientToClientMessage message) {
				
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

	private FriendshipRequestListener makeFriendshipRequestListener() {
		return new FriendshipRequestListener() {
			@Override
			public void frendShipRequestReceived(FriendshipRequestCommand command) {
				requestsListWindow.getFriendRequests().add(command);
				requestsListWindow.refreshList();
			}
		};
	}
	
	private WindowListener makeChatWindowListener() {
		return new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				chatWindows.remove((ChatWindow)e.getWindow());
			}
		};
	}
}
