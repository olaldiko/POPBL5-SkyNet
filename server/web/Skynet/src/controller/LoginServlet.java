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
 * Servlet implementation class LoginServlet
 */
@WebServlet("/Login")
public class LoginServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
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
		String nextPage = Definitions.indexPageS;
		if (request.getSession().getAttribute("page") != null) {
			nextPage = (String) request.getSession().getAttribute("page");
		}
		UserFacade uf = null;
		try {
			uf = new UserFacade();
		} catch (SQLException e) {
			request.setAttribute("mensaje", "Error de conexion con la BD");
			e.printStackTrace();
		}
		User user = uf.login(request.getParameter("user"), request.getParameter("password"));
		if ((user != null) && (user.getUsername() != null)) {
			request.getSession().setAttribute("user", user);
		} else {
			request.setAttribute("mensaje", "Usuario o contrasena incorrectos");
		}
		response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextPage);
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
