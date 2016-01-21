package controller;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JOptionPane;

import ui.MainWindow;
import configuration.Configuration;
import configuration.Logger;
import connections.ServletConnectionManager;
import connections.StationConnectionManager;

public class SkynetMain implements WindowListener {
	
	MainWindow window;
	Logger log;
	ServletConnectionManager servletConn;
	StationConnectionManager stationConn;

	public static void main(String[] args) {
		SkynetMain p = new SkynetMain();
		p.start();
	}

	public void start() {
		
		//Configuration
		
		boolean defaultLoaded = false;
		
		try {
			Configuration.loadFromFile();
		} catch(Exception e) {
			try {
				Configuration.loadDefault();
				defaultLoaded = true;
			} catch(Exception e2) {
				e2.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error loading configuration: "+e2.getMessage(), "FATAL", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
		}
		
		//Window
		
		try {
			window = new MainWindow(this);
			window.setVisible(true);
		} catch(Exception e) {
			//GraphicsEnviroment isHeadless
		}
		
		try {
			log = Configuration.getCurrent().getLogger();
			log.init(window);
		} catch(Exception e) {
			e.printStackTrace();
		}
		log.log(defaultLoaded ? "Default configuration loaded":"Configuration loaded from file");
		
		//Solver
		Configuration.getCurrent().getSolver().init();
		
		//Tasks
		servletConn = new ServletConnectionManager();
		stationConn = new StationConnectionManager();
		
	}
	
	/////////////////WINDOW LISTENER/////////////////
	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowClosing(WindowEvent e) {
		try {
			if(window!=null) window.saveSettings();
			Configuration.saveToFile();
			log.log("Configuration saved to file");
		} catch (Exception ex) {
			log.log("Failed to save configuration: "+ex.getLocalizedMessage(),Logger.ERROR);
		}
		
		log.log("Window closing, application terminated");
		System.exit(0);
	}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	/////////////////WINDOW LISTENER/////////////////
	
}
