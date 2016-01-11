package database;

public class Incidencia {

	public int id;
	public int tipo;
	public int afectados;
	public int resolucion;
	public double lat;
	public double lng;
	
	IncidenciaFacade ifac;
	
	public Incidencia() {
		ifac = new IncidenciaFacade();
	}
	
	public Integer getPrioridad() {
		return ifac.getPrioridad(id);
	}
	
	public Integer personaRecogida() {
		return ifac.personaRecogida(id);
	}
	
	public String toString() {
		return id+"#"+lat+"#"+lng;
	}
	
}
