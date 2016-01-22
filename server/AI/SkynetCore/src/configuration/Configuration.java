package configuration;

import ia.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import ui.*;
import connections.*;
import database.*;

public class Configuration implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String configFile = "./SkynetConfig.ini";
	
	public static Configuration current;
	
	/////////////////////DATA/////////////////////
	private Map<Integer,Recurso> recursos;
	private Map<Integer,Incidencia> recursoIncidencia;
	/////////////////////DATA/////////////////////
	
	/////////////////////SOLVER/////////////////////
	private static final int[] defaultWindowSettings = {400,150,520,330};
	public MainWindowSettings windowSettings;
	/////////////////////SOLVER/////////////////////
	
	/////////////////////CONNECTIONS/////////////////////
	private static final String defaultServletConnection = "connections.TCPConnection";
	private static final Object[] defaultServletConnectionSettings = {"127.0.0.1","6969"};
	public Connection servletConnection;
	
	private static final String defaultStationConnection = "connections.TCPConnection";
	private static final Object[] defaultStationConnectionSettings = {"127.0.0.1","5000"};
	public Connection stationConnection;
	
	private static final Object[] defaultMulticastConnectionSettings = {MulticastConnection.FIRST_GROUP,"5000",""};
	public MulticastConnection multicastConnection;
	/////////////////////CONNECTIONS/////////////////////

	/////////////////////SOLVER/////////////////////
	private static final String defaultSolver = "ia.RandomSolver";
	private static final Object[] defaultSolverSettings = {};
	public Solver solver;
	/////////////////////SOLVER/////////////////////
	
	/////////////////////LOGGER/////////////////////
	private static final Object[] defaultLoggerSettings = {"./logs/","0","86400000"};
	public Logger logger;
	/////////////////////LOGGER/////////////////////
	
	/////////////////////DATABASE/////////////////////
	private static final String defaultDao = "database.PostgreSQLConnector";
	private static final Object[] defaultDaoSettings = {"jdbc:postgresql://localhost:5432","Skynet","postgres", "dotty2048"};
	public SQLConnector dao;
	/////////////////////DATABASE/////////////////////
	
	public Configuration() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		setRecursoIncidencia(Collections.synchronizedMap(new HashMap<Integer,Incidencia>(64)));
		setRecursos(Collections.synchronizedMap(new HashMap<Integer,Recurso>(64)));
		multicastConnection = MulticastConnection.class.getConstructor(List.class).newInstance(Arrays.asList(defaultMulticastConnectionSettings));
		windowSettings = new MainWindowSettings(defaultWindowSettings);
		logger = new Logger(defaultLoggerSettings);
		solver = (Solver)Class.forName((String)defaultSolver).getConstructor(List.class).newInstance(Arrays.asList(defaultSolverSettings));
		dao = (SQLConnector)Class.forName((String)defaultDao).getConstructor(List.class).newInstance(Arrays.asList(defaultDaoSettings));
		servletConnection = (Connection)Class.forName((String)defaultServletConnection).getConstructor(List.class).newInstance(Arrays.asList(defaultServletConnectionSettings));
		stationConnection = (Connection)Class.forName((String)defaultStationConnection).getConstructor(List.class).newInstance(Arrays.asList(defaultStationConnectionSettings));
	}
	
	public static void loadDefault() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		current = new Configuration();
	}
	
	//Deserializer para texto plano
	public static void loadFromFile() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		loadDefault();
		String line;
		String object=null;
		String clazz=null;
		String[] fields=null;
		String[] data=null;
		Scanner in = new Scanner(new FileInputStream(configFile));
		try {
			while (in.hasNextLine()) {
				line = in.nextLine();
				if(line.length()>0) {
					if(line.charAt(0)=='#') {
						if(line.equalsIgnoreCase("#Object#")) {
							object = in.nextLine();
							if(object.equals("")) object=null;
						}
						else if(line.equalsIgnoreCase("#Class#")) {
							clazz = in.nextLine();
							if(clazz.equals("")) clazz=null;
						}
						else if(line.equalsIgnoreCase("#Fields#")) {
							fields = in.nextLine().split("[#]");
							//if(fields.length<1 || fields[0].equals("")) fields=null;
						}
						else if(line.equalsIgnoreCase("#Data#")) {
							data = in.nextLine().split("[#]");
							//if(data.length<1 || data[0].equals("")) data=null;
						}
					}
					if(object!=null && clazz!=null && fields!=null && data!=null) {
						Object o = Class.forName(clazz).getConstructor(List.class).newInstance(Arrays.asList(data));
						switch(object) {
						case "windowSettings":
							current.setWindowSettings((MainWindowSettings)o);
							break;
						case "servletConnection":
							current.setServletConnection((Connection)o);
							break;
						case "stationConnection":
							current.setStationConnection((Connection)o);
							break;
						case "multicastConnection":
							current.setMulticastConnection((MulticastConnection)o);
							break;
						case "solver":
							current.setSolver((Solver)o);
							break;
						case "logger":
							current.setLogger((Logger)o);
							break;
						case "dao":
							current.setDao((SQLConnector)o);
							break;
						}
						object=null;
						fields=null;
						data=null;
					}
				}
				else {
					object=null;
					fields=null;
					data=null;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		in.close();
	}
	
	//Serializer para texto plano
	public static void saveToFile() throws FileNotFoundException, IOException {
		PrintStream out = new PrintStream(new FileOutputStream(configFile));
		try {
			out.println("##################################");
			for(Field f:Configuration.class.getDeclaredFields()) {
				if(f.getModifiers()<2 && f.get(current)!=null) {
					@SuppressWarnings("rawtypes")
					Class c = f.get(current).getClass();
					out.println("#Object#");
					out.println(f.getName());
					out.println("#Class#");
					out.println(c.getName());
					out.println("#Fields#");
					if(c.getSuperclass()==SQLConnector.class) c=SQLConnector.class;//Esta clase es especial
					boolean first = true;
					for(Field f2:c.getDeclaredFields()) {
						if(f2.getModifiers()==1) {
							if(!first) out.print("#");
							else first=false;
							out.print(f2.getName());
						}
					}
					out.println();
					out.println("#Data#");
					first = true;
					for(Field f2:c.getDeclaredFields()) {
						if(f2.getModifiers()==1) {
							if(!first) out.print("#");
							else first=false;
							out.print(f2.get(f.get(current)).toString());
						}
					}
					out.println();
					out.println("##################################");
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		out.close();
	}
	
	/*public static void loadFromFile() throws IOException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(configFile));
		current = (Configuration)in.readObject();
		in.close();
	}
	
	public static void saveToFile() throws FileNotFoundException, IOException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(configFile));
		out.writeObject(current);
		out.close();
	}*/

	public static Configuration getCurrent() {
		return current;
	}

	public Solver getSolver() {
		return solver;
	}

	public Logger getLogger() {
		return logger;
	}

	public SQLConnector getDao() {
		return dao;
	}
	
	public Connection getServletConnection() {
		return servletConnection;
	}

	public Connection getStationConnection() {
		return stationConnection;
	}
	
	public MainWindowSettings getWindowSettings() {
		return windowSettings;
	}
	
	public int[] getDefaultWindowSettings() {
		return defaultWindowSettings;
	}

	public MulticastConnection getMulticastConnection() {
		return multicastConnection;
	}

	public void setMulticastConnection(MulticastConnection multicastConnection) {
		this.multicastConnection = multicastConnection;
	}

	public void setSolver(Solver solver) {
		this.solver = solver;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public void setDao(SQLConnector dao) {
		this.dao = dao;
	}

	public void setServletConnection(Connection servletConnection) {
		this.servletConnection = servletConnection;
	}

	public void setStationConnection(Connection stationConnection) {
		this.stationConnection = stationConnection;
	}

	public void setWindowSettings(MainWindowSettings windowSettings) {
		this.windowSettings = windowSettings;
	}

	public Map<Integer,Recurso> getRecursos() {
		return recursos;
	}

	public void setRecursos(Map<Integer,Recurso> recursos) {
		this.recursos = recursos;
	}

	public Map<Integer,Incidencia> getRecursoIncidencia() {
		return recursoIncidencia;
	}

	public void setRecursoIncidencia(Map<Integer,Incidencia> recursoIncidencia) {
		this.recursoIncidencia = recursoIncidencia;
	}
	
}
