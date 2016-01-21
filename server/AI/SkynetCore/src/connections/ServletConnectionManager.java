package connections;

import ia.Solver;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import configuration.Configuration;
import configuration.Logger;
import database.Recurso;

public class ServletConnectionManager {
	
	public static final byte sendByte = 0;
	public static final byte receiveByte = 0;
	
	public static final long sendPeriod = 1000L;
	public static final long timeout = 30000L;

	Logger log;
	Solver sol;
	Map<Integer,Recurso> recursos;
	final Connection connectionData;
	Connection c;
	Thread send;
	Thread receive;
	boolean stop;
	Lock lock;
	
	public ServletConnectionManager() {
		lock = new ReentrantLock();
		log = Configuration.getCurrent().getLogger();
		sol = Configuration.getCurrent().getSolver();
		recursos = Configuration.getCurrent().getRecursos();
		connectionData = Configuration.getCurrent().getServletConnection();
		c = connectionData.clone();
		send = new Thread() {
			public void run() {
				sendingTask();
			}
		};
		receive = new Thread() {
			public void run() {
				receivingTask();
			}
		};
		send.start();
		receive.start();
	}
	
	public void onRequest() {
		log.log("Routes calculation requested");
		sol.scheduleSolution(recursos.values().toArray(new Recurso[0]));
	}
	
	public void halt() {
		stop = true;
		send.interrupt();
		receive.interrupt();
	}
	
	public void sendingTask() {
		byte[] b = {sendByte};
		log.log("Servlet sending thread started, waiting for connections", Logger.DEBUG);
		try {
			c.initServer();
			Connection tmp = c.accept();
			lock.lock();
			c = tmp;
			lock.unlock();
			receive.interrupt();
			log.log("Servlet connected");
		} catch (IOException e){
			log.log("Error binding servlet connection", Logger.ERROR);
		}
		try {
			while(!stop) {
				Thread.sleep(sendPeriod);
				try {
					c.write(b);
				} catch(IOException e) {
					c.close();
					c = connectionData.clone();
					log.log("Servlet sending thread communications disconnected", Logger.DEBUG);
					while(!c.isConnected() && !stop) {
						try {
							c.initServer();
							Connection tmp = c.accept();
							lock.lock();
							c = tmp;
							lock.unlock();
							receive.interrupt();
						} catch (IOException e1){}
					}
					log.log("Servlet sending thread communications connected", Logger.DEBUG);
				}
			}
		} catch(InterruptedException e) {}
		log.log("Servlet sending thread terminated");
	}
	
	public void receivingTask() {
		byte[] b = new byte[1];
		b[0] = receiveByte+1;
		log.log("Servlet receiving thread started", Logger.DEBUG);
		try {
			while(!stop) {
				try {
					if(c.read(b)>0 && b[0]==receiveByte) {
						log.log("Servlet request received", Logger.DEBUG);
						onRequest();
					}
				} catch(IOException e) {
					c.close();
					log.log("Servlet receiving thread communications disconnect", Logger.DEBUG);
					lock.lock();
					while(!c.isConnected() && !stop) {
						try {
							lock.unlock();
							Thread.sleep(timeout);
							log.log("Servlet request simulated because of timeout", Logger.DEBUG);
							onRequest();
							lock.lock();
						} catch(InterruptedException ie) {}
					}
					try {
						lock.unlock();
					} catch(IllegalMonitorStateException mse) {}
					log.log("Servlet receiving thread communications reconnected", Logger.DEBUG);
				}
			}
		} catch(Exception e) {e.printStackTrace();}
		log.log("Servlet receiving thread terminated");
	}
	
	public Connection getConnection() {
		return c;
	}
	
}
