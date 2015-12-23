package configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import ia.*;
import connections.*;
import database.*;
import ui.*;

public class Configuration implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String configFile = "./SkynetConfig.obj";
	
	private static Configuration current;
	
	/////////////////////SOLVER/////////////////////
	private static final int[] defaultWindowSettings = {400,150,520,330};
	private MainWindow.MainWindowSettings windowSettings;
	/////////////////////SOLVER/////////////////////
	
	/////////////////////CONNECTIONS/////////////////////
	private static final String defaultServletConnection = "connections.TCPConnection";
	private static final Object[] defaultServletConnectionSettings = {"127.0.0.1",6969};
	private Connection servletConnection;
	
	private static final String defaultStationConnection = "connections.TCPConnection";
	private static final Object[] defaultStationConnectionSettings = {"127.0.0.1",9696};
	private Connection stationConnection;
	/////////////////////CONNECTIONS/////////////////////

	/////////////////////SOLVER/////////////////////
	private static final String defaultSolver = "ia.RandomSolver";
	private static final Object[] defaultSolverSettings = {};
	private Solver solver;
	/////////////////////SOLVER/////////////////////
	
	/////////////////////LOGGER/////////////////////
	private static final Object[] defaultLoggerSettings = {"./logs/",0,86400000L};
	private Logger logger;
	/////////////////////LOGGER/////////////////////
	
	/////////////////////DATABASE/////////////////////
	private static final String defaultDao = "database.PostgreSQLConnector";
	private static final Object[] defaultDaoSettings = {"localhost:5432","Skynet","postgres", "dotty2048"};
	private SQLConnector dao;
	/////////////////////DATABASE/////////////////////
	
	public Configuration() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		logger = new Logger(defaultLoggerSettings);
		solver = (Solver)Class.forName((String)defaultSolver).getConstructor(List.class).newInstance(Arrays.asList(defaultSolverSettings));
		dao = (SQLConnector)Class.forName((String)defaultDao).getConstructor(List.class).newInstance(Arrays.asList(defaultDaoSettings));
		servletConnection = (Connection)Class.forName((String)defaultServletConnection).getConstructor(List.class).newInstance(Arrays.asList(defaultServletConnectionSettings));
		stationConnection = (Connection)Class.forName((String)defaultStationConnection).getConstructor(List.class).newInstance(Arrays.asList(defaultStationConnectionSettings));
	}
	
	public static void loadDefault() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		current = new Configuration();
	}
	
	public static void loadFromFile() throws IOException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(configFile));
		current = (Configuration)in.readObject();
		in.close();
	}
	
	public static void saveToFile() throws FileNotFoundException, IOException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(configFile));
		out.writeObject(current);
		out.close();
	}

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
	
	public MainWindow.MainWindowSettings getWindowSettings() {
		return windowSettings;
	}
	
	public int[] getDefaultWindowSettings() {
		return defaultWindowSettings;
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

	public void setWindowSettings(MainWindow.MainWindowSettings windowSettings) {
		this.windowSettings = windowSettings;
	}
	
}
