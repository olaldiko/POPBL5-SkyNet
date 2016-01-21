package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import configuration.Configuration;
import configuration.Logger;
import connections.MulticastConnection;

public class MainWindow extends JFrame implements KeyListener, ActionListener {

	private static final long serialVersionUID = 1L;

	public MainWindowSettings settings;
	
	private JTextArea output;
	private JTextField txtAlert;
	private JButton btnAlert;
	private MulticastConnection multicastConnection;
	
	public MainWindow(WindowListener w) {
		settings = Configuration.getCurrent().getWindowSettings();
		if(settings==null) settings = new MainWindowSettings(Configuration.getCurrent().getDefaultWindowSettings());
		Configuration.getCurrent().setWindowSettings(settings);
		multicastConnection = Configuration.getCurrent().getMulticastConnection();
		
		output = new JTextArea(5, 20);
		output.setMargin(new Insets(5,5,5,5));
		output.setEditable(false);
		JScrollPane scroll = new JScrollPane(output);
		add(scroll);
		
		JPanel panelSur = new JPanel(new BorderLayout());
		txtAlert = new JTextField();
		btnAlert = new JButton("Alert");
		panelSur.add(txtAlert,BorderLayout.CENTER);
		panelSur.add(btnAlert,BorderLayout.EAST);
		add(panelSur,BorderLayout.SOUTH);
		txtAlert.addKeyListener(this);
		btnAlert.addActionListener(this);
		
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
	}
	
	public void sendAlert() {
		try {
			multicastConnection.sendAlert(txtAlert.getText());
			txtAlert.setText("");
		} catch (Exception e) {
			Configuration.getCurrent().getLogger().log("Exception sending alert "+e.getClass()+": "+e.getMessage(),Logger.ERROR);
		}
	}

	public void keyPressed(KeyEvent k) {
		if(k.getKeyChar()=='\n') {
			sendAlert();
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		sendAlert();
	}
	
	public void keyReleased(KeyEvent arg0) {}
	public void keyTyped(KeyEvent arg0) {}
	
}
