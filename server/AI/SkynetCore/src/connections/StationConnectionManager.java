package connections;

public class StationConnectionManager {

	MessageParser c;
	
	public StationConnectionManager(Connection c) {
		this.c = new MessageParser(c);
	}
	
	public Connection getConnection() {
		return c.getConnection();
	}
	
}
