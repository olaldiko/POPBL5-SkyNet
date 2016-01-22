package domain;

import java.sql.SQLException;

import data.Definitions;

public class IncidenciaFacade {

	public final PostgreSQLConnector c;
	
	public IncidenciaFacade() throws SQLException {
		c = new PostgreSQLConnector(Definitions.dbAddress+":"+Definitions.dbPort, Definitions.dbName, Definitions.dbUser, Definitions.dbPass);
	}
	
	public boolean reportar(Incidencia i) {
		if (Integer.valueOf(c.query(reportarFunction(i)).getResult(0, 0)) > 0) return true;
		else return false;
	}
	
	public String reportarFunction(Incidencia i) {
		String s = "";
		s += "SELECT f_nueva_incidencia(";
		s += i.getTipoincidenciaid()+",";
		s += i.getUbicacionlat()+",";
		s += i.getUbicacionlng()+",";
		s += i.getUsuarioid()+",";
		s += "'"+i.getTelefono()+"',";
		s += "'"+i.getNotas()+"',";
		s += i.getGravedad()+",";
		s += i.getNumeroafectados()+")";
		return s;
	}
	
	public Incidencia[] getIncidenciasAbiertas() {
		QueryResult r = c.query("SELECT * FROM f_get_incidencias_abiertas()");
		Incidencia [] inc = new Incidencia[r.getRows()];
		if (r.getResult(0, "incidenciaid") == null) {
			return new Incidencia[0];
		}
		for (int i = 0; i < inc.length; i++) {
			inc[i] = new Incidencia();
			inc[i].setId(Integer.valueOf(r.getResult(i, "incidenciaid")));
			inc[i].setTipoincidenciaid(r.getResult(i, "tipoincidenciaid"));
			inc[i].setNumeroafectados(r.getResult(i, "numeroafectados"));
			inc[i].setUbicacionlat(r.getResult(i, "ubicacionlat"));
			inc[i].setUbicacionlng(r.getResult(i, "ubicacionlng"));
			inc[i].setFechanotificacion(r.getResult(i, "fechanotificacion"));
			inc[i].setNotas(r.getResult(i, "notas"));
			inc[i].setFecharesolucion(r.getResult(i, "fecharesolucion"));
			inc[i].setResolucion(r.getResult(i, "resolucion"));
			inc[i].setGravedad(r.getResult(i, "gravedad"));
		} return inc;
	}
	
	public String[][] tiposIncidencia() {
		return c.query("SELECT * FROM f_get_tipos_incidencia()").toStringMatrix();
	}
	
}
