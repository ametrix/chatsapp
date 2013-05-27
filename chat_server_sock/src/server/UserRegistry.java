/**
 * 
 */
package server;

import java.util.Map;
import java.util.TreeMap;

import shared.message.StatusChagedCommandFactory;
import shared.message.StatusChangedCommand;

/**
 * @author PDimitrov
 *
 */
public class UserRegistry {

	private DBOperator dbOperator;
	
	private TreeMap<Long, ClientData> usersMap = new TreeMap<Long, ClientData>();
	
	public UserRegistry(DBOperator dbOperator) {
		if(dbOperator == null) {
			throw new IllegalStateException(" DBOperator must not be Null! ");
		}
		this.dbOperator = dbOperator;
	}
	
	public synchronized void addClient(ClientData client) {
		ClientData previousLog = usersMap.get(client.getId());
		if(previousLog != null) {
			previousLog.stop();
		}
		usersMap.put(client.getId(), client);
	}
	public synchronized void deleteClient(ClientData client) {
		ClientData cl = usersMap.remove(client.getId());
		if(cl != null) {
			
			StatusChangedCommand statusCommand = StatusChagedCommandFactory.makeONToOFFCommand(cl.getId());
			
			Map<Long, String> friendsMap = dbOperator.getUserFriends(cl.getUsername(), cl.getPassword());
			for(Long friendId : friendsMap.keySet()) {
				ClientData friend = getClient(friendId);
				if(friend != null) {
					friend.getClientSender().sendMessage(statusCommand);
				}
			}
			
			cl.stop();
		}
	}
	
	public synchronized ClientData getClient(Long id) {
		return usersMap.get(id);
	}
	
	public synchronized boolean containsClient(Long id) {
		return usersMap.containsKey(id);
	}
	public long getSize() {return usersMap.size(); }
	
	public synchronized void destroy() {
		for(ClientData cl : usersMap.values()) {
			cl.stop();
		}
		usersMap = new TreeMap<Long, ClientData>();
	}
}
