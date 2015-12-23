package connections;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class TCPConnection implements Connection, Serializable {

	private static final long serialVersionUID = 1L;
	
	public String host;
	public int port;
	
	transient ServerSocket servSocket;
	transient Socket socket;
	
	public TCPConnection(List<Object> settings) {
		host = (String) settings.get(0);
		port = (Integer) settings.get(1);
	}
	
	public TCPConnection(Socket s) {
		host = s.getInetAddress().getHostAddress();
		port = s.getPort();
	}
	
	public void initServer() throws IOException {
		servSocket = new ServerSocket(port);
	}
	
	public Connection accept() throws IOException {
		return new TCPConnection(servSocket.accept());
	}
	
	public void connect() throws UnknownHostException, IOException {
		socket = new Socket(host,port);
	}
	
	public void close() throws IOException {
		socket.close();
	}

	public void write(byte[] b) throws IOException {
		socket.getOutputStream().write(b);
	}

	public int read(byte[] b) throws IOException {
		return socket.getInputStream().read(b);
	}

}
