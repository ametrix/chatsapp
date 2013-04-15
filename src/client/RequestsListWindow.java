/**
 * 
 */
package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import shared.message.FriendshipRequestCommand;

/**
 * @author PDimitrov
 *
 */
@SuppressWarnings("serial")
public class RequestsListWindow extends JFrame {

	private JPanel contentPane = new JPanel();
	private JTextArea messageTextArea = new JTextArea();
	private JList<String> requestslist;
	
	private Connector connector;
	private List<FriendshipRequestCommand> friendRequests = Collections.synchronizedList(new LinkedList<FriendshipRequestCommand>());
	
	
	public RequestsListWindow(Connector connector) {
		this.connector = connector;
		
		setTitle("PAPS Communicator 0.0.1");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 467, 353);
		contentPane.setLayout(new BorderLayout());
		
		JPanel requestsPanel = makeListPanel();
		contentPane.add(requestsPanel, BorderLayout.CENTER);
		
		messageTextArea.setRows(2);
		messageTextArea.setEditable(false);
		requestsPanel.add(messageTextArea, BorderLayout.SOUTH);
		
		contentPane.add(makecontrolPanel(), BorderLayout.SOUTH);
		requestslist = new JList<String>(new DefaultListModel<String>());  
		requestslist.setBorder(new LineBorder(new Color(0, 0, 0)));
		requestslist.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int index = requestslist.getSelectedIndex();
				if(index < 0) {
					messageTextArea.setText("");
				} else {
					messageTextArea.setText(friendRequests.get(index).getMessage());
				}
			}
		});
		
		JScrollPane requestsListScrollbar = new JScrollPane(requestslist);
		requestsListScrollbar.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		requestsPanel.add(requestsListScrollbar, BorderLayout.CENTER);
		
		setContentPane(contentPane);
	}
	private JPanel makeListPanel() {
		TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLUE), "People, who wants to be your friends");
		JPanel chatPanel = new JPanel();
		chatPanel.setBorder(title);
		chatPanel.setLayout(new BorderLayout(5,5));
		chatPanel.setPreferredSize(new Dimension(250,200));
		return chatPanel;
	}
	private JPanel makecontrolPanel() {
		JPanel ctrPanel = new JPanel();
		ctrPanel.setLayout(new FlowLayout());
		JButton acceptButton = new JButton("Accept");
		acceptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendAnswerToSelectedRequest(true);
			}
		});
		
		JButton denyButton = new JButton("Deny");
		denyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendAnswerToSelectedRequest(false);
			}
		});
		
		ctrPanel.add(denyButton);
		ctrPanel.add(acceptButton);
		return ctrPanel;
	}
	
	private void sendAnswerToSelectedRequest(boolean accepted) {
		int selectedIndex = requestslist.getSelectedIndex();
		if(selectedIndex < 0) { // there is no selection
			return;
		}
		FriendshipRequestCommand com = friendRequests.get(selectedIndex);
		
		if(accepted) {
			com.setAccepted(true);
		} else {
			com.setDenied(true);
		}
		
		connector.sendFredshipRequest(com);
		friendRequests.remove(selectedIndex);
		refreshList();
	}
	
	public List<FriendshipRequestCommand> getFriendRequests() {
		return friendRequests;
	}
	
	public void refreshList() {
		DefaultListModel<String> model = new DefaultListModel<String>();
		int i = 0;
		
		synchronized (friendRequests) {
			for(FriendshipRequestCommand command : friendRequests){
				model.add(i, command.getSenderName());
				i++;
			}
		}
		
		requestslist.setModel(model);
		requestslist.repaint();
	}
	
	
}
