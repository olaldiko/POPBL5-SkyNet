package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import frontend.DebuggingUI;

/**
 * Clase que crea un servidor de escucha en un Socket, dado el numero de socket.
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
	
	protected Receive(Socket socket, Buzon<String> buzon) {
		this.socket = socket;
		this.buzon = buzon;
		dUI = new DebuggingUI("Receive");
	}
	
	/**
	 * Creamos el BufferedReader utilizando el inputStream del Socket. Nada mas recivir informacion del socket, lo enviamos al buzon del Parser.
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
			String line = null;
			try {
				line = input.readLine();
			} catch (IOException e) {
				System.out.println("Conexion cerrada por el servidor.");
				e.printStackTrace();
			}
			if (line != null) {
				try {
					buzon.send(line);
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
