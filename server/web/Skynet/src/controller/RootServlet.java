package controller;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.postgresql.Driver;

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
	
	private static final String PAGE_INDEX = "/index.jsp";
	private static final String PAGE_ERROR = "/pages/mensaje.jsp";
	private static final String PAGE_MAPA = "/Mapa";
	
	private static final byte[] send = {0};
	private TCPConnection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RootServlet() {
        super();
        try {
        	connect();
        } catch (Exception e) {e.printStackTrace();}
        try {
			Driver.register();
		} catch (Exception e) {}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String nextPage = PAGE_INDEX;
		String event = request.getParameter("action");
		
		response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		
		if(event != null) {
			switch(event) {
			case "AYUDA":
				if(reportarIncidencia(request)) {
					nextPage = PAGE_MAPA;
					// Forward
					response.sendRedirect(request.getContextPath() + nextPage);
					return;
				}
				else {
					nextPage = PAGE_ERROR;
					request.setAttribute("mensaje", "Error al enviar la incidencia");
				}
				break;
			}
		}
		
		// Forward
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
		
		IncidenciaFacade ifacade = null;
		Incidencia incidencia = null;
		
		try {
			//Usuario
			User u = null;
			HttpSession s = request.getSession(false);
			if(s!=null) u = (User)s.getAttribute("user");
			//Localizacion
			Double[] loc = obtenerLocalizacion(request.getParameter("lugar"));
			//Incidencia
			incidencia = new Incidencia(request.getParameter("tipo"),null,null,
					loc[0].toString(),loc[1].toString(),u!=null ? u.getUsuarioId():"0",request.getParameter("telefono"),
					request.getParameter("observaciones"),request.getParameter("gravedad"),request.getParameter("personas"),null);
			ifacade = new IncidenciaFacade();
			if (ifacade.reportar(incidencia)) {
				//Avisar a la aplicación Java
				try {
					connection.write(send);
				} catch(Exception e1) {
					try {
						connection.connect();
					} catch(Exception e2) {e2.printStackTrace();}
				}
				return true;
			}
		} catch(Exception e){e.printStackTrace();}
		
		return false;
	}
	
	public Double[] obtenerLocalizacion(String lugar) {
		Double[] loc = new Double[2];
		String[] latlng = lugar.split("[,]");
		loc[0] = Double.valueOf(latlng[0]);
		loc[1] = Double.valueOf(latlng[1]);
		return loc;
	}
	
	//Conexión con aplicación Java
	
	public void connect() throws UnknownHostException, IOException {
		connection = new TCPConnection("127.0.0.1",6969);
		connection.connect();
	}

}
