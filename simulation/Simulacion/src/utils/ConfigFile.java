package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import data.Definitions;

/**
 * ConfigFile
 * 
 * This class reads and writes the config file.
 * 
 * @author Skynet Team
 *
 */
public class ConfigFile {
	
	public boolean check() {
		File conf = new File(Definitions.configFile);
		return conf.exists();
	}
	
	public void writeAll() {
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(Definitions.configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		try {
			bufferedWriter.write("socketNumber="+Definitions.socketNumber);
			bufferedWriter.newLine();
			bufferedWriter.write("serverURL="+Definitions.socketAddres);
			bufferedWriter.newLine();
			bufferedWriter.write("ID="+Definitions.id);
			bufferedWriter.newLine();
			bufferedWriter.write("ESTADO="+Definitions.estado);
			bufferedWriter.newLine();
			bufferedWriter.write("LAT="+Definitions.lat);
			bufferedWriter.newLine();
			bufferedWriter.write("LNG="+Definitions.lng);
			bufferedWriter.newLine();
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String readAll() {
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(Definitions.configFile);
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: El archivo no existe, utiliza el metodo 'write' para escribirlo antes.");
			e.printStackTrace();
		}
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line = null;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				String [] data = line.split("=");
				switch (data[0]) {
					case "socketNumber": Definitions.socketNumber = Integer.parseInt(data[1]);
						break;
					case "serverURL": Definitions.socketAddres = data[1]; 
						break;
					case "ID": Definitions.id = Integer.parseInt(data[1]); 
						break;
					case "ESTADO": Definitions.estado = Integer.parseInt(data[1]); 
						break;
					case "LAT": Definitions.lat = Double.parseDouble(data[1]); 
						break;
					case "LNG": Definitions.lng = Double.parseDouble(data[1]); 
						break;
					default: break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} return null;
	}
	
}
