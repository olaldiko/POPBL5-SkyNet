package database;

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
			if(r.getResult(0, "estado")!=null) {
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
		return null;
	}
	
	public int actualizarRecurso(int id, int estado, double lat, double lng) {
		log.log("Recurso "+id+": "+estado+"#"+lat+"#"+lng,Logger.DEBUG);
		return Integer.valueOf(dao.query("SELECT * FROM f_actualizar_posicion("+id+","+estado+","+lat+","+lng+")").getResult(0,0));
	}
	
	public Estacion getEstacionMasCercana(Recurso rec) {
		
		return null;
	}
	
}
