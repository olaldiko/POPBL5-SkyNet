package data;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import frontend.MainUI;
import simulation.Simulation;
import socket.Buzon;
import socket.Dispatcher;
import socket.Message;
import tasks.SenderRunnable;
import utils.ConfigFile;

/**
 * Recurso sera la clase donde guardaremos los datos del recurso que la simulacion mueve.
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
				case "ROUTE": try {
						if (simu.getDriving()) {
							killSimulator();
							prepareSimulator();
							initSimulator();
						}
						simuBuzon.send(msg.getData());
					} catch (InterruptedException e) {
						e.printStackTrace();
					} break;
				case "IDASSING": setID(msg.getID());
					break;
				default: break;
			}
		}
	}
	
	private void init() {
		if (Definitions.id == -1) {
			d.send(Definitions.id, "IDREQUEST", "");
			//setID(d.receive("IDASSING").getID());
			setID(1005);
		} d.send(Definitions.id, "CONNECTED", "");
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
	
	public void sendToBuzon(String s) {
		if (simu.getDriving()) {
			ui.reload();
			killSimulator();
			prepareSimulator();
			initSimulator();
		}
		try {
			simuBuzon.send(s);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void rewrite() {
		ConfigFile file = new ConfigFile();
		file.writeAll();
	}
	
}
