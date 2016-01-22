package socket;

/**
 * Message
 * 
 * This class will storage all the data of a message received throught sockets.
 * 
 * @author Skynet Team
 *
 */
public class Message {
	
	private int id;
	private String type, data;
	
	public Message(int id, String type, String data) {
		this.id = id;
		this.type = type;
		this.data = data;
	}
	
	public String getType() {
		return type;
	}

	public String getData() {
		return data;
	}

	public int getID() {
		return id;
	}

	/**
	 * This class parses the "Message" object to a format to send it throught sockets.
	 * 
	 * @return String message.
	 */
	public String toSocketFormat() {
		return (((char) 2)+String.valueOf(id)+((char) 29)+type+((char) 29)+data+((char) 3));
	}
	
	@Override
	public String toString() {
		return "<STX>"+id+"<GS>"+type+"<GS>"+data+"<ETX>";
	}
	
}
