package database;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

public class PostgreSQLConnector extends SQLConnector implements Serializable {

	private static final long serialVersionUID = 1L;

	public PostgreSQLConnector(List<Object> settings) throws SQLException {
		super("jdbc:postgresql://" + (String)settings.get(0), (String)settings.get(1), (String)settings.get(2), (String)settings.get(3));
	}
	
	public PostgreSQLConnector(String url, String database, String user, String password) throws SQLException {
		super("jdbc:postgresql://" + url, database, user, password);
	}

}
