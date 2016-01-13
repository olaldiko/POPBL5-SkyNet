package socket;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import frontend.DebuggingUI;

/**
 * Parser es la clase que se va a ocupar de verificar los mensajes recividos y en convertirlos a un objeto Message.
 * 
 * @author Skynet Team
 *
 */
public class Parser extends Thread {
	
	Matcher matcher;
	
	private final String regex = "^(\u0002?)([0-9]+)(\u001D?)([A-Z]+)(\u001D?)([\\w\\s]+)(\u0003?)$";
	
	private Buzon<String> in;
	private Buzon<Message> out;
	
	private boolean stop = false;
	
	private DebuggingUI dUI;
	
	public Parser(Buzon<String> in, Buzon<Message> out) {
		this.in = in;
		this.out = out;
		dUI = new DebuggingUI("Parser");
	}
	
	@Override
	public void run() {
		while (!stop) {
			dUI.print("PARSER: Esperando mensaje... ");
			matcher = Pattern.compile(regex).matcher(receive());
			dUI.println("OK!");
			if (matcher.matches()) {
				dUI.print("PARSER: Parseando mensaje... ");
				int id = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)): -1;
				String type = matcher.group(4) != null ? matcher.group(4) : "";
				String data = matcher.group(6) != null ? matcher.group(6) : "";
				Message msg = new Message(id, type, data);
				dUI.print(msg.toString());
				send(msg);
				dUI.println(" OK!");
			} else dUI.println("PARSER - ERROR: El formato del mensaje no es correcto.");
		} dUI.println("PARSER: Fin del hilo.");
	}
	
	private String receive() {
		String line = null;
		try {
			line = in.receive();
		} catch (InterruptedException e) {}
		return line;
	}
	
	private void send(Message message) {
		try {
			out.send(message);
		} catch (InterruptedException e) {}
	}
	
	public void kill() {
		stop = true;
	}

}
