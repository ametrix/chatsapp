/**
 * 
 */
package shared.message;

/**
 * @author PDimitrov
 *
 */
public interface ClientToClientMessage extends Message {

	public Long getSenderId();
	public Long getReceiverId();
}
