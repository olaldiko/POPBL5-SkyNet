package controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.postgresql.Driver;

import data.Definitions;
import domain.User;
import domain.UserFacade;

/**
 * Servlet implementation class MapServlet
 */
@WebServlet("/Registrarse")
public class RegisterServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
    	super();
    	try {
    		if (!Driver.isRegistered()) Driver.register();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String next_page = Definitions.registerPage;
		String event = request.getParameter("action");
		if (event != null) {
			switch(event) {
				case "Registrarse":
					next_page = Definitions.indexPage;
					request.setAttribute("mensaje", registrarse(request));
					break;
				default: break;
			}
		}
		response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(next_page);
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public String registrarse(HttpServletRequest request) {
		User user = new User("0",
							 "0",
							 request.getParameter("nombre"),
							 request.getParameter("apellido"),
							 request.getParameter("direccion"),
							 request.getParameter("telefono"),
							 request.getParameter("DNI"),
							 request.getParameter("notas"),
							 request.getParameter("user"),
							 request.getParameter("password"));
		UserFacade uf;
		try {
			uf = new UserFacade();
		} catch (SQLException e) {
			e.printStackTrace();
			return "ERROR: Error al registrar el usuario";
		}
		if (request.getParameter("password").equals(request.getParameter("password2"))) {
			int result = uf.register(user);
			if (result == -1) {
				return "ERROR: Usuario exitente";
			} else if (result == -2) {
				return "ERROR: Error al registrar el usuario";
			} else return "Usuario registrado con exito";
		} else return "ERROR: Las contraseñas no coinciden";
	}

}
