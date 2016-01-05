package connections;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import configuration.Configuration;
import configuration.Logger;
import database.Recurso;
import database.RecursoFacade;

public class StationConnectionManager {

	Logger log;
	Connection c;
	Thread server;
	Thread receive;
	Thread closed;
	ArrayBlockingQueue<Message> msgIn;
	Vector<MessageParser> connections;
	Map<Integer,Recurso> recursos;
	Lock lockConnections;
	Lock lockRecursos;
	
	public StationConnectionManager() {
		log = Configuration.getCurrent().getLogger();
		c = Configuration.getCurrent().getStationConnection();
		recursos = Collections.synchronizedMap(new HashMap<Integer,Recurso>(64));
		connections = new Vector<MessageParser>();
		msgIn = new ArrayBlockingQueue<Message>(MessageParser.MSG_BUFFER_SIZE);
		lockConnections = new ReentrantLock();
		lockRecursos = new ReentrantLock();
		server = new Thread() {
			public void run() {
				serverTask();
			}
		};
		receive = new Thread() {
			public void run() {
				receivingTask();
			}
		};
		closed = new Thread() {
			public void run() {
				closedConnectionsTask();
			}
		};
		server.start();
		receive.start();
		closed.start();
	}
	
	public void manageMsg(Message msg) {
		Recurso rec;
		RecursoFacade rf;
		log.log("Station message: "+msg.toString(),Logger.DEBUG);
		try {
			switch(msg.tipo) {
			case "ROUTEACK":
				//Ni caso
				break;
			case "ROUTENACK":
				//Ni caso
				break;
			case "ESTADO":
				rec = recursos.get(msg.id);
				if(rec!=null && rec.actualizarEstado(Integer.valueOf(msg.msg))>0) msg.origin.writeMessage(new Message(msg.id,"ESTADOACK",String.valueOf(msg.cont)));
				else msg.origin.writeMessage(new Message(msg.id,"ESTADONACK",String.valueOf(msg.cont)));
				break;
			case "LOCATION":
				rec = recursos.get(msg.id);
				if(rec!=null) rec.actualizarposicion(msg.msg);
				break;
			case "LISTAREQUEST":
				//Ni caso
				break;
			case "IDREQUEST":
				rf = new RecursoFacade();
				rec = rf.nuevoRecurso(Integer.valueOf(msg.msg));
				if(rec!=null) msg.origin.writeMessage(new Message(rec.id,"IDASSIGN",String.valueOf(msg.cont)));
				break;
			case "CONNECTED":
				rf = new RecursoFacade();
				rec = rf.getRecurso(msg.id);
				if(rec!=null) {
					//Id es valido
					rec.setConnection(msg.origin);
					recursos.put(rec.id, rec);
					log.log("Recurso "+rec.id+" conectado");
				}
				else {
					//Id desconocido -> Asignar id valido
					rec = rf.nuevoRecurso(Integer.valueOf(msg.msg));
					if(rec!=null) msg.origin.writeMessage(new Message(rec.id,"IDASSIGN",String.valueOf(msg.cont)));
				}
				break;
			case "DISCONNECTED":
				rec = recursos.remove(msg.id);
				if(rec!=null) log.log("Recurso "+msg.id+" desconectado");
				break;
			}
		} catch(Exception e) {
			log.log("Error managing station message: "+e.getMessage(),Logger.ERROR);
		}
	}
	
	public void serverTask() {
		log.log("serverTask started", Logger.DEBUG);
		MessageParser mp;
		try {
			c.initServer();
			while(true) {
				mp = new MessageParser(msgIn,c.accept());
				lockConnections.lock();
				connections.add(mp);
				lockConnections.unlock();
				log.log("New station connected");
			}
		} catch (IOException e) {
			try {
				lockConnections.unlock();
			} catch(Exception e2) {}
			log.log("serverTask exception: "+e.getLocalizedMessage(), Logger.FATAL);
		}
	}
	
	public void receivingTask() {
		log.log("receivingTask started", Logger.DEBUG);
		while(true) {
			try {
				lockRecursos.lock();
				manageMsg(msgIn.take());
				lockRecursos.unlock();
			} catch(Exception e) {
				log.log("receivingTask exception: "+e.getLocalizedMessage(), Logger.DEBUG);
			}
		}
	}
	
	public void closedConnectionsTask() {
		MessageParser element;
		log.log("closedConnectionsTask started", Logger.DEBUG);
		while(true) {
			lockConnections.lock();
			try {
				for(int i = 0 ; i < connections.size() ; i++) {
					element = connections.get(i);
					if(!element.getConnection().isConnected()) {
						connections.remove(element);
						log.log("Closed station connection removed", Logger.DEBUG);
						Set<Integer> s = recursos.keySet(); 
						synchronized(recursos) {
							Iterator<Integer> it = s.iterator();
							lockRecursos.lock();
							while(it.hasNext()) {
								Integer id = it.next();
								if(element == recursos.get(id).getConnection()) {
									recursos.remove(id);
								}
							}
							lockRecursos.unlock();
						}
					}
				}
				Thread.sleep(10000);
			} catch(Exception e) {
				log.log("closedConnectionsTask exception: "+e.getLocalizedMessage(), Logger.DEBUG);
			}
			lockConnections.unlock();
		}
	}
	
}
