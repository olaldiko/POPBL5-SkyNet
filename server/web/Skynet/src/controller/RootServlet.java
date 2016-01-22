package controller;

import java.io.IOException;
import java.net.UnknownHostException;
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
import domain.Incidencia;
import domain.IncidenciaFacade;
import domain.TCPConnection;
import domain.User;

/**
 * Servlet implementation class RootServlet
 */
@WebServlet("/Avisos")
public class RootServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static final byte[] send = {0};
	private TCPConnection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RootServlet() {
        super();
        try {
        	if (connection == null) connect();
        	if (!Driver.isRegistered()) Driver.register();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String nextPage = Definitions.indexPage;
		String event = request.getParameter("action");
		response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		if (event != null) {
			switch (event) {
			case "AYUDA":
				if (reportarIncidencia(request)) {
					nextPage = Definitions.mapPageS;
					response.sendRedirect(request.getContextPath() + nextPage);
					return;
				} else {
					nextPage = Definitions.indexPage;
					request.setAttribute("mensaje", "Error al enviar la incidencia");
				}
				break;
			}
		}
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextPage);
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public boolean reportarIncidencia(HttpServletRequest request) {
		HttpSession s = request.getSession(false);
		User u = null;
		if (s != null) {
			u = (User) s.getAttribute("user");
		}
		Double[] loc = new Double[2];
		loc[0] = Double.parseDouble(request.getParameter("lat"));
		loc[1] = Double.parseDouble(request.getParameter("lng"));
		Incidencia incidencia = new Incidencia(request.getParameter("tipo"),
											   null,
											   null,
											   loc[0].toString(),
											   loc[1].toString(),
											   u != null ? u.getUsuarioId(): "0",
											   request.getParameter("telefono"),
											   request.getParameter("observaciones"),
											   request.getParameter("gravedad"),
											   request.getParameter("personas"),
											   null);
		IncidenciaFacade ifacade = null;
		try {
			ifacade = new IncidenciaFacade();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		if (ifacade.reportar(incidencia)) {
			if (connection.isConnected()) {
				try {
					connection.write(send);
				} catch (IOException e) {
					try {
						connection.connect();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			} return true;			
		} return false;
	}
	
	public void connect() throws UnknownHostException, IOException {
		connection = new TCPConnection(Definitions.tcpAddress, Definitions.tcpPort);
		connection.connect();
	}

}
