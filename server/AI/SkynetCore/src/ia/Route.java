package ia;

import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

import database.Estacion;
import database.Incidencia;
import database.Recurso;

public class Route {
	
	public String json;
	
	public double origenlat;
	public double origenlng;
	public double destinolat;
	public double destinolng;
	
	public Route(double origenlat, double origenlng, double destinolat, double destinolng) throws Exception {
		this.origenlat = origenlat;
		this.origenlng = origenlng;
		this.destinolat = destinolat;
		this.destinolng = destinolng;
		getJson();
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
		return "";
	}
	
	public Long getTime() {
		return 0L;
	}
	
	public void getJson() throws Exception {
		String req = "GET /maps/api/directions/json?origin="+origenlat+","+origenlng+"&destination="+destinolat+","+destinolng+" HTTP/1.1\r\nHost: maps.googleapis.com\r\nConnection: keep-alive\r\nAccept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\r\nUpgrade-Insecure-Requests: 1\r\nUser-Agent: Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36\r\nAccept-Encoding: \r\nAccept-Language: en-US,en;q=0.8\r\n\r\n";
		Socket s = new Socket("maps.googleapis.com",80);
		PrintStream p = new PrintStream(s.getOutputStream());
		p.print(req);
		Scanner scan = new Scanner(s.getInputStream());
		json = readJson(scan);
		scan.close();
		s.close();
	}
	
	public String readJson(Scanner s) {
		String json = "";
		boolean started = false;
		int cont = 0;
		while(s.hasNext() && (!started || cont>0)) {
			String line = s.nextLine();
			if(!started) {
				if(countChars(line,'{')>0) {
					started=true;
					cont += countChars(line,'{');
					cont -= countChars(line,'}');
					json += line+"\r\n";
				}
			}
			else {
				cont += countChars(line,'{');
				cont -= countChars(line,'}');
				json += line+"\r\n";
			}
		}
		return json;
	}
	
	public int countChars(String s, char c) {
		int cont = 0;
		boolean inString = false;
		for(int i = 0 ; i < s.length() ; i++) {
			if(s.charAt(i)=='\"') inString = !inString;
			if(!inString && s.charAt(i)==c) cont++;
		}
		return cont;
	}
	
}
