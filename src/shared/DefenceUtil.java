/**
 * 
 */
package shared;

/**
 * @author PDimitrov
 *
 */
public class DefenceUtil {

	public static void enshureArgsNotNull(String errorMessage, Object ... args) {
		for(Object arg : args) {
			if(arg == null) {
				throw new IllegalArgumentException(errorMessage);
			}
		}
	}
}
