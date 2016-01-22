package socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import data.Definitions;

/**
 * Multicast
 * 
 * This class creates a Multicast socket to listen to a multicast group.
 * 
 * @author Skynet Team
 *
 */
public class Multicast extends Thread {
	
	Buzon<String> buzon;
	
	MulticastSocket socket;
	InetAddress group;
	
	boolean stop = false;
	
	/**
	 * Multicast constructor receives a mailbox to place the alert messages.
	 * 
	 * @param buzon Buzon buzon (mailbox)
	 */
	public Multicast(Buzon<String> buzon) {
		this.buzon = buzon;
		init();
	}
	
	/**
	 * After joining the group, starts listening to the multicast socket.
	 */
	@Override
	public void run() {
		DatagramPacket packet;
		while (!stop) {
			byte [] buf = new byte [256];
		    packet = new DatagramPacket(buf, buf.length);
		    try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		    try {
		    	String s = new String(packet.getData());
		    	System.out.println(s);
				buzon.send(s);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This method joins the Multicast group.
	 */
	private void init() {
		try {
			socket = new MulticastSocket(Definitions.multicastPort);
			group = InetAddress.getByName(Definitions.multicastGroup);
			socket.joinGroup(group);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method leaves the group.
	 */
	public void close() {
		stop = true;
		try {
			socket.leaveGroup(group);
		} catch (IOException e) {
			e.printStackTrace();
		}
		socket.close();
	}

}
