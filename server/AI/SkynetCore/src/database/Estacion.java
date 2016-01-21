package database;

public class Estacion {
	public int id;
	public int tipo;
	public double lat;
	public double lng;
	public static Estacion[] getEstaciones(QueryResult r) {
		Estacion[] estaciones = new Estacion[r.getRows()];
		for(int i = 0 ; i < r.getRows() ; i++) {
			try {
				estaciones[i] = new Estacion();
				estaciones[i].id = Integer.valueOf(r.getResult(i, "estacionid"));
				estaciones[i].tipo = Integer.valueOf(r.getResult(i, "tiporecursoid"));
				estaciones[i].lat = Double.valueOf(r.getResult(i, "ubicacionestacionlat"));
				estaciones[i].lng = Double.valueOf(r.getResult(i, "ubicacionestacionlng"));
			} catch(Exception e) {
				e.printStackTrace();
				return new Estacion[0];
			}
		}
		return estaciones;
	}
}
