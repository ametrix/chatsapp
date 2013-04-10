package shared;

import java.io.Serializable;

/**
 * The status of a friend in the friends list.
 * @author PDimitrov
 */
public enum SkypeStatus implements Serializable {
	
	/**
	 * Means that the friend is online and it can receive messages.
	 */
	ONLINE
	
	/**
	 * Means that the friend is offline and it cannot receive messages.
	 */
	, OFFLINE
	
	/**
	 * Means that the friend is not confirmed the friendship yet.
	 */
	, WAIT_FOR_FRENDSHIP_CONFIRMATION ; 
	
	public static SkypeStatus converFromName(String name) {
		for(SkypeStatus status : values()) {
			if(status.name().equalsIgnoreCase(name)) {
				return status;
			}
		}
		return null;
	}
}
