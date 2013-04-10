package shared.message;

/**
 * 
 * @author PDimitrov
 */
public class RegisterCommand implements CommandMessage {

	private static final long serialVersionUID = -2991057521507321703L;

	private String userName;
	private String password;
	
	public RegisterCommand() {
		super();
	}
	
	public RegisterCommand(String userName, String password) {
		super();
		this.userName = userName;
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
