package domain;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLConnector {

	public String url;
	public String database;
	public String user;
	public String password;
	
	public SQLConnector(String url, String database, String user, String password) throws SQLException {
		this.url = url;
		this.database = database;
		this.user = user;
		this.password = password;
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		DriverManager.getConnection(url+"/"+database,user,password);
	}
	
	public QueryResult query(String q) {
		Connection connection = null;
		QueryResult r = null;
		try {
			connection = DriverManager.getConnection(url+"/"+database,user,password);
			r = new QueryResult(connection.prepareStatement(q).executeQuery());
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} return r;
	}
	
	public int update(String q) {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(url+"/"+database, user, password);
			int r = connection.prepareStatement(q).executeUpdate();
			connection.close();
			return r;
		} catch (SQLException e) {
			e.printStackTrace();
		} return -1;
	}
	
	public boolean transaction(String q) {
		String [] s = {q};
		return transaction(s);
	}
	
	public boolean transaction(String [] q) {
		Connection connection = null;
		PreparedStatement s;
		boolean b = true;
		try {
			connection = DriverManager.getConnection(url+"/"+database, user, password);
			connection.setAutoCommit(false);
			for(int i = 0 ; i < q.length ; i++) {
				s = connection.prepareStatement(q[i]);
				s.executeUpdate();
				s.close();
			}
			connection.commit();
		} catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
					b = false;
				} catch (SQLException e1) {
					b = false;
					e1.printStackTrace();
				}
			}
		} finally {
			if (connection != null) {
				try {
					connection.setAutoCommit(true);
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return b;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
