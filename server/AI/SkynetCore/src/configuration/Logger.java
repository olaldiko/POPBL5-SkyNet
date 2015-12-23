package configuration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import ui.MainWindow;

public class Logger implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final int DEBUG = 0;
	public static final int INFO = 1;
	public static final int WARN = 2;
	public static final int ERROR = 3;
	public static final int FATAL = 4;
	public static final String[] logLevels = {"[DEBUG]","[INFO]","[WARN]","[ERROR]","[FATAL]"};
	
	String logPath;
	int minLogLevel;
	long oldFileLimit;
	
	transient MainWindow window;
	transient FileWriter out;

	public Logger(Object[] settings) {
		logPath = (String) settings[0];
		minLogLevel = (Integer) settings[1];
		oldFileLimit = (Long) settings[2];
	}
	
	public void init(MainWindow w) throws IOException {
		window = w;
		File dir = new File(logPath);
		deleteOldFiles(dir);
		if(!dir.exists()) dir.mkdir();
		out = new FileWriter(logPath+"Skynet"+System.currentTimeMillis()+".log");
	}
	
	public void deleteOldFiles(File dir) {
		if(dir.isDirectory()) {
			for(File f:dir.listFiles()) deleteOldFiles(f);
		}
		else if(System.currentTimeMillis()>dir.lastModified()+oldFileLimit) dir.delete();
	}
	
	public void log(String msg) {
		log(msg,1);
	}
	
	public void log(String msg, int logLevel) {
		msg = getDatetime() + " " + logLevels[logLevel] + " -> " + msg + "\n";
		if(window!=null) window.update(msg);
		try {
			out.write(msg);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getDatetime() {
		return (LocalDate.now().toString()+" "+hour()+":"+minute());
	}
	
	public String hour() {
		String hour;
		hour = String.valueOf(LocalTime.now().getHour());
		if(hour.length()<2) hour = "0"+hour;
		return hour;
	}
	
	public String minute() {
		String minute;
		minute = String.valueOf(LocalTime.now().getMinute());
		if(minute.length()<2) minute = "0"+minute;
		return minute;
	}

}
