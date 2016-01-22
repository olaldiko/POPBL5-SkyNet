package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

import frontend.DebuggingUI;

/**
 * Receive
 * 
 * This class, using a created socket, receives data throught sockets.
 * 
 * @author Skynet Team
 *
 */
public class Receive extends Thread {
	
	private Buzon<String> buzon;
	
	private Socket socket;
	private BufferedReader input = null;
	
	private boolean stop = false;
	
	private DebuggingUI dUI;
	
	/**
	 * The constructor receives the "Socket" object and a mailbox to place the new messages received.
	 * 
	 * @param Socket socket
	 * @param Buzon buzon (Mailbox)
	 */
	protected Receive(Socket socket, Buzon<String> buzon) {
		this.socket = socket;
		this.buzon = buzon;
		dUI = new DebuggingUI("Receive");
	}
	
	/**
	 * This methods creates a "BufferedReader" object with the "Socket.getInputStream()". Then, the thread
	 * waits for a new message in the sockte and, when the thread receives a message, it places the message
	 * in a String mailbox.
	 */
	@Override
	public void run() {
		try {
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			System.out.println("ERROR: Ha ocurrido un problema con el socket.");
			e.printStackTrace();
		}
		while (!stop) {
			dUI.print("RECEIVE: Esperando mensajes... ");
			char [] data = null;
			try {
				ArrayList<Character> a = new ArrayList<>(); 
				int value = 0;
				while ((value = input.read()) != 3) {
					a.add((char) value);
				}
				data = new char[a.size()];
				for (int i = 0; i < a.size(); i++) {
					data[i] = a.get(i);
				}
			} catch (IOException e) {
				System.out.println("Conexion cerrada por el servidor.");
				e.printStackTrace();
			}
			if (data != null) {
				try {
					buzon.send(String.valueOf(data));
					dUI.println("Recibido!");
				} catch (InterruptedException e) {}
			}
		} dUI.println("RECEIVE: Fin del hilo.");
	}
	
	protected void kill() {
		stop = true;
		try {
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
