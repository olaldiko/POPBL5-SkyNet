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
import domain.Incidencia;
import domain.IncidenciaFacade;
import domain.Recurso;
import domain.RecursoFacade;

/**
 * Servlet implementation class SkynetServlet
 */
@WebServlet("/Ajax")
public class AjaxServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AjaxServlet() {
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
		String next_page = Definitions.ajaxPage;
		request.getSession().setAttribute("arrayIncidencias", getIncidencias());
		request.getSession().setAttribute("arrayRecursos", getRecursos());
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
	
	private String getIncidencias() {
		boolean something = false;
		Incidencia [] i = null;
		try {
			i = new IncidenciaFacade().getIncidenciasAbiertas();
			String data = "";
			for (int j = 0; j < i.length; j++) {
				data += i[j].getUbicacionlat();
				data += ">";
				data += i[j].getUbicacionlng();
				data += ">";
				data += i[j].getId();
				data += ">";
				data += i[j].getFechanotificacion();
				data += ">";
				data += i[j].getGravedad();
				data += ">";
				data += i[j].getNotas();
				data += ">";
				data += i[j].getNumeroafectados();
				data += ">";
				data += i[j].getFecharesolucion() == null ? "Sin resolver": i[j].getFecharesolucion();
				data += ">";
				data += i[j].getTipoincidenciaid();
				data += "<";
				something = true;
			}
			if (something) return data;
			else return null;
		} catch (SQLException e) {
			e.printStackTrace();
		} return "error";
	}
	
	private String getRecursos() {
		boolean something = false;
		Recurso [] r = null;
		try {
			r = new RecursoFacade().getWorkingRecursos();
			String data = "";
			for (int j = 0; j < r.length; j++) {
				data += r[j].getId();
				data += ">";
				data += r[j].getFecha();
				data += ">";
				data += r[j].getLat();
				data += ">";
				data += r[j].getLng();
				data += "<";
				something = true;
			}
			if (something) return data;
			else return null;
		} catch (SQLException e) {
			e.printStackTrace();
		} return "error";
	}

}

