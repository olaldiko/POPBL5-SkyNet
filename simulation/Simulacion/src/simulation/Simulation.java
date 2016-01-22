package simulation;

import java.util.ArrayList;

import data.Resource;
import frontend.MainUI;
import json.JSON;
import socket.Buzon;

/**
 * Simulation
 * 
 * This class creates a thread to simulate the navigation route in Google Maps.
 * 
 * @author Skynet Team
 *
 */
public class Simulation extends Thread {
	
	private MainUI ui;
	private Buzon<String> buzon;
	private Resource r;
	
	private Navigation nav;
	private ArrayList<Step> steps;
	
	private static final int pasos = 100;
	private static final int refreshRate = 500;
	private boolean stop = false;
	
	private boolean driving = false;
	
	/**
	 * The constructo set the needed varibles for the Simulation.
	 *  
	 * @param ui MainUI ui is needed to print the route in the UI.
	 * @param buzon Buzon buzon (mailbox) is needed to listen for new routes.
	 * @param r Resource r is needed to change the location and state of the resource.
	 */
	public Simulation(MainUI ui, Buzon<String> buzon, Resource r) {
		this.ui = ui;
		this.buzon = buzon;
		this.r = r;
	}
	
	/**
	 * The thread starts listening to the mailbox. When the thread receives a JSON, it parses it using
	 * "JSON" object, then sets the state of the resource and starts the simulation. The simulation will
	 * be stopped by the "Resource" thread if a route is received when the state is 1. If the simulation
	 * finishes the route to the incident, the incident will be resolved and the resource will be changed
	 * to the state 2. When the server receives the state 2 message, the server will send a JSON with the
	 * return home path.
	 */
	@Override
	public void run() {
		while (!stop) {
			JSON json = null;
			int result = 0;
			try {
				json = new JSON(buzon.receive());
				result = json.parseNav();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (result < 0) {
				r.setEstado(0);
			} else {
				driving = true;
				nav = json.getNav();
				steps = nav.getSteps();
				if (r.getEstado() == 0) {
					r.setEstado(1);
					ui.addNavText("Ruta (ida) recibida, leyendo resumen de ruta.");
				} else if (r.getEstado() == 2) {
					ui.addNavText("Ruta (vuelta) recibida, leyendo resumen de ruta.");
				} 
				ui.setDistanciaTotal(nav.getDistance());
				ui.setDuracionTotal(nav.getDuration());
				ui.addNavText("Leyendo instrucciones de ruta:");
				addPointer(steps.get(0).getStart_lat(), steps.get(0).getStart_lng());
				ui.addNavText("\tPASO 0");
				ui.addNavText("\t\t"+steps.get(0).getInstruction());
				for (int i = 1; i < steps.size(); i++) {
					updatePointer(steps.get(i).getStart_lat(), steps.get(i).getStart_lng());
					ui.addNavText("\tPASO "+i);
					ui.addNavText("\t\t"+steps.get(i).getInstruction());
					ui.setDistanciaRest(String.valueOf((steps.get(i).getDistance_v() * 0.001))+" km");
					ui.setDuracionRest(secondsToString(steps.get(i).getDuration_v()));
					if (stop) {
						if (r.getEstado() == 1) {
							r.setEstado(0);
							ui.clearNavText();
							ui.addNavText("Ruta cancelada, nueva ruta recibida.");
							return;
						}
					}
				}
				if (r.getEstado() != 2) {
					r.setEstado(2);
					ui.addNavText("Incidente atendido, esperando ruta de vuelta.");
				} else {
					r.setEstado(0);
					ui.addNavText("Ruta de vuelta finalizada.");
					sleep(1000);
					ui.clearNavText();
					ui.addNavText("Esperando ruta...");
				}
			}
		}
	}
	
	public void kill() {
		this.interrupt();
		stop = true;
	}
	
	/**
	 * This method add a pointer to the map and updates the location of the resource.
	 * 
	 * @param Double latitude of the marker.
	 * @param Double longitude of the marker.
	 */
	private void addPointer(double lat, double lng) {
		r.setLocation(lat, lng);
		ui.addPointer(lat, lng);
	}
	
	/**
	 * Updates the position of the marker in a progressive way to simulate the movement
	 * of a resource.
	 * 
	 * @param Double latitude of the marker.
	 * @param Double longitude of the marker.
	 */
	private void updatePointer(double lat, double lng) {
		double diffLat = lat - r.getLat();
		double diffLng = lng - r.getLng();
		double length = Math.sqrt((diffLat * diffLat) + (diffLng * diffLng)) * pasos;
		int j = (int) length;
		for (int i = 0; i < j; i++) {
			double oLat = r.getLat() + (diffLat / j);
			double oLng = r.getLng() + (diffLng / j);
			r.setLocation(oLat, oLng);
			ui.updatePointer(oLat, oLng);
			if (stop) return;
			sleep(refreshRate);
		}
	}
	
	private String secondsToString(int val) {
		int hours = val / 3600;
	    int remainder = val - hours * 3600;
	    int mins = remainder / 60;
		return hours+"h "+mins+"min";
	}
	
	private void sleep(int mili) {
		synchronized (this) {
			try {
				this.wait(mili);
			} catch (InterruptedException e) {}
		}
	}
	
	public boolean getDriving() {
		return driving;
	}

}
