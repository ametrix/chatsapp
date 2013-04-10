/**
 * 
 */
package client.comunication;


import java.util.Date;
import java.util.Map;


import junit.framework.TestCase;

import org.junit.Before;

import shared.message.TextMessage;

import client.Connector.IncomeMessageListener;

/**
 * @author gmt3
 *
 */
public class ConnectorImplTest extends TestCase {

	private ConnectorImpl conn;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	//	conn = new ConnectorImpl();
	}

	/*
	public void testLogin() {
		
		boolean success = conn.createNewProfile("new", "newP");
		Map<Long, String> newfrMap = conn.login("new", "newP");
		assertTrue(newfrMap.containsValue("new"));
		
		
		Map<Long, String> frMap = conn.login("ivan", "ivanP");
		
		assertTrue(frMap.containsValue("ivan"));
		assertTrue(frMap.containsValue("dragan"));
		conn.closeConnection();
		
	}
*/


//	/*	
	public void testFindUsers() {
		conn.login("ivan", "ivanP"); // make sure that the conn has open connection 
		
	//	Map<Long, String> result = conn.findNewFriends("an");
	//	assertTrue(result.containsValue("ivan"));
	//	assertTrue(result.containsValue("dragan"));
	}   
//	  */
	
	/*
	public void testSendMessage() {
		conn.login("ivan", "ivanP"); // make sure that the conn has open connection 
		ConnectorImpl c2 = new ConnectorImpl();
		c2.login("dragan", "draganP");
		TextMessage msg = new TextMessage(2L,1L, "Hi", new Date() );
		
		conn.sendMessage(msg);
		c2.registerMessageListener(new IncomeMessageListener() {
			@Override
			public void massageReceived(TextMessage message) {
				System.out.println("Received_message:" + message.getMessage());
			}
		});
	}   
	*/
	
	
	
}
