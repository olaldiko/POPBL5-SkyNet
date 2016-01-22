package connections;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import configuration.Configuration;
import configuration.Logger;

public class MulticastConnection implements Connection {
	
	public static final String FIRST_GROUP = "239.128.0.1";
	public static final String USE_INTERFACE = "192.168.0.14";
	
	public static final byte MSG_START = 2;
	public static final byte MSG_END = 3;
	public static final byte MSG_SEPARATOR = 29;
	
	MulticastSocket s;
	InetAddress group;
	Logger log;
	
	public String ip;
	public int port;
	public String useInterface;
	
	public MulticastConnection(List<String> settings) throws IOException {
		this(settings.get(0),Integer.valueOf(settings.get(1)),settings.get(2));
	}
	
	public MulticastConnection(int port) throws IOException {
		this(FIRST_GROUP,port,USE_INTERFACE);
	}
	
	public MulticastConnection(String ip, int port, String useInterface) throws IOException {
		this.ip = ip;
		this.useInterface = useInterface;
		this.group = InetAddress.getByName(ip);
		this.port = port;
		s = new MulticastSocket(port);
		s.setInterface(InetAddress.getByName(useInterface));
	}
	
	public void sendAlert(String msg) throws IOException {
		ArrayList<Byte> b = new ArrayList<Byte>();
		b.add(MSG_START);
		b.add((byte)'0');
		b.add(MSG_SEPARATOR);
		addBytes(b,"ALERT".getBytes());
		b.add(MSG_SEPARATOR);
		addBytes(b,msg.getBytes());
		b.add(MSG_END);
		write(toPrimitives(b.toArray(new Byte[0])));
		Configuration.getCurrent().getLogger().log("Alert sent: "+msg);
	}
	
	public void addBytes(ArrayList<Byte> list, byte[] b) {
		for(int i = 0 ; i < b.length ; i++) {
			list.add(b[i]);
		}
	}
	
	public byte[] toPrimitives(Byte[] oBytes) {
	    byte[] bytes = new byte[oBytes.length];
	    for(int i = 0; i < oBytes.length; i++) {
	        bytes[i] = oBytes[i];
	    }
	    return bytes;
	}

	public boolean isConnected() {
		return (s!=null);
	}

	public void initServer() throws IOException {}

	public Connection accept() throws IOException {
		return null;
	}

	public void connect() throws UnknownHostException, IOException {}

	public void close() {}

	public void write(byte[] b) throws IOException {
		s.send(new DatagramPacket(b, b.length,
                group, port));
	}

	public int read(byte[] b) throws IOException {
		return 1;
	}

	public Connection clone() {
		return null;
	}

}
