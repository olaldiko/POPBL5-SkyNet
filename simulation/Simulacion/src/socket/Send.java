package socket;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import frontend.DebuggingUI;

/**
 * Send
 * 
 * Send is the class that creates a thread to communicate throught sokect.
 * This class sends the data to the base.
 * 
 * @author Skynet Team
 *
 */
public class Send extends Thread {
	
	private Buzon<Message> buzon;
	
	private Socket socket;
	private PrintWriter output = null;
	
	private boolean stop = false;
	private DebuggingUI dUI;
	
	/**
	 * The constructor receives the "Socket" object and a mailbox to listen for new messages to send.
	 * 
	 * @param Socket socket
	 * @param Buzon buzon (Mailbox)
	 */
	protected Send(Socket socket, Buzon<Message> buzon) {
		this.socket = socket;
		this.buzon = buzon;
		dUI = new DebuggingUI("Send");
	}
	
	/**
	 * This methods creates a "PrintWriter" object with the "Socket.getOutputStream()". Then, the thread
	 * waits for a new message in the mailbox and, when the thread takes it out the mailbox, sends it
	 * using the PrintWriter and finnally flushs it to the base.
	 */
	@Override
	public void run() {
		try {
			output = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			System.out.println("ERROR: Ha ocurrido un problema con el socket.");
			e.printStackTrace();
		}
		while (!stop) {
			dUI.print("SEND: Esperando datos... ");
			try {
				Message msg = buzon.receive();
				dUI.print(msg.toString());
				output.print(msg.toSocketFormat());
			} catch (InterruptedException e) {}
			output.flush();
			dUI.println(" Enviado!");
		} dUI.println("SEND: Fin del hilo");
	}
	
	protected void kill() {
		stop = true;
		output.close();
	}
	
}
