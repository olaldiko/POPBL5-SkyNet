package connections;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import configuration.Configuration;
import configuration.Logger;

public class TCPConnection implements Connection, Serializable {

	private static final long serialVersionUID = 1L;
	
	public String host;
	public int port;
	
	transient ServerSocket servSocket;
	transient Socket socket;
	transient Logger log;
	
	public TCPConnection(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public TCPConnection(List<String> settings) {
		host = settings.get(0);
		port = Integer.valueOf(settings.get(1));
	}
	
	public TCPConnection(Socket s) {
		host = s.getInetAddress().getHostAddress();
		port = s.getPort();
		socket = s;
	}
	
	public boolean isConnected() {
		if(socket!=null) {
			if(socket.isConnected()) {
				if(!socket.isClosed()) return true;
			}
		}
		return false;
	}
	
	public void initServer() throws IOException {
		log = Configuration.getCurrent().getLogger();
		servSocket = new ServerSocket(port);
	}
	
	public Connection accept() throws IOException {
		log.log("Listening port "+port, Logger.DEBUG);
		return new TCPConnection(servSocket.accept());
	}
	
	public void connect() throws UnknownHostException, IOException {
		log = Configuration.getCurrent().getLogger();
		log.log("Connected to "+host+":"+port, Logger.DEBUG);
		socket = new Socket(host,port);
	}
	
	public void close() {
		try {
			socket.close();
		}catch(Exception e){}
		socket = null;
		
	}

	public void write(byte[] b) throws IOException {
		if(socket==null) throw new IOException();
		socket.getOutputStream().write(b);
	}

	public int read(byte[] b) throws IOException {
		if(socket==null) throw new IOException();
		return socket.getInputStream().read(b);
	}
	
	public Connection clone() {
		return new TCPConnection(host,port);
	}

}
