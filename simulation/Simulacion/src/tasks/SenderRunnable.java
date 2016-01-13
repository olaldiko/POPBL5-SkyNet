package tasks;

import data.Resource;
import socket.Dispatcher;

/**
 * SenderRunnable es un Runnable que envía un mensaje concreto con los datos de Resource.
 * Tipo de datos:
 * 		- ID.
 * 		- LOC.
 * 		- ESTADO.
 * 
 * @author Skynet Team
 *
 */
public class SenderRunnable extends Thread {
	
	private Resource r;
	private Dispatcher d;

	private String type;
	
	public SenderRunnable(Resource r, Dispatcher d, String type) {
		this.r = r;
		this.d = d;
		this.type = type;
	}
	
	@Override
	public void run() {
		String data = null;
		switch (type) {
			case "LOCATION": data = r.getLocation();
				break;
			default: System.out.println("SENDER RUNNABLE - ERROR: Tipo de mensaje no valido; tipo: "+type+".");
		}
		d.send(r.getID(), type, data);
	}

}
