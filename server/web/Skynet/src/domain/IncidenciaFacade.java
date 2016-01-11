package domain;

import java.sql.SQLException;

public class IncidenciaFacade {

	public final PostgreSQLConnector c;
	//public final String password = "9BLKMyQDeYwjCjzVXgSskAM6";
	public final String password = "dotty2048";
	
	public IncidenciaFacade() throws SQLException {
		c = new PostgreSQLConnector("localhost:5432","Skynet",
				"postgres", password);
	}
	
	public boolean reportar(Incidencia i) {
		try {
			if(Integer.valueOf(c.query(reportarFunction(i)).getResult(0, 0))>0) return true;
		} catch(Exception e) {}
		return false;
	}
	
	public String reportarFunction(Incidencia i) {
		String s = "";
		s += "select f_nueva_incidencia(";
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
	
	public String[][] tiposIncidencia() {
		try {
			return c.query("SELECT * FROM f_get_tipos_incidencia()").toStringMatrix();
		} catch(Exception e) {
			e.printStackTrace();
			return new String[0][0];
		}
	}
	
}
