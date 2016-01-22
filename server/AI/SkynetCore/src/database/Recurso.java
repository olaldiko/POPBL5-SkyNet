package database;

import ia.Route;
import connections.MessageParser;

public class Recurso {
	
	public static final int ESTADO_LIBRE = 0;
	public static final int ESTADO_EN_RUTA = 1;
	public static final int ESTADO_DE_VUETA = 2;

	public int id;
	public int estacionId;
	public int tipo;
	public int estado;
	public double lat;
	public double lng;
	
	MessageParser connection;
	
	RecursoFacade rf;
	
	public Recurso() {
		rf = new RecursoFacade();
	}
	
	public int actualizarEstado(int estado) {
		int res = rf.actualizarRecurso(id, estado, lat, lng);
		if(res>0) this.estado = estado;
		return res;
	}
	
	public int actualizarposicion(double lat, double lng) {
		int res = rf.actualizarRecurso(id, estado, lat, lng);
		if(res>0) {
			this.lat = lat;
			this.lng = lng;
		}
		return res;
	}
	
	public int actualizarposicion(String msg) {
		String[] pos = null;
		try {
			pos = msg.split("[,]");
			return actualizarposicion(Double.valueOf(pos[0]),Double.valueOf(pos[1]));
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -3;
	}

	public MessageParser getConnection() {
		return connection;
	}

	public void setConnection(MessageParser connection) {
		this.connection = connection;
	}
	
	public Route getRutaEstacionMasCercana() {
		return rf.getRutaEstacionMasCercana(this);
	}
	
}
