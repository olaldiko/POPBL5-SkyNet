package ia;

import java.io.Serializable;
import java.util.List;

import configuration.Configuration;
import configuration.Logger;
import connections.Message;
import database.Incidencia;
import database.IncidenciaFacade;
import database.Recurso;
import database.RecursoFacade;

public class AStarSolver implements Solver, Serializable {

private static final long serialVersionUID = 1L;
	
	Logger log;
	MonoSemaphore lock;
	Thread solver;
	Recurso[] recursosId;
	Recurso[] recursos;
	Incidencia[] incidencias;
	
	IncidenciaFacade ifac;
	RecursoFacade rfac;
	
	public AStarSolver(List<String> settings) {
		
		lock = new MonoSemaphore();
		
		ifac = new IncidenciaFacade();
		rfac = new RecursoFacade();
		
		solver = new Thread() {
			public void run() {
				solverTask();
			}
		};
		solver.start();
		
	}
	
	public void init() {
		log = Configuration.getCurrent().getLogger();
		ifac = new IncidenciaFacade();
		rfac = new RecursoFacade();
	}
	
	public synchronized void scheduleSolution(Recurso[] recursos) {
		this.recursosId = recursos;
		lock.release();
		log.log(getClass()+" scheduled", Logger.DEBUG);
	}
	
	private synchronized void setRecursos() {
		recursos = new Recurso[recursosId.length];
		for(int i = 0 ; i < recursos.length ; i++) {
			recursos[i] = rfac.getRecurso(recursosId[i].id);
			recursos[i].setConnection(recursosId[i].getConnection());
		}
	}
	
	public void solverTask() {
		while(true) {
			lock.acquire();
			log.log(getClass()+" started");
			setRecursos();
			incidencias = ifac.getIncidenciasAbiertas();
			assign(solve());
		}
	}
	
	public Solution[] solve() {
		Route route;
		Recurso recurso;
		int recursoId;
		Solution[] sol = new Solution[incidencias.length];
		if(incidencias.length>0 && recursos.length>0) {
			for(int i = 0 ; i < sol.length ; i++) {
				try {
					recursoId = 0;
					while(recursos[recursoId].tipo!=incidencias[i].tipo || recursos[recursoId].estado!=Recurso.ESTADO_LIBRE) recursoId++;
					recurso = recursos[recursoId];
					route = new Route(recurso,incidencias[i]);
					sol[i] = new Solution(incidencias[i],recurso,route);
					recurso.estado = Recurso.ESTADO_EN_RUTA;
				} catch(Exception e){e.printStackTrace();}
			}
		}
		return sol;
	}
	
	public void assign(Solution[] solution) {
		log.log(getClass()+" solved, sending messages");
		//Asignar las rutas para resolver incidencias
		for(int i = 0 ; i < solution.length ; i++) {
			try {
				log.log("Sending incidencia "+solution[i].incidencia.id+" to recurso "+solution[i].recurso.id+", route takes "+solution[i].ruta.getTimeString(), Logger.DEBUG);
				solution[i].recurso.getConnection().writeMessage(new Message(solution[i].recurso.id,"ROUTE",solution[i].ruta.toString()));
				if(solution[i].recurso.estado==Recurso.ESTADO_EN_RUTA) solution[i].recurso.estado = -9;
			} catch(Exception e){
				log.log("Error sending incidencia "+(solution[i]==null ? "NULL":solution[i].incidencia),Logger.ERROR);
			}
		}
		//Avisar a los que ya no tienen que hacer nada
		log.log("Sending void routes to previously busy resources");
		for(int i = 0 ; i < recursos.length ; i++) {
			try {
				if(recursos[i].estado==Recurso.ESTADO_EN_RUTA)
					recursos[i].getConnection().writeMessage(new Message(recursos[i].id,"ROUTE",""));
				else if(recursos[i].estado==-9)
					solution[i].recurso.estado = Recurso.ESTADO_EN_RUTA;
			} catch(Exception e){log.log("Error sending void route to recurso "+recursos[i].id,Logger.ERROR);}
		}
		log.log(getClass()+" finished");
	}
	
}
