package connections;

import java.io.IOException;
import java.net.UnknownHostException;

public interface Connection {

	public boolean isConnected();
	public void initServer() throws IOException;
	public Connection accept() throws IOException;
	public void connect() throws UnknownHostException, IOException;
	public void close();
	public void write(byte[] b) throws IOException;
	public int read(byte[] b) throws IOException;
	public Connection clone();
	
}
