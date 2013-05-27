/**
 * 
 */
package client;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Represents a message shown in the chat window
 * @author PDimitrov
 *
 */
public class UIMessage {
	
	private String autor;
	private String text;
	private Date dateTime;
	private String constructedText;
	
	public UIMessage(String text, String autor, Date date) {
		this.text = text;
		this.autor = autor;
		this.dateTime = date;
		constructedText = "<html><font fileID='bla.lq' color=\"#779966\">"+getAutor()+": "+getDateAsString()+"</font>- "+getText()+"</html>";
	}
	
	private String getDateAsString() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    	return sdf.format(getDateTime());
	}

	public String getText() {
		return text;
	}

	public String getAutor() {
		return autor;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public String toString() {
		return constructedText;
	}
}
