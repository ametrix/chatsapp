/**
 * 
 */
package shared.message;

/**
 * @author PDimitrov
 *
 */
public class KeepAliveMessage implements CommandMessage {

	private static final long serialVersionUID = -3136515591737811647L;
	
	public static final KeepAliveMessage INSTANCE = new KeepAliveMessage();
	
	public final String message = "keepAlive!";
	
	private KeepAliveMessage() {}
}
