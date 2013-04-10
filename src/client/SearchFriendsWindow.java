/**
 * 
 */
package client;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.JButton;

import client.Connector.ResultCallBack;

import shared.message.FriendshipRequestCommand;


import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.NetPermission;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;



/**
 * @author PDimitrov
 *
 */
@SuppressWarnings("serial")
public class SearchFriendsWindow  extends JFrame {


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
		setTitle("Welcome");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 277, 391);
		
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(2,1));
			
		contentPane.add(makeControllsPanel());
		
		JPanel friendsPanel = new JPanel();
		friendsPanel.setLayout(new BorderLayout());
		
		userslistModel = new DefaultListModel<Friend>();  
		usersList = new JList<Friend>(userslistModel);  
		contentPane.add(friendsPanel);
		
		friendsPanel.add(usersList, BorderLayout.CENTER);
		
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
	
		SpringLayout sl_contentPane = new SpringLayout();
		controllsPane.setLayout(sl_contentPane);
		
		// initialize header Label
		JLabel lblPleaseEnterYour = new JLabel("Please enter your Name and password :");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblPleaseEnterYour, 5, SpringLayout.NORTH, controllsPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblPleaseEnterYour, 5, SpringLayout.WEST, controllsPane);
		controllsPane.add(lblPleaseEnterYour);
		
		//add username label
		criteriaLabel = new JLabel("User Name:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, criteriaLabel, 6, SpringLayout.SOUTH, lblPleaseEnterYour);
		sl_contentPane.putConstraint(SpringLayout.WEST, criteriaLabel, 0, SpringLayout.WEST, lblPleaseEnterYour);
		controllsPane.add(criteriaLabel);
		//initialize username textbox
		criteria = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, criteria, 6, SpringLayout.SOUTH, lblPleaseEnterYour);
		sl_contentPane.putConstraint(SpringLayout.WEST, criteria, 39, SpringLayout.EAST, criteriaLabel);
		sl_contentPane.putConstraint(SpringLayout.EAST, criteria, -13, SpringLayout.EAST, controllsPane);
		controllsPane.add(criteria);
		criteria.setColumns(10);
		
		// initialize create button
		JButton buttonSearch = new JButton("Search");
		buttonSearch.addActionListener(makeSearchListener());
		
		// add create button
		sl_contentPane.putConstraint(SpringLayout.WEST, buttonSearch, 0, SpringLayout.WEST, lblPleaseEnterYour);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, buttonSearch, -10, SpringLayout.SOUTH, controllsPane);
		controllsPane.add(buttonSearch);
		
		// initialize login button
		JButton buttonSend = new JButton("Send request");
		buttonSend.addActionListener(makeSendListener());
		//add login button
		sl_contentPane.putConstraint(SpringLayout.NORTH, buttonSend, -35, SpringLayout.SOUTH, controllsPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, buttonSend, -129, SpringLayout.EAST, criteria);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, buttonSend, -10, SpringLayout.SOUTH, controllsPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, buttonSend, 0, SpringLayout.EAST, criteria);
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
