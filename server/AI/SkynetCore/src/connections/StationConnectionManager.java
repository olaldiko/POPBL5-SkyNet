package connections;

import ia.Route;
import ia.Solver;

import java.io.IOException;
import java.util.ArrayList;
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
	Solver sol;
	Connection c;
	Thread server;
	Thread receive;
	Thread closed;
	ArrayBlockingQueue<Message> msgIn;
	Vector<MessageParser> connections;
	Map<Integer,Recurso> recursos;
	Lock lockConnections;
	
	public StationConnectionManager(Map<Integer,Recurso> recursos) {
		log = Configuration.getCurrent().getLogger();
		sol = Configuration.getCurrent().getSolver();
		c = Configuration.getCurrent().getStationConnection();
		this.recursos = recursos;
		connections = new Vector<MessageParser>();
		msgIn = new ArrayBlockingQueue<Message>(MessageParser.MSG_BUFFER_SIZE);
		lockConnections = new ReentrantLock();
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
				if(rec!=null && rec.actualizarEstado(Integer.valueOf(msg.msg))>0) {
					msg.origin.writeMessage(new Message(msg.id,"ESTADOACK",String.valueOf(msg.cont)));
					if(Integer.valueOf(msg.msg)==Recurso.ESTADO_DE_VUETA) {
						rf = new RecursoFacade();
						msg.origin.writeMessage(new Message(msg.id,"ROUTE",new Route(rec,rf.getEstacionMasCercana(rec)).toString()));
					}
				}
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
					//Despues el recurso tiene que volver a mandar CONNECTED con el ID correcto
				}
				break;
			case "DISCONNECTED":
				rec = recursos.remove(msg.id);
				if(rec!=null) log.log("Recurso "+msg.id+" desconectado");
				break;
			}
		} catch(Exception e) {
			log.log("Error managing station message: "+e.getClass()+": "+e.getMessage()+": "+e.getLocalizedMessage(),Logger.ERROR);
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
				manageMsg(msgIn.take());
			} catch(Exception e) {
				log.log("receivingTask exception: "+e.getLocalizedMessage(), Logger.DEBUG);
			}
		}
	}
	
	public void closedConnectionsTask() {
		MessageParser element;
		log.log("closedConnectionsTask started", Logger.DEBUG);
		while(true) {
			log.log("closedConnectionsTask checking connections", Logger.DEBUG);
			lockConnections.lock();
			try {
				for(int i = 0 ; i < connections.size() ; i++) {
					element = connections.get(i);
					if(!element.getConnection().isConnected()) {
						connections.remove(element);
						log.log("Closed station connection removed", Logger.DEBUG);
						Set<Integer> s = recursos.keySet();
						ArrayList<Integer> del = new ArrayList<Integer>();
						synchronized(recursos) {
							Iterator<Integer> it = s.iterator();
							while(it.hasNext()) {
								Integer id = it.next();
								if(element == recursos.get(id).getConnection()) {
									del.add(id);
								}
							}
						}
						for(int j = 0 ; j < del.size() ; j++) {
							recursos.remove(del.get(j));
							log.log("Resource "+del.get(j)+" connection removed", Logger.DEBUG);
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
