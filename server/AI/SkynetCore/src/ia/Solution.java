package ia;

import database.Incidencia;
import database.Recurso;

public class Solution {

	public Incidencia incidencia;
	public Recurso recurso;
	public Route ruta;
	
	public Solution() {}
	
	public Solution(Incidencia incidencia, Recurso recurso, Route ruta) {
		this.incidencia = incidencia;
		this.recurso = recurso;
		this.ruta = ruta;
	}
	
}
