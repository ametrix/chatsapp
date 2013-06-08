package shared.message;

import java.util.HashMap;
import java.util.Map;



/**
 * @author PDimitrov
 */
public class LoginCommand implements CommandMessage {
	
	private static final long serialVersionUID = -5146451509866429845L;
	
	private String userName;
	private String password;
	private Map<Long, String> loginResult = new HashMap<Long,String>();
	
	public LoginCommand() {
		super();
	}
	
	public LoginCommand(String userName, String password) {
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

	public Map<Long, String> getLoginResult() {
		return loginResult;
	}

	@Override
	public Long getSenderId() {
		return null;
	}

	
}
