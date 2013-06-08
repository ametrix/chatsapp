/**
 * 
 */
package common.server;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import shared.message.StatusChagedCommandFactory;
import shared.message.StatusChangedCommand;

/**
 * @author PDimitrov
 *
 */
public class UserRegistry<Data extends ClientHolder> {

	private DBOperator dbOperator;
	
	private TreeMap<Long, Data> usersMap = new TreeMap<Long, Data>();
	
	public UserRegistry(DBOperator dbOperator) {
		if(dbOperator == null) {
			throw new IllegalStateException(" DBOperator must not be Null! ");
		}
		this.dbOperator = dbOperator;
	}
	
	public synchronized void addClient(Data client) {
		Data previousLog = usersMap.get(client.getId());
		if(previousLog != null) {
			previousLog.destroy();
		}
		usersMap.put(client.getId(), client);
	}
	public synchronized void deleteClient(Data client) {
		Data cl = usersMap.remove(client.getId());
		if(cl != null) {
			
			StatusChangedCommand statusCommand = StatusChagedCommandFactory.makeONToOFFCommand(cl.getId());
			
			Map<Long, String> friendsMap = dbOperator.getUserFriends(cl.getUsername(), cl.getPassword());
			for(Long friendId : friendsMap.keySet()) {
				Data friend = getClient(friendId);
				if(friend != null) {
					friend.addMsgForSending(statusCommand);
				}
			}
			
			cl.destroy();
		}
	}
	
	public synchronized Data getClient(Long id) {
		return usersMap.get(id);
	}
	
	public synchronized boolean containsClient(Long id) {
		return usersMap.containsKey(id);
	}
	public long getSize() {
		return usersMap.size(); 
	}
	
	public synchronized void destroy() {
		for(Data cl : usersMap.values()) {
			cl.destroy();
		}
		usersMap = new TreeMap<Long, Data>();
	}
	
	public synchronized ArrayList<Data> getAllClients() {
		ArrayList<Data> clients = new ArrayList<Data>();
		for(Data client : usersMap.values()) {
			clients.add(client);
		}
		return clients;
	}
	
}
