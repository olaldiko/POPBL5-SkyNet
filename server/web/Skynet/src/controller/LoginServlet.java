package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.postgresql.Driver;

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
			Driver.register();
		} catch (Exception e) {}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		UserFacade uf = null;
		User user = null;
		String nextPage = "/Avisos";
		
		if(request.getSession().getAttribute("page")!=null) nextPage = (String)request.getSession().getAttribute("page");
		try {
			uf = new UserFacade();
			user = uf.login(request.getParameter("user"), request.getParameter("password"));
			if(user != null && user.getUsername() != null) request.getSession().setAttribute("user", user);
			else request.getSession().setAttribute("loginError", "Usuario o contraseña incorrectos");
		} catch (Exception e) {
			request.getSession().setAttribute("loginError", "Error de conexión con la BD");
		}
		
		// Forward
		response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		response.sendRedirect(request.getContextPath() + nextPage);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
