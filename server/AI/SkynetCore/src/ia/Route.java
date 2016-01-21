package ia;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONObject;

import database.Estacion;
import database.Incidencia;
import database.Recurso;

public class Route {
	
	public String json;
	
	public double origenlat;
	public double origenlng;
	public double destinolat;
	public double destinolng;
	
	public long time;
	public String timeString;
	
	URL url;
	
	public Route(double origenlat, double origenlng, double destinolat, double destinolng) throws Exception {
		this.origenlat = origenlat;
		this.origenlng = origenlng;
		this.destinolat = destinolat;
		this.destinolng = destinolng;
		getJson();
		try {
			if(!new JSONObject(json).getString("status").equalsIgnoreCase("OK")) throw new Exception("Failed to fetch route: FROM ("+origenlat+
					","+origenlng+") TO ("+destinolat+","+destinolng+") Status not OK");
			else {
				JSONObject duration = new JSONObject(json).getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration");
				time = duration.getLong("value");
				timeString = duration.getString("text");
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception("Failed to create JSON of route: FROM ("+origenlat+
					","+origenlng+") TO ("+destinolat+","+destinolng+") Status not OK");
		}
	}
	
	public Route(Recurso r, double destinolat, double destinolng) throws Exception {
		this(r.lat,r.lng,destinolat,destinolng);
	}
	
	public Route(Recurso r, Incidencia i) throws Exception {
		this(r.lat,r.lng,i.lat,i.lng);
	}
	
	public Route(Recurso r, Estacion e) throws Exception {
		this(r.lat,r.lng,e.lat,e.lng);
	}
	
	public String toString() {
		return json;
	}
	
	public String getTimeString() {
		return timeString;
	}
	
	public Long getTime() {
		return time;
	}
	
	public void getJson() throws Exception {
		url = new URL("http://maps.googleapis.com/maps/api/directions/json?origin="+origenlat+","+origenlng+"&destination="+destinolat+","+destinolng);
		BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
		String strTemp;
		json = "";
		while (null != (strTemp = br.readLine())) {
			json += strTemp + "\r\n";
		}
		br.close();
		json = new JSONObject(json).toString();
	}
	
}
