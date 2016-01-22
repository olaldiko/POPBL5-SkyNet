package domain;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPConnection {
	
	public String host;
	public int port;
	
	Socket socket;
	
	public TCPConnection(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public boolean isConnected() {
		if (socket != null) {
			if (socket.isConnected()) {
				if (!socket.isClosed()) return true;
			}
		}
		return false;
	}
	
	public void connect() throws UnknownHostException, IOException {
		socket = new Socket(host, port);
		socket.setSendBufferSize(1);
		socket.setReceiveBufferSize(1);
	}
	
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		socket = null;
	}

	public void write(byte [] b) throws IOException {
		socket.getOutputStream().write(b);
	}

	public int read(byte [] b) throws IOException {
		if (socket == null) {
			throw new IOException();
		} return socket.getInputStream().read(b);
	}

}
