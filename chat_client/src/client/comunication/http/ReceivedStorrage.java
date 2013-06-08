/**
 * 
 */
package client.comunication.http;

import java.io.Serializable;

/**
 * @author PDimitrov
 *
 */
interface ReceivedStorrage {
	public void store(Serializable received);
}
