package database;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.postgresql.*;

@SuppressWarnings("unused")
public class SQLConnector implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String url;
	public String database;
	public String user;
	public String password;
	
	public SQLConnector(String url, String database, String user, String password) throws SQLException {
		this.url=url;
		this.database=database;
		this.user=user;
		this.password=password;
		//DriverManager.setLoginTimeout(5);
		//DriverManager.getConnection(url+"/"+database,user,password);
	}
	
	public QueryResult query(String q) {
		try {
			Connection connection = DriverManager.getConnection(url+"/"+database,user,password);
			QueryResult r = new QueryResult(connection.prepareStatement(q).executeQuery());
			connection.close();
			return r;
		} catch(Exception e){}
		return null;
	}
	
	public int update(String q) {
		try {
			Connection connection = DriverManager.getConnection(url+"/"+database,user,password);
			int r = connection.prepareStatement(q).executeUpdate();
			connection.close();
			return r;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public boolean transaction(String q) {
		String[] s = {q};
		return transaction(s);
	}
	
	public boolean transaction(String[] q) {
		Connection connection = null;
		PreparedStatement s;
		boolean b = true;
		try {
			connection = DriverManager.getConnection(url+"/"+database,user,password);
			connection.setAutoCommit(false);
			for(int i = 0 ; i < q.length ; i++) {
				s = connection.prepareStatement(q[i]);
				s.executeUpdate();
				s.close();
			}
			connection.commit();
		} catch(Exception e) {
			if (connection != null) {
				try {
					connection.rollback();
					b = false;
				} catch (SQLException e1) {b = false;}
			}
		} finally {
			if (connection != null) {
				try {
					connection.setAutoCommit(true);
					connection.close();
				} catch (SQLException e) {}
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
