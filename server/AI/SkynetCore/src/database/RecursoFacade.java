package database;

import ia.Route;
import configuration.Configuration;
import configuration.Logger;

public class RecursoFacade {

	private SQLConnector dao;
	private Logger log;
	
	public RecursoFacade() {
		dao = Configuration.getCurrent().getDao();
		log = Configuration.getCurrent().getLogger();
	}
	
	public Recurso getRecurso(int id) {
		Recurso res = new Recurso();
		QueryResult r = dao.query("SELECT * FROM f_get_recurso("+id+")");
		if(r.getResult(0, "estacionid")!=null) {
			QueryResult r2 = dao.query("SELECT * FROM f_get_estacion("+r.getResult(0, "estacionid")+")");
			QueryResult r3 = dao.query("SELECT * FROM f_get_location_resource("+id+")");
			res.id = id;
			res.estacionId = Integer.valueOf(r.getResult(0, "estacionid"));
			if(r2.getResult(0,"tiporecursoid")!=null) {
				res.tipo = Integer.valueOf(r2.getResult(0,"tiporecursoid"));
			}
			else return null;
			if(r3.getResult(0, "estado")!=null) {
				try {
					res.estado = Integer.valueOf(r3.getResult(0, "estado"));
					res.lat = Double.valueOf(r3.getResult(0, "lat"));
					res.lng = Double.valueOf(r3.getResult(0, "lng"));
				} catch(Exception e) {
					res.estado = 0;
					res.lat = Double.valueOf(r2.getResult(0, "ubicacionestacionlat"));
					res.lng = Double.valueOf(r2.getResult(0, "ubicacionestacionlng"));
				}
			}
			else {
				res.estado = 0;
				res.lat = Double.valueOf(r2.getResult(0, "ubicacionestacionlat"));
				res.lng = Double.valueOf(r2.getResult(0, "ubicacionestacionlng"));
			}
			return res;
		}
		else return null;
	}
	
	public Recurso nuevoRecurso(int estacion) {
		Recurso res = new Recurso();
		QueryResult r = dao.query("SELECT * FROM f_get_estacion("+estacion+")");
		if(r.getResult(0,"tiporecursoid")!=null) {
			int id = Integer.valueOf(dao.query("SELECT * FROM f_nuevo_recurso("+estacion+")").getResult(0,0));
			if(id>0) {
				res.id = id;
				res.estacionId = estacion;
				res.tipo = Integer.valueOf(r.getResult(0,"tiporecursoid"));
				res.estado = 0;
				res.lat = Double.valueOf(r.getResult(0, "ubicacionestacionlat"));
				res.lng = Double.valueOf(r.getResult(0, "ubicacionestacionlng"));
				log.log("Nuevo recurso creado: ID="+id+" ESTACION="+estacion);
				return res;
			}
		}
		log.log("Creacion de nuevo recurso fallida: ESTACION = "+estacion,Logger.DEBUG);
		return null;
	}
	
	public int actualizarRecurso(int id, int estado, double lat, double lng) {
		int res = Integer.valueOf(dao.query("SELECT * FROM f_actualizar_posicion("+id+","+estado+","+lat+","+lng+")").getResult(0,0));
		log.log("f_actualizar_posicion("+id+","+estado+","+lat+","+lng+") = "+res,Logger.DEBUG);
		return res;
	}
	
	public Route getRutaEstacionMasCercana(Recurso rec) {
		Estacion[] estaciones = Estacion.getEstaciones(dao.query("SELECT * FROM f_get_estaciones("+rec.tipo+")"));
		Route rCercana = null, rActual;
		try {
			rCercana = new Route(rec,estaciones[0]);
			rActual = rCercana;
			for(int i = 1 ; i < estaciones.length ; i++) {
				rActual = new Route(rec,estaciones[i]);
				if(rActual.getTime()<rCercana.getTime()) rCercana = rActual;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rCercana;
	}
	
}
