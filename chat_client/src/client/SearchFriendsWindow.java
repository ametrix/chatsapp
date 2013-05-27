/**
 * 
 */
package client;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.JButton;

import client.Connector.ResultCallBack;

import shared.message.FriendshipRequestCommand;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;



/**
 * @author PDimitrov
 *
 */
@SuppressWarnings("serial")
public class SearchFriendsWindow  extends JFrame {

//	private static final Color BACKGROUND = new Color(125, 158,254);
	

	private JPanel contentPane = new JPanel();
	private JLabel criteriaLabel;
	private JTextField criteria;
	private Connector con;
	private JList<Friend> usersList;
	private DefaultListModel<Friend> userslistModel;
	private Long id;
	private String userName;
	
	/**
	 * Create the frame.
	 */
	public SearchFriendsWindow(Connector connector, Long id, String name) {
		con = connector;
		this.id = id;
		this.userName = name;
		
		setResizable(false);
		setTitle(FriendsListWindow.TITLE);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 370, 391);
		
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
			
		contentPane.add(makeControllsPanel());
		
		JPanel peoplePanel = new JPanel();
		peoplePanel.setLayout(new BorderLayout());
		
		userslistModel = new DefaultListModel<Friend>();  
		usersList = new JList<Friend>(userslistModel); 
		usersList.setBackground(FriendsListWindow.SECOND_COLOR);
		contentPane.add(peoplePanel);
		
		JScrollPane peopleListScrollbar = new JScrollPane(usersList);
		peoplePanel.add(peopleListScrollbar, BorderLayout.CENTER);
		
		usersList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				@SuppressWarnings({"unchecked" })
				JList<Friend> list = (JList<Friend>)event.getSource();
				if (event.getClickCount() == 2) {
				   Friend fr = list.getSelectedValue();
				   if(fr != null) {
					   FriendshipRequestCommand com = new FriendshipRequestCommand();
					   com.setReceiverId(fr.getId());
					   com.setSenderId(SearchFriendsWindow.this.id);
					   com.setSenderName(SearchFriendsWindow.this.userName);
					   com.setDate(new Date());
					  
					  // con.sendFredshipRequest(com);
				   }
				} 
			}
		});
	}
	
	private JPanel makeControllsPanel() {
		// initialize controllsPane
		JPanel controllsPane = new JPanel();
		controllsPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		controllsPane.setBackground(FriendsListWindow.BACKGROUND);
		controllsPane.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		//add username label
		criteriaLabel = new JLabel("User Name:");
		controllsPane.add(criteriaLabel);
		//initialize username textbox
		criteria = new JTextField();
		controllsPane.add(criteria);
		criteria.setColumns(10);
		
		
		BufferedImage inviteButtonIcon = null;
		BufferedImage searchButtonIcon = null;
		try {
			inviteButtonIcon = ImageIO.read(new File("images/user-business-add-icon.png"));
			searchButtonIcon = ImageIO.read(new File("images/search-icon.png"));
			
		} catch (IOException e1) {  
			e1.printStackTrace();
		}
		
		// initialize and addcreate button
		JButton buttonSearch = new JButton("Search", new ImageIcon(searchButtonIcon));
		buttonSearch.setMargin(new Insets(0,0,0,1));
		buttonSearch.setFont(new Font(Font.SERIF, Font.PLAIN, 13));
		buttonSearch.addActionListener(makeSearchListener());
		controllsPane.add(buttonSearch);
		
		// initialize and add login button
		JButton buttonSend = new JButton("Invite", new ImageIcon(inviteButtonIcon));
		buttonSend.setMargin(new Insets(0,0,0,1));
		buttonSend.setFont(new Font(Font.SERIF, Font.PLAIN, 13));
		buttonSend.addActionListener(makeSendListener());
		controllsPane.add(buttonSend);
		
		return controllsPane;												
	}
	
	private ActionListener makeSearchListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				 ResultCallBack<Map<Long,String>> callBack = new ResultCallBack<Map<Long,String>>() {

					@Override
					public void resultReady(Map<Long, String> result) {
						for(Entry<Long, String> entry : result.entrySet()) {
							Friend fr = new Friend(entry.getValue(), entry.getKey());
							userslistModel.addElement(fr);
						}
					}
				};
				con.findNewFriends(criteria.getText(), callBack);
			//	boolean result=con.createNewProfile(userName.getText(), new String(password.getPassword()));
		/*		if (result == true){
					criteriaLabel.setForeground(new Color(150, 250, 150));
					
				} else {
					criteriaLabel.setForeground(new Color(250, 150, 150));
					criteria.setBackground(new Color(250, 190, 190));			
				}   */
			}  
		};
	}

	private ActionListener makeSendListener() {
		return new ActionListener() {
			//Sign In button
			public void actionPerformed(ActionEvent e) {
				
			}
		};
	}
	
	private Long findId(String name, Map<Long,String> frMap) {
		for(Entry<Long, String> entry : frMap.entrySet()) {
			if(entry.getValue().equals(name)) {
				return entry.getKey();
			}
		}
		return null;
	}
	


}
