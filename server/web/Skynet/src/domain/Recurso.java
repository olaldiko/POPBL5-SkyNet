package domain;

public class Recurso {
	
	private int id;
	private String fecha;
	private String lat;
	private String lng;
	
	public Recurso(int id, String fecha, String lat, String lng) {
		this.id = id;
		this.fecha = fecha;
		this.lat = lat;
		this.lng = lng;
	}
	
	public Recurso() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

}
