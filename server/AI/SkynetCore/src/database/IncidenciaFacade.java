package database;

import configuration.Configuration;

public class IncidenciaFacade {
	
	private SQLConnector dao;
	
	public IncidenciaFacade() {
		dao = Configuration.getCurrent().getDao();
	}

	public Incidencia[] getIncidenciasAbiertas() {
		QueryResult r = dao.query("SELECT * FROM f_get_incidencias_abiertas()");
		Incidencia[] inc = new Incidencia[r.getRows()];
		if(r.getResult(0, "incidenciaid")==null) return new Incidencia[0];
		for(int i = 0 ; i < inc.length ; i++) {
			inc[i] = new Incidencia();
			inc[i].id = Integer.valueOf(r.getResult(i, "incidenciaid"));
			inc[i].tipo = Integer.valueOf(r.getResult(i, "tipoincidenciaid"));
			inc[i].afectados = Integer.valueOf(r.getResult(i, "numeroafectados"));
			inc[i].resolucion = Integer.valueOf(r.getResult(i, "resolucion"));
			inc[i].lat = Double.valueOf(r.getResult(i, "ubicacionlat"));
			inc[i].lng = Double.valueOf(r.getResult(i, "ubicacionlng"));
		}
		return inc;
	}
	
	public Integer getPrioridad(int id) {
		return Integer.valueOf(dao.query("SELECT * FROM f_get_severity_incident("+id+")").getResult(0,0));
	}
	
	public Integer personaRecogida(int id) {
		return Integer.valueOf(dao.query("SELECT * FROM f_persona_recogida("+id+")").getResult(0,0));
	}
	
	public Integer finIncidencia(int id) {
		return Integer.valueOf(dao.query("SELECT * FROM f_fin_incidencia("+id+")").getResult(0,0));
	}
	
}
