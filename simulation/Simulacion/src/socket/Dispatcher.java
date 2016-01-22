package socket;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import data.Definitions;
import frontend.DebuggingUI;

/**
 * Dispatcher
 * 
 * Dispatcher is the that manage the socket communications. This class will be the one who tries to reconnect when
 * the conection is lost.
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
	
	/**
	 * Dispatcher constructor creates the mailboxes.
	 */
	public Dispatcher() {
		receiverBuzon = new Buzon<>(1000);
		senderBuzon = new Buzon<>(1000);
		parserBuzon = new Buzon<>(1000);
		dUI = new DebuggingUI("Dispatcher");
	}
	
	/**
	 * Dispatcher thread creates the needed threads to use sockets and, if fails, tries to reconnect.
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
	
	/**
	 * This method creates the "Socket" object.
	 */
	private void initConnection() {
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
	 * With this method the programs sends...
	 * 		- ID
	 * 		- IDREQUEST
	 * 
	 * @param id Integer ID.
	 * @param type String type.
	 * @param data Integer data.
	 */
	public void send(int id, String type, int data) {
		try {
			senderBuzon.send(new Message(id, type, String.valueOf(data)));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * With this method the programs sends...
	 * 		- LOCATION
	 * 		- ESTADO
	 * 
	 * @param id Integer ID.
	 * @param type String type.
	 * @param data String data.
	 */
	public void send(int id, String type, String data) {
		try {
			senderBuzon.send(new Message(id, type, data));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * With this method the programs sends...
	 * 		- IDASSING
	 * 		- ALERT
	 * 		- ROUTE
	 * 
	 * @param type String type.
	 * @return Message message.
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
