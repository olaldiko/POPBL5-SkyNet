package domain;

import java.io.Serializable;

public class Incidencia implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String tipoincidenciaid;
	private String fechanotificacion;
	private String fecharesolucion;
	private String ubicacionlat;
	private String ubicacionlng;
	private String usuarioid;
	private String telefono;
	private String notas;
	private String gravedad;
	private String numeroafectados;
	private String resolucion;

	public Incidencia() {}
	
	public Incidencia(String tipoincidenciaid, String fechanotificacion, String fecharesolucion, String ubicacionlat, String ubicacionlng, 
			String usuarioid, String telefono, String notas, String gravedad, String numeroafectados, String resolucion) {
		this.tipoincidenciaid = tipoincidenciaid;
		this.fechanotificacion = fechanotificacion;
		this.fecharesolucion = fecharesolucion;
		this.ubicacionlat = ubicacionlat;
		this.ubicacionlng = ubicacionlng;
		this.usuarioid = usuarioid;
		this.telefono = telefono;
		this.notas = notas;
		this.gravedad = gravedad;
		this.numeroafectados = numeroafectados;
		this.resolucion = resolucion;
	}
	
	public Incidencia(QueryResult r, int index) {
		this.tipoincidenciaid = r.getResult(index, "tipoincidenciaid");
		this.fechanotificacion = r.getResult(index, "fechanotificacion");
		this.fecharesolucion = r.getResult(index, "fecharesolucion");
		this.ubicacionlat = r.getResult(index, "ubicacionlat");
		this.ubicacionlng = r.getResult(index, "ubicacionlng");
		this.usuarioid = r.getResult(index, "usuarioid");
		this.telefono = r.getResult(index, "telefono");
		this.notas = r.getResult(index, "notas");
		this.gravedad = r.getResult(index, "gravedad");
		this.numeroafectados = r.getResult(index, "numeroafectados");
		this.resolucion = r.getResult(index, "resolucion");
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getTipoincidenciaid() {
		return tipoincidenciaid;
	}

	public void setTipoincidenciaid(String tipoincidenciaid) {
		this.tipoincidenciaid = tipoincidenciaid;
	}

	public String getFechanotificacion() {
		return fechanotificacion;
	}

	public void setFechanotificacion(String fechanotificacion) {
		this.fechanotificacion = fechanotificacion;
	}

	public String getFecharesolucion() {
		return fecharesolucion;
	}

	public void setFecharesolucion(String fecharesolucion) {
		this.fecharesolucion = fecharesolucion;
	}

	public String getUbicacionlat() {
		return ubicacionlat;
	}

	public void setUbicacionlat(String ubicacionlat) {
		this.ubicacionlat = ubicacionlat;
	}

	public String getUbicacionlng() {
		return ubicacionlng;
	}

	public void setUbicacionlng(String ubicacionlng) {
		this.ubicacionlng = ubicacionlng;
	}

	public String getUsuarioid() {
		return usuarioid;
	}

	public void setUsuarioid(String usuarioid) {
		this.usuarioid = usuarioid;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getNotas() {
		return notas;
	}

	public void setNotas(String notas) {
		this.notas = notas;
	}

	public String getGravedad() {
		return gravedad;
	}

	public void setGravedad(String gravedad) {
		this.gravedad = gravedad;
	}

	public String getNumeroafectados() {
		return numeroafectados;
	}

	public void setNumeroafectados(String numeroafectados) {
		this.numeroafectados = numeroafectados;
	}

	public String getResolucion() {
		return resolucion;
	}

	public void setResolucion(String resolucion) {
		this.resolucion = resolucion;
	}
	
}
