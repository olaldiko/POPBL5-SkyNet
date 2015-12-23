package ui;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.WindowListener;
import java.io.Serializable;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import configuration.Configuration;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	public MainWindowSettings settings;
	
	private JTextArea output;
	
	public MainWindow(WindowListener w) {
		settings = Configuration.getCurrent().getWindowSettings();
		if(settings==null) settings = new MainWindowSettings(Configuration.getCurrent().getDefaultWindowSettings());
		Configuration.getCurrent().setWindowSettings(settings);
		
		output = new JTextArea(5, 20);
		output.setMargin(new Insets(5,5,5,5));
		output.setEditable(false);
		JScrollPane scroll = new JScrollPane(output);
		add(scroll);
		
		addWindowListener(w);
		setMinimumSize(new Dimension(520,330));
		setSize(settings.dimX, settings.dimY);
		setIconImage(new ImageIcon("./Skynet.jpg").getImage());
		setLocation(settings.posX, settings.posY);
		setTitle("Skynet");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void update(String s) {
		output.append(s);
	}
	
	public void saveSettings() {
		settings.posX = getLocation().x;
		settings.posY = getLocation().y;
		settings.dimX = getSize().width;
		settings.dimY = getSize().height;
		System.out.println(getSize().width+":"+getSize().height);
	}
	
	public class MainWindowSettings implements Serializable {
		private static final long serialVersionUID = 1L;
		public int posX;
		public int posY;
		public int dimX;
		public int dimY;
		public MainWindowSettings(int[] settings) {
			posX = settings[0];
			posY = settings[1];
			dimX = settings[2];
			dimY = settings[3];
		}
	}
	
}
