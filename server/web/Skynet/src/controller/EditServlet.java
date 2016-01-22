package controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.postgresql.Driver;

import data.Definitions;
import domain.User;
import domain.UserFacade;

/**
 * Servlet implementation class MapServlet
 */
@WebServlet("/Editar")
public class EditServlet extends HttpServlet {
		
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EditServlet() {
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
		String next_page = Definitions.profilePage;
		String event = request.getParameter("action");
		response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		if (event != null) {
			switch(event) {
				case "Editar":
					next_page = Definitions.indexPage;
					request.setAttribute("mensaje", edit(request));
					break;
				default: break;
			}
		}
		User u = getUser(request);
		request.getSession().setAttribute("editedUser", u);
		request.getSession().setAttribute("user", u);
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(next_page);
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public User getUser(HttpServletRequest request) {
		try {
			HttpSession s = request.getSession(false);
			if (s != null) {
				return new UserFacade().getUser(((User) s.getAttribute("user")).getUsername());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} return null;
	}
	
	public String edit(HttpServletRequest request) {
		UserFacade uf = null;
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
		try {
			uf = new UserFacade();
		} catch (SQLException e) {
			e.printStackTrace();
			return "ERROR: Error al editar el usuario";
		}
		if (request.getParameter("password").equals(request.getParameter("password2"))) {
			int result = uf.edit(user);
			if (result == -1) {
				return "ERROR: El usuario no existe";
			} else if (result == -2) {
				return "ERROR: Error al editar el usuario";
			} else return "Usuario editado con exito";
		} else return "ERROR: Las contraseñas no coinciden";
	}
	
}
