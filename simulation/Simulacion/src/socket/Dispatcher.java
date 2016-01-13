package socket;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import data.Definitions;
import frontend.DebuggingUI;

/**
 * Dispatcher sera la clase que controle todo lo que concierne a Sockets. La clase dividira y notificara al hilo principal
 * de todos los mensajes entrantes.
 * 
 * @author Skynet Team
 *
 */
public class Dispatcher extends Thread {

	private Socket socket;
	
	private Receive receiver;
	private Send sender;
	private Parser parser;
	
	private Buzon<Message> receiverBuzon, senderBuzon;
	private Buzon<String> parserBuzon;
	
	private boolean stop = false;
	
	private DebuggingUI dUI;
	
	public Dispatcher() {
		receiverBuzon = new Buzon<>(1000);
		senderBuzon = new Buzon<>(1000);
		parserBuzon = new Buzon<>(1000);
		dUI = new DebuggingUI("Dispatcher");
	}
	
	/**
	 * Dispatcher se encarga de iniciar y reconectar con el servidor en caso de fallo.
	 */
	@Override
	public void run() {
		while (!stop) {
			dUI.print("DISPATCHER: Iniciando socket de comunicación... ");
			initConnection();
			dUI.println("OK!");
			dUI.print("DISPATCHER: Creando hilos de comunicación... ");
			createThreads();
			dUI.println("OK!");
			dUI.print("DISPATCHER: Iniciando hilos de comunicación... ");
			startThreads();
			dUI.println("OK!");
			joinThreads();
		} dUI.println("DISPATCHER: Fin del hilo.");
	}
	
	private void initConnection() {
		if ((Definitions.socketAddres.equals("127.0.0.1")) || (Definitions.socketAddres.equals("localhost"))) {
			try {
				Runtime.getRuntime().exec("nc -l "+Definitions.socketNumber);
			} catch (IOException e) {}
		}
		try {
			socket = new Socket(Definitions.socketAddres, Definitions.socketNumber);
		} catch (UnknownHostException e) {
			System.out.println("ERROR: Host desconocido.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("ERROR: Ha ocurrido un problema intentando crear el socket.");
			e.printStackTrace();
		}
	}
	
	private void createThreads() {
		receiver = new Receive(socket, parserBuzon);
		sender = new Send(socket, senderBuzon);
		parser = new Parser(parserBuzon, receiverBuzon);
	}
	
	private void startThreads() {
		receiver.start();
		sender.start();
		parser.start();
	}

	private void joinThreads() {
		try {
			receiver.join();
			sender.join();
			parser.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void kill() {
		stop = true;
		receiver.kill();
		sender.kill();
		parser.kill();
		try {
			socket.close();
		} catch (IOException e) {
			System.out.println("ERROR: Ha ocurrido un problema intentando cerrar el socket.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Con esta clase deberiamos enviar...
	 * 		- ID
	 * 		- IDREQUEST
	 * 
	 * @param type
	 * @param data
	 */
	public void send(int id, String type, int data) {
		try {
			senderBuzon.send(new Message(id, type, String.valueOf(data)));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Con esta clase deberiamos enviar...
	 * 		- LOCATION
	 * 		- ESTADO
	 * 
	 * @param type
	 * @param data
	 */
	public void send(int id, String type, String data) {
		try {
			senderBuzon.send(new Message(id, type, data));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Con esta clase deberiamos recivir...
	 * 		- IDASSING
	 * 		- ALERT
	 * 		- ROUTE
	 * 
	 * @param type
	 * @return
	 */
	public Message receive(String type) {
		try {
			return receiverBuzon.receive(type);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} return null;
	}
	
	public Message receive() {
		try {
			return receiverBuzon.receive();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} return null;
	}

	public Buzon<Message> getReceiverBuzon() {
		return receiverBuzon;
	}

	public Buzon<Message> getSenderBuzon() {
		return senderBuzon;
	}

	public Buzon<String> getParserBuzon() {
		return parserBuzon;
	}
	
}
