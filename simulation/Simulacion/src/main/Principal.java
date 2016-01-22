package main;

import java.util.concurrent.Semaphore;

import data.Resource;
import frontend.OptionsUI;
import socket.Dispatcher;
import utils.ConfigFile;

/**
 * Principal
 * 
 * The objective of this Simulation project is to simulate a resource. To do that, we receive information from sokects,
 * and simulate the navigation to incidents using Google Maps and JSONs.
 * 
 * @author Skynet Team
 *
 */
public class Principal {
	
	private Resource r;
	private Dispatcher d;
	
	private OptionsUI initUI;
		
	private void start() {
		initConfiUI();
		initDispatcher();
		initResource();
	}
	
	/**
	 * Starts the OptionsUI window to set the simulation variables.
	 */
	private void initConfiUI() {
		Semaphore stop =  new Semaphore(0);
		initUI = new OptionsUI(stop);
		initUI.start();
		try {
			stop.acquire();
		} catch (InterruptedException e) {}
	}
	
	/**
	 * Dispatcher starts the whole system of socket communications.
	 */
	private void initDispatcher() {
		d = new Dispatcher();
		d.start();
	}
	
	/**
	 * Creates the Resource object with the Dispatcher.
	 */
	private void initResource() {
		r = new Resource(d);
		r.start();
	}
	
	public static void main(String args[]) {
		System.out.println("PRINCIPAL: Inicio del programa.");
		// Read the file if exists.
		ConfigFile file = new ConfigFile();
		if (file.check()) file.readAll();
		// Start the program.
		Principal p = new Principal();
		p.start();
		System.out.println("PRINCIPAL: Fin del hilo principal del programa.");
	}

}
