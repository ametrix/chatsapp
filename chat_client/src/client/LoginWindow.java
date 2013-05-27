package client;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.JButton;

import client.comunication.ConnectorImpl;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.Map.Entry;


@SuppressWarnings("serial")
public class LoginWindow extends JFrame {

	private JPanel contentPane = new JPanel();
	private JLabel userNameLabel;
	private JLabel passwordLabel;
	private JTextField userName;
	private JPasswordField password;
	private StatusListenerProxy statusListenerProxy = new StatusListenerProxy();
	private Connector con;

	
	public static void main(String[] args) {
		LoginWindow frame_welcome = new LoginWindow();
		frame_welcome.setVisible(true);
		
	}
	
	
	/**
	 * Create the frame.
	 */
	public LoginWindow() {
		con = new ConnectorImpl(statusListenerProxy);
		
		
		setResizable(false);
		setTitle("Welcome");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 277, 191);
		
		// initialize contentPane
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setBackground(FriendsListWindow.SECOND_COLOR);
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
		
		//add header label
		JLabel lblPleaseEnterYour = makeHeaderLabel(sl_contentPane);
		contentPane.add(lblPleaseEnterYour);
		
		//add username label
		userNameLabel = new JLabel("User Name:");
		sl_contentPane.putConstraint(SpringLayout.NORTH, userNameLabel, 6, SpringLayout.SOUTH, lblPleaseEnterYour);
		sl_contentPane.putConstraint(SpringLayout.WEST, userNameLabel, 0, SpringLayout.WEST, lblPleaseEnterYour);
		contentPane.add(userNameLabel);
		//initialize username textbox
		userName = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, userName, 6, SpringLayout.SOUTH, lblPleaseEnterYour);
		sl_contentPane.putConstraint(SpringLayout.WEST, userName, 39, SpringLayout.EAST, userNameLabel);
		sl_contentPane.putConstraint(SpringLayout.EAST, userName, -13, SpringLayout.EAST, contentPane);
		contentPane.add(userName);
		userName.setColumns(10);
		
		//add password label
		passwordLabel = new JLabel("Password :");
		sl_contentPane.putConstraint(SpringLayout.NORTH, passwordLabel, 29, SpringLayout.SOUTH, userNameLabel);
		sl_contentPane.putConstraint(SpringLayout.WEST, passwordLabel, 0, SpringLayout.WEST, lblPleaseEnterYour);
		contentPane.add(passwordLabel);
		// initialize password textbox
		password = new JPasswordField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, password, -3, SpringLayout.NORTH, passwordLabel);
		sl_contentPane.putConstraint(SpringLayout.WEST, password, 0, SpringLayout.WEST, userName);
		sl_contentPane.putConstraint(SpringLayout.EAST, password, 0, SpringLayout.EAST, userName);
		contentPane.add(password);
		
		// initialize create button
		JButton buttonNewAccount = new JButton("New Account");
		buttonNewAccount.addActionListener(makeCreateListener());
		
		// add create button
		sl_contentPane.putConstraint(SpringLayout.WEST, buttonNewAccount, 0, SpringLayout.WEST, lblPleaseEnterYour);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, buttonNewAccount, -10, SpringLayout.SOUTH, contentPane);
		contentPane.add(buttonNewAccount);
		
		// initialize login button
		JButton buttonSignin = new JButton("Sign IN");
		buttonSignin.addActionListener(makeLoginListener());
		//add login button
		sl_contentPane.putConstraint(SpringLayout.NORTH, buttonSignin, -33, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, buttonSignin, -89, SpringLayout.EAST, userName);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, buttonSignin, -10, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, buttonSignin, 0, SpringLayout.EAST, userName);
		contentPane.add(buttonSignin);
	}
	
	private JLabel makeHeaderLabel(SpringLayout parentLayout) {
		// initialize header Label
		JLabel lblPleaseEnterYour = new JLabel("Please enter your Name and password :");
		parentLayout.putConstraint(SpringLayout.NORTH, lblPleaseEnterYour, 5, SpringLayout.NORTH, contentPane);
		parentLayout.putConstraint(SpringLayout.WEST, lblPleaseEnterYour, 5, SpringLayout.WEST, contentPane);
		return lblPleaseEnterYour;
	}
	
	private ActionListener makeCreateListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean result=con.createNewProfile(userName.getText(), new String(password.getPassword()));
				if (result == true){
					userNameLabel.setForeground(new Color(150, 250, 150));
					
				} else {
					userNameLabel.setForeground(new Color(250, 150, 150));
					userName.setBackground(new Color(250, 190, 190));			
				}
			}
		};
	}

	private ActionListener makeLoginListener() {
		return new ActionListener() {
			//Sign In button
			public void actionPerformed(ActionEvent e) {
				String name = userName.getText();
				String pass = new String(password.getPassword());
				Map<Long,String> friendsMap = con.login(name, pass);
				
				if(friendsMap == null) {
					userNameLabel.setForeground(new Color(250, 150, 150));
					userName.setBackground(new Color(250, 190, 190));	
					passwordLabel.setForeground(new Color(250, 150, 150));
					password.setBackground(new Color(250, 190, 190));	
					return;
				}
				
				Long id = findId(name, friendsMap);
				if(id == null) {
					System.out.println("  !!! Loging succed but cant find the ID for the username !!!");
					throw new RuntimeException("  !!! Loging succed but cant find the ID for the username !!!");
				}
				
				FriendsListWindow  window_friends = new FriendsListWindow (con,statusListenerProxy, name, id, friendsMap);//getWelcomeInstance());
				window_friends.setVisible(true);
				LoginWindow.this.dispose();
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
