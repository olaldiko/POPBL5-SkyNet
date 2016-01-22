package data;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import frontend.MainUI;
import simulation.Simulation;
import socket.Buzon;
import socket.Dispatcher;
import socket.Message;
import socket.Multicast;
import tasks.SenderRunnable;
import utils.ConfigFile;

/**
 * Resource
 * 
 * Resource is the class that manage all the data of the simulation.
 * NOTE: Setters in this class rewrite config file using "rewrite()" method.
 * 
 * @author Skynet Team
 *
 */
public class Resource extends Thread {

	private MainUI ui;
	private Dispatcher d;
	
	private Buzon<String> simuBuzon;
	
	private Simulation simu;
	
	private ScheduledExecutorService scheduler;
	
	private Multicast m;
	
	private boolean stop = false;
	
	public Resource(Dispatcher d) {
		this.d = d;
		init();
		ui = new MainUI();
		ui.start();
		simuBuzon = new Buzon<>(1000);
		prepareSimulator();
		initSimulator();
	}
	
	/**
	 * When the resource thread is running, set's UI variables to be shown to the user.
	 * Then creates the task running at the background periodically (LOCATION is send each 10 seconds)
	 * Finnally, start's listening to the mailbox that is storaged the messages.
	 */
	@Override
	public void run() {
		ui.setURL(Definitions.socketAddres);
		ui.setSocket(Definitions.socketNumber);
		ui.setLocation(Definitions.lat, Definitions.lng);
		ui.setEstado(Definitions.estado);
		ui.setID(Definitions.id);
		createTasks();
		while (!stop) {
			Message msg = d.receive();
			switch (msg.getType()) {
				case "ALERT": ui.addAlertText(msg.getData());
					break;
				case "ROUTE": if (simu != null) {
						if (simu.getDriving()) {
							ui.reload();
							killSimulator();
							prepareSimulator();
							initSimulator();
						}
					}
					try {
						simuBuzon.send(msg.getData());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				case "IDASSIGN": setID(msg.getID());
					break;
				case "JOIN": Definitions.multicastGroup = msg.getData();
					if (m != null) {
						if (m.isAlive()) {
							m.close();
						}
					}
					m = new Multicast(d.getParserBuzon());
					m.start();
					break;
				default: break;
			}
		}
	}
	
	/**
	 * This method initialize the resource asking for an ID to the server. If the ID is storaged in the configuration file,
	 * then restores it and set's the resource of the state to 0 if it was waiting or moving to an incident or; if it was
	 * going back to the base, sends a state 2 to the server to receive the return home route.
	 */
	private void init() {
		if (Definitions.id == -1) {
			d.send(Definitions.id, "IDREQUEST", " ");
			setID(d.receive("IDASSIGN").getID());
		} d.send(Definitions.id, "CONNECTED", " ");
		if (Definitions.estado == 2) {
			setEstado(2);
		} else if (Definitions.estado == 1) {
			setEstado(0);
		}
	}
	
	public int getID() {
		return Definitions.id;
	}
	
	private void setID(int id) {
		if (ui != null) ui.setID(id);
		Definitions.id = id;
		rewrite();
	}
	
	public int getEstado() {
		return Definitions.estado;
	}

	public void setEstado(int estado) {
		if (ui != null) ui.setEstado(estado);
		Definitions.estado = estado;
		d.send(Definitions.id, "ESTADO", estado);
		rewrite();
	}
	
	public double getLat() {
		return Definitions.lat;
	}
	
	public double getLng() {
		return Definitions.lng;
	}
	
	public String getLocation() {
		return getLat()+","+getLng();
	}
	
	public void setLocation(double lat, double lng) {
		Definitions.lat = lat;
		Definitions.lng = lng;
		ui.setLocation(lat, lng);
		rewrite();
	}

	/**
	 * This methods creates a periodical runnable method to send specific data to the server periodically.
	 */
	private void createTasks() {
		scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(new SenderRunnable(this, d, "LOCATION"), 0, 10, TimeUnit.SECONDS);
	}
	
	private void prepareSimulator() {
		simu = new Simulation(ui, simuBuzon, this);
	}
	
	private void initSimulator() {
		simu.start();
	}
	
	private void killSimulator() {
		simu.kill();
	}
	
	/**
	 * Rewrite calls "writeAll()" method from "ConfigFile" to write the data with the variables in "Definitions" class.
	 */
	private void rewrite() {
		ConfigFile file = new ConfigFile();
		file.writeAll();
	}
	
}
