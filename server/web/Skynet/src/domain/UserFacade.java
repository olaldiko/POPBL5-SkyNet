package domain;

import java.sql.SQLException;

import data.Definitions;

public class UserFacade {
	
	public final PostgreSQLConnector c;
	
	public UserFacade() throws SQLException {
		c = new PostgreSQLConnector(Definitions.dbAddress+":"+Definitions.dbPort, Definitions.dbName, Definitions.dbUser, Definitions.dbPass);
	}

	public User login(String user, String password) {
		QueryResult r = c.query(loginFunction(user, password));
		if ((r != null) && (r.getRows() > 0)) {
			return new User(r, 0);
		} return null;
	}
	
	public int register(User u) {
		int result = Integer.valueOf(c.query(registerFunction(u)).getResult(0, 0));
		if (result == -1) {
			return -1;
		} else if (result > 0) {
			return result;
		} else {
			return -2;
		}
	}
	
	public int edit(User u) {
		int result = Integer.valueOf(c.query(editFunction(u)).getResult(0, 0));
		if (result == -1) {
			return -1;
		} else if (result > 0) {
			return result;
		} else {
			return -2;
		}
	}
	
	public String loginFunction(String user, String password) {
		return "SELECT * FROM f_check_user('"+user+"', '"+password+"')";
	}
	
	public String registerFunction(User u) {
		String s = "";
		s += "SELECT f_insert_user(";
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
	
	public String editFunction(User u) {
		String s = "";
		s += "SELECT f_edit_user(";
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
	

	public User getUser(String username) {
		QueryResult r = c.query("SELECT * FROM f_get_user('"+username+"');");
		User u = new User();
		u.setNombre(r.getResult(0, "nom"));
		u.setApellido(r.getResult(0, "ape"));
		u.setDireccion(r.getResult(0, "dir"));
		u.setTelefono(r.getResult(0, "tlf"));
		u.setNotas(r.getResult(0, "obs"));
		u.setDNI(r.getResult(0, "v_dni"));
		u.setUsername(r.getResult(0, "v_user"));
		u.setPassword(r.getResult(0, "pass"));
		return u;
	}
	
}
