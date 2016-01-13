package socket;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import frontend.DebuggingUI;

/**
 * Clase que envia por sockets informacion al puesto.
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
	
	protected Send(Socket socket, Buzon<Message> buzon) {
		this.socket = socket;
		this.buzon = buzon;
		dUI = new DebuggingUI("Send");
	}
	
	/**
	 * Creamos el PrintWriter con el outputStream del Socket. Despues, esperamos los datos del buzon para enviarserlo al PrintWriter.
	 * Finalmente hacemos flush y lo enviamos.
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
