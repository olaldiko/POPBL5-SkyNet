package domain;

import java.sql.SQLException;

public class PostgreSQLConnector extends SQLConnector {

	public PostgreSQLConnector(String url, String database, String user, String password) throws SQLException {
		super("jdbc:postgresql://"+url, database, user, password);
	}

}
