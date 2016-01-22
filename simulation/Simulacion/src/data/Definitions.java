package data;

/**
 * Definitions
 * 
 * This class is used to share global variables between different classes.
 * 
 * @author Skynet Team
 *
 */
public class Definitions {
	
	public static boolean debugging = true;
	
	public static String configFile = "conf.dat";
	
	public static int socketNumber;
	public static String socketAddres;
	
	public static int id = -1;
	public static int estado = 0;
	
	public static double lat = 43.063081;
	public static double lng = -2.505862;
	
	public static int multicastPort = 8000;
	public static String multicastGroup;

}
