package frontend;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import data.Definitions;
import utils.ConfigFile;

/**
 * Esta clase controlara la UI de inicio del programa, que modifica las variables de inicio del recurso.
 * 
 * @author Skynet Team
 *
 */
public class OptionsUI implements ActionListener {
	
	JFrame window;
	JPanel mainPanel, optionsPanel;
	JTextField serverURL, socketNumber;
	JCheckBox check;
	
	ConfigFile file;
	
	Semaphore stop;
	
	public OptionsUI(Semaphore stop) {
		this.stop = stop;
		window = new JFrame();
		window.setTitle("Inicializacion de Recurso");
		window.setSize(400, 150);
		window.getContentPane().add(createMainPanel());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initData();
	}
	
	public void start() {
		window.setVisible(true);
	}
	
	private void initData() {
		file = new ConfigFile();
		if (file.check()) {
			serverURL.setText(Definitions.socketAddres);
			socketNumber.setText(String.valueOf(Definitions.socketNumber));
		}
	}
	
	private Container createMainPanel() {
		mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(createTitle(), BorderLayout.NORTH);
		mainPanel.add(createOptionsPanel(), BorderLayout.CENTER);
		mainPanel.add(createButton(), BorderLayout.SOUTH);
		return mainPanel;
	}
	
	private Container createOptionsPanel() {
		optionsPanel = new JPanel(new GridLayout(3, 2, 5, 5));
		optionsPanel.add(new JLabel("URL del servidor:", SwingConstants.RIGHT));
		optionsPanel.add(serverURL = new JTextField());
		optionsPanel.add(new JLabel("Socket del servidor:", SwingConstants.RIGHT));
		optionsPanel.add(socketNumber = new JTextField());
		optionsPanel.add(new JLabel("Habilitar monitorización:", SwingConstants.RIGHT));
		optionsPanel.add(check = new JCheckBox());
		return optionsPanel;
	}
	
	private Component createTitle() {
		JLabel title =  new JLabel("Opciones iniciales del Recurso", SwingConstants.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 20));
		return title;
	}
	
	private Component createButton() {
		JButton button = new JButton();
		button.setText("OK");
		button.setActionCommand("save");
		button.addActionListener(this);
		return button;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("save")) {
			Definitions.socketAddres = serverURL.getText();
			Definitions.socketNumber = Integer.parseInt(socketNumber.getText());
			Definitions.debugging = check.isSelected();
			file.writeAll();
			window.dispose();
			stop.release();
		}
	}

}
