package connections;

public class MessageParser {
	
	public static final byte MSG_START = 2;
	public static final byte MSG_END = 3;
	public static final byte MSG_SEPARATOR = 29;
	
	Connection c;

	public MessageParser(Connection c) {
		this.c = c;
	}
	
	public Message readMessage() {
		return null;
	}
	
	public void writeMessage(Message msg) {
		
	}
	
	public Connection getConnection() {
		return c;
	}
	
}
