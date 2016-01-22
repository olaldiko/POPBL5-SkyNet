package domain;

import java.sql.SQLException;

import data.Definitions;

public class RecursoFacade {

	public final PostgreSQLConnector c;
	
	public RecursoFacade() throws SQLException {
		c = new PostgreSQLConnector(Definitions.dbAddress+":"+Definitions.dbPort, Definitions.dbName, Definitions.dbUser, Definitions.dbPass);
	}
	
	public Recurso[] getWorkingRecursos() {
		QueryResult r = c.query("SELECT * FROM f_get_recursos_working()");
		Recurso [] re = new Recurso[r.getRows()];
		if (r.getResult(0, "id") == null) {
			return new Recurso[0];
		}
		for (int i = 0; i < r.getRows(); i++) {
			re[i] = new Recurso();
			re[i].setId(Integer.parseInt((r.getResult(i, "id"))));
			re[i].setFecha(r.getResult(i, "f"));
			re[i].setLat(r.getResult(i, "lat"));
			re[i].setLng(r.getResult(i, "lng"));
		} return re;
	}

}
