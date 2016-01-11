package domain;

import java.sql.SQLException;

public class UserFacade {
	
	public final PostgreSQLConnector c;
	//public final String password = "9BLKMyQDeYwjCjzVXgSskAM6";
	public final String password = "dotty2048";
	
	public UserFacade() throws SQLException {
		c = new PostgreSQLConnector("localhost:5432","Skynet",
				"postgres", password);
	}

	public User login(String user, String password) {
		try {
			QueryResult r = c.query(loginFunction(user,password));
			if(r!=null && r.getRows()>0) return new User(r,0);
		} catch(Exception e) {}
		return null;
	}
	
	public boolean register(User u) {
		try {
			if(Integer.valueOf(c.query(registerFunction(u)).getResult(0, 0))>0) return true;
		} catch(Exception e) {}
		return false;
	}
	
	public String loginFunction(String user, String password) {
		return "select * from f_check_user('"+user+"', '"+password+"')";
	}
	
	public String registerFunction(User u) {
		String s = "";
		s += "select f_insert_user(";
		s += "'"+u.getNombre()+"',";
		s += "'"+u.getApellido()+"',";
		s += "'"+u.getDireccion()+"',";
		s += "'"+u.getTelefono()+"',";
		s += "'"+u.getNotas()+"',";
		s += "'"+u.getDNI()+"',";
		s += "'"+u.getUsername()+"',";
		s += "'"+u.getPassword()+"')";
		return s;
	}
	
}
