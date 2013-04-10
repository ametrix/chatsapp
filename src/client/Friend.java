/**
 * 
 */
package client;

import shared.SkypeStatus;


/**
 * @author PDimitrov
 */
public class Friend {
	private String name;
	private Long id;
	private SkypeStatus status = SkypeStatus.OFFLINE;
	
	public Friend(String name, Long id) {
		super();
		this.name = name;
		this.id = id;
	}

	public SkypeStatus getStatus() {
		return status;
	}

	public void setStatus(SkypeStatus status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public Long getId() {
		return id;
	}
	
	@Override
	public String toString() {
		String color = "#aa2343";
		if(status.equals(SkypeStatus.ONLINE)) {
			color = "#33aa23";
		}
		
		return "<html><font color=\""+color+"\">"+name+"</font></html>";
	}
	
}
