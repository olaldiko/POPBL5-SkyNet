package controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.postgresql.Driver;

import domain.PostgreSQLConnector;

/**
 * Servlet implementation class SkynetServlet
 */
@WebServlet("/DatabaseTest")
public class DatabaseTestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String PAGE_POSTGRE = "/test/postgre.jsp";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DatabaseTestServlet() {
        super();
        try {
			Driver.register();
		} catch (Exception e) {}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String next_page = PAGE_POSTGRE;
		String event = request.getParameter("action");
		
		if(event != null) {
			switch(event) {
			case "query":
				query(request);
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
	
	public void query(HttpServletRequest request) {
		try {
			if(request.getParameter("pgdb") != null && request.getParameter("pguser") != null &&
					 request.getParameter("pgpass") != null && request.getParameter("query") != null) {
				PostgreSQLConnector c = new PostgreSQLConnector("localhost:5432",request.getParameter("pgdb"),
						request.getParameter("pguser"), request.getParameter("pgpass"));
				request.setAttribute("result", c.query(request.getParameter("query")));
			}
		} catch(Exception e){
			request.setAttribute("error", e.getMessage());
		}
	}

}
