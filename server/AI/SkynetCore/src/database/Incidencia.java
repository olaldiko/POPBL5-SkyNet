package database;

public class Incidencia {

	public int id;
	public int tipo;
	public int afectados;
	public int resolucion;
	public int prioridad;
	public double lat;
	public double lng;
	
	IncidenciaFacade ifac;
	
	public Incidencia() {
		ifac = new IncidenciaFacade();
	}
	
	public Integer getPrioridad() {
		prioridad = ifac.getPrioridad(id);
		return prioridad;
	}
	
	public Integer personaRecogida() {
		return ifac.personaRecogida(id);
	}
	
	public String toString() {
		return id+"#"+lat+"#"+lng;
	}
	
	public void cerrarIncidencia() {
		ifac.finIncidencia(id);
	}
	
}
