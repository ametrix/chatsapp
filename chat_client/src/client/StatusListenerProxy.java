/**
 * 
 */
package client;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import shared.SkypeStatus;

import client.Connector.FriendsStatusListener;


/**
 * @author PDimitrov
 *
 */
public class StatusListenerProxy implements FriendsStatusListener {

	private FriendsStatusListener proxied = null;
	private Map<Long, SkypeStatus> notServised = new HashMap<Long,SkypeStatus>();
	
	public synchronized void setProxied(FriendsStatusListener proxied) {
		this.proxied = proxied;
	}

	@Override
	public synchronized void statusChange(Long userId, SkypeStatus newStatus) {
		System.out.println("StatusListenerProxy:"+userId+" newStatus:"+newStatus);
		if(proxied != null) {
			serviceMap();
			proxied.statusChange(userId, newStatus);
		} else {
			notServised.put(userId, newStatus);
		}		
	}
	
	private void serviceMap() {
		for(Entry<Long, SkypeStatus> entry : notServised.entrySet()) {
			proxied.statusChange(entry.getKey(), entry.getValue());
		}
		notServised.clear();
	}

}
