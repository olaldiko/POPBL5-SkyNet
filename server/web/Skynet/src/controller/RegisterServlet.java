package controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.postgresql.Driver;

import domain.User;
import domain.UserFacade;

/**
 * Servlet implementation class MapServlet
 */
@WebServlet("/Registrarse")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String PAGE_REGISTER = "/pages/registrarse.jsp";
	private static final String PAGE_REGISTERED = "/pages/mensaje.jsp";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
    	super();
        try {
			Driver.register();
		} catch (Exception e) {}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String next_page = PAGE_REGISTER;
		String event = request.getParameter("action");
		
		if(event != null) {
			switch(event) {
			case "Registrarse":
				next_page = PAGE_REGISTERED;
				request.setAttribute("mensaje", registrarse(request));
				break;
			}
		}
		
		// Forward
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
		
		UserFacade uf = null;
		User user = null;
		
		try {
			user = new User("0","0",request.getParameter("nombre"),request.getParameter("apellido"),
					request.getParameter("direccion"),request.getParameter("telefono"), request.getParameter("DNI"),
					request.getParameter("notas"),request.getParameter("user"),request.getParameter("password"));
			uf = new UserFacade();
			if (uf.register(user)) return "Usuario registrado con éxito";
			else return "Datos incorrectos";
		} catch(Exception e){}
		
		return "Error al registrar el usuario";
		
	}

}
