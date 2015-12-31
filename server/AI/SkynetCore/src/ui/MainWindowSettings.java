package ui;

import java.io.Serializable;
import java.util.List;

public class MainWindowSettings implements Serializable {
	private static final long serialVersionUID = 1L;
	public int posX;
	public int posY;
	public int dimX;
	public int dimY;
	public MainWindowSettings(List<String> settings) {
		posX = Integer.valueOf(settings.get(0));
		posY = Integer.valueOf(settings.get(1));
		dimX = Integer.valueOf(settings.get(2));
		dimY = Integer.valueOf(settings.get(3));
	}
	public MainWindowSettings(int[] settings) {
		posX = settings[0];
		posY = settings[1];
		dimX = settings[2];
		dimY = settings[3];
	}
}
