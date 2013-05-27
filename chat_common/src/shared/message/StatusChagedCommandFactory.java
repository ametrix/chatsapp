/**
 * 
 */
package shared.message;

import shared.SkypeStatus;

/**
 * @author PDimitrov
 *
 */
public class StatusChagedCommandFactory {
	
	public static StatusChangedCommand makeONToOFFCommand(long userId) {
		return new StatusChangedCommand (userId, SkypeStatus.ONLINE.name(), SkypeStatus.OFFLINE.name());
	}
	
	public static StatusChangedCommand makeOFFToONCommand(long userId) {
		return new StatusChangedCommand (userId, SkypeStatus.OFFLINE.name(), SkypeStatus.ONLINE.name());
	}
}
