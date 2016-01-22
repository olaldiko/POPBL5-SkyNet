package socket;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import frontend.DebuggingUI;

/**
 * Parser 
 * 
 * Parser class takes a received message with a concrete format and, parsing the Message, creates a "Message" object.
 * 
 * @author Skynet Team
 *
 */
public class Parser extends Thread {
	
	Matcher matcher;
	
	//private final String regex = "^\u0002?([0-9]+)\u001D?([\\w]+)\u001D?([\\w\\s\\.]+).*$";
	private final String regex = "^\u0002?([0-9]+)\u001D?(.*)$";
	
	private Buzon<String> in;
	private Buzon<Message> out;
	
	private boolean stop = false;
	
	private DebuggingUI dUI;
	
	/**
	 * The constructor sets the in and out mailbox.
	 * 
	 * @param in Buzon in mailbox.
	 * @param out Buzon out mailbox.
	 */
	public Parser(Buzon<String> in, Buzon<Message> out) {
		this.in = in;
		this.out = out;
		dUI = new DebuggingUI("Parser");
	}
	
	/**
	 * Parser compares the received message, checks the format and, if its correct,
	 * divides the message and creates a "Message" objects. Finally, it places the
	 * "Message" object in the output mailbox.
	 */
	@Override
	public void run() {
		while (!stop) {
			dUI.print("PARSER: Esperando mensaje... ");
			String s = receive();
			System.out.println(s);
			matcher = Pattern.compile(regex).matcher(s);
			dUI.println("OK!");
			if (matcher.matches()) {
				dUI.print("PARSER: Parseando mensaje... ");
				int id = matcher.group(1) != null ? Integer.parseInt(matcher.group(1)): -1;
				String data = matcher.group(2) != null ? matcher.group(2) : "";
				String [] d = data.split("\u001D");
				Message msg = new Message(id, d[0], d[1]);
				dUI.print(msg.toString());
				send(msg);
				dUI.println(" OK!");
			} else dUI.println("PARSER - ERROR: El formato del mensaje no es correcto: "+s);
		} dUI.println("PARSER: Fin del hilo.");
	}
	
	/**
	 * Receives the string message throught the input mailbox.
	 * 
	 * @return line String message text.
	 */
	private String receive() {
		String line = null;
		try {
			line = in.receive();
		} catch (InterruptedException e) {}
		return line;
	}
	
	/**
	 * Sends the message to the output mailbox.
	 * 
	 * @param message Message receiverd new message.
	 */
	private void send(Message message) {
		try {
			out.send(message);
		} catch (InterruptedException e) {}
	}
	
	public void kill() {
		stop = true;
	}

}
