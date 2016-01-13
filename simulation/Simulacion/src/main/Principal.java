package main;

import java.util.concurrent.Semaphore;

import data.Resource;
import frontend.OptionsUI;
import socket.Dispatcher;
import utils.ConfigFile;

/**
 * El objetivo de este proyecto es simular una ambulancia. Para ello, tenemos que tener las siguientes funcionalidades:
 * - Recibir por sockets informacion.
 * 		* Primero, tenemos que pedir un ID con IDREQUEST
 * - Procesar JSONs
 * - Printar el desplazamiento de una ambulancia punto por punto.
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
		r.sendToBuzon("https://maps.googleapis.com/maps/api/directions/json?origin=Galdakao&destination=43.244861,-2.0");
		synchronized (this) {
			try {
				this.wait(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("ENVIO");
		r.sendToBuzon("https://maps.googleapis.com/maps/api/directions/json?origin=Galdakao&destination=43.244861,-2.0");
	}
	
	/**
	 * Iniciamos el UI de opciones para dar la opcion a editar la conexion al servidor.
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
	 * Iniciamos todo el sistema de comunicaciones.
	 */
	private void initDispatcher() {
		d = new Dispatcher();
		d.start();
	}
	
	/**
	 * Iniciamos el recurso, este iniciara el MainUI y los sistemas de simulacion.
	 */
	private void initResource() {
		r = new Resource(d);
		r.start();
	}
	
	public static void main(String args[]) {
		System.out.println("PRINCIPAL: Inicio del programa.");
		// Leemos el archivo si existe.
		ConfigFile file = new ConfigFile();
		if (file.check()) file.readAll();
		// Iniciamos el programa.
		Principal p = new Principal();
		p.start();
		System.out.println("PRINCIPAL: Fin del programa.");
	}

}
