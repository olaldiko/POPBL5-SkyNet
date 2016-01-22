package frontend;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.io.File;
import java.util.concurrent.Semaphore;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import data.Definitions;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * MainUI
 * 
 * This class will manage the main UI interface. This UI will be initialize by the "Resoruce" class.
 *  
 * @author Skynet Team
 *
 */
public class MainUI extends Thread {
	
	private JFrame window;
	private JTextArea alertArea;
	private JTextArea navArea;
	private JScrollPane scrollAlert;
	private JScrollPane scrollNav;
	
	private JLabel lID, lEstado, lLat, lLng, lURL, lSocket;
	private JLabel lDistanciaTotal, lDistanciaRest, lDuracionTotal, lDuracionRest;
	
	private WebEngine webEngine;
	private JFXPanel panel;
	
	private Semaphore stop;
	private Semaphore candado;
	private boolean loaded = false;
	
	private String mapURL = "file:///"+new File("map.html").getAbsolutePath();
	
	/** 
	 * Constructor creates the Main Frame and creates the needed semaphores.
	 * Additionally, the method creates the Scene (The JavaFX panel that contains the WebView)
	 */
	public MainUI() {
		window = new JFrame();
		window.setTitle("Panel de control de Recurso");
		window.setSize(300, 200);
		window.getContentPane().add(createMainPanel());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setExtendedState(JFrame.MAXIMIZED_BOTH);
		window.setVisible(true);
		createScene();
		stop = new Semaphore(0);
		candado = new Semaphore(1);
	}
	
	/**
	 * At start, the UI sets the JLabels and loads the map in the WebView.
	 */
	@Override
	public void run() {
		resetNav();
		addNavText("Esperando ruta...");
		loadURL();
		updateMap(Definitions.lat, Definitions.lng);
	}
	
	/**
	 * Main panel is a BorderLayout panel with another 3 panels located in the north, center and south of the UI.
	 * 
	 * @return The main panel of the MainUI
	 */
	private Container createMainPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(createNorthPanel(), BorderLayout.NORTH);
		panel.add(mapView(), BorderLayout.CENTER);
		panel.add(createSouthPanel(), BorderLayout.SOUTH);
		return panel;
	}
	
	/**
	 * The north panel creates all the JLabels to show the information to the user.
	 * 
	 * @return The north panel of the MainUI
	 */
	private Container createNorthPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 5, 0, 0));
		JPanel panel1 = new JPanel(new BorderLayout());
		panel1.setBorder(BorderFactory.createTitledBorder("ID del Recurso"));
		lID = new JLabel("--", SwingConstants.CENTER);
		panel1.add(lID);
		JPanel panel2 = new JPanel(new BorderLayout());
		panel2.setBorder(BorderFactory.createTitledBorder("Estado del Recurso"));
		lEstado = new JLabel("--", SwingConstants.CENTER);
		panel2.add(lEstado);
		JPanel panel3 = new JPanel(new BorderLayout());
		panel3.setBorder(BorderFactory.createTitledBorder("Localizacion del Recurso"));
		lLat = new JLabel("--", SwingConstants.CENTER);
		panel3.add(lLat, BorderLayout.NORTH);
		lLng = new JLabel("--", SwingConstants.CENTER);
		panel3.add(lLng, BorderLayout.CENTER);
		JPanel panel4 = new JPanel(new BorderLayout());
		panel4.setBorder(BorderFactory.createTitledBorder("IP del Servidor"));
		lURL = new JLabel("--", SwingConstants.CENTER);
		panel4.add(lURL);
		JPanel panel5 = new JPanel(new BorderLayout());
		panel5.setBorder(BorderFactory.createTitledBorder("Nº de Puerto"));
		lSocket = new JLabel("--", SwingConstants.CENTER);
		panel5.add(lSocket);
		panel.add(panel1);
		panel.add(panel2);
		panel.add(panel3);
		panel.add(panel4);
		panel.add(panel5);
		return panel;
	}
	
	/**
	 * South panel fills the panel (GridLayout) with a combination of panels to show alert and route information.
	 * 
	 * @return The south panel of the MainUI
	 */
	private Container createSouthPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 2, 0, 0));
		JPanel panel1 = new JPanel(new BorderLayout());
		panel1.setBorder(BorderFactory.createTitledBorder("Mensajes de alerta"));
		alertArea = new JTextArea(12, 20);
		scrollAlert = new JScrollPane(alertArea); 
		alertArea.setEditable(false);
		panel1.add(scrollAlert);
		JPanel panel2 = new JPanel(new BorderLayout());
		panel2.setBorder(BorderFactory.createTitledBorder("Navegador"));
		navArea = new JTextArea(12, 20);
		scrollNav = new JScrollPane(navArea); 
		navArea.setEditable(false);
		JPanel panel21 = new JPanel(new GridLayout(1, 4, 0, 0));
		JPanel panel211 = new JPanel(new BorderLayout());
		JPanel panel212 = new JPanel(new BorderLayout());
		JPanel panel213 = new JPanel(new BorderLayout());
		JPanel panel214 = new JPanel(new BorderLayout());
		panel211.setBorder(BorderFactory.createTitledBorder("Distancia total"));
		panel211.add(lDistanciaTotal = new JLabel("--", SwingConstants.CENTER));
		panel212.setBorder(BorderFactory.createTitledBorder("Distancia restante"));
		panel212.add(lDistanciaRest = new JLabel("--", SwingConstants.CENTER));
		panel213.setBorder(BorderFactory.createTitledBorder("Duracion total"));
		panel213.add(lDuracionTotal = new JLabel("--", SwingConstants.CENTER));
		panel214.setBorder(BorderFactory.createTitledBorder("Duracion restante"));
		panel214.add(lDuracionRest = new JLabel("--", SwingConstants.CENTER));
		panel21.add(panel211);
		panel21.add(panel212);
		panel21.add(panel213);
		panel21.add(panel214);
		panel2.add(panel21, BorderLayout.NORTH);
		panel2.add(scrollNav);
		panel.add(panel1);
		panel.add(panel2);
		return panel;
	}
	
	/**
	 * This method creates the JFXPanel to show the WebView.
	 * 
	 * @return JFXPanel for the JavaFX elements.
	 */
	private Component mapView() {
		panel = new JFXPanel();
		return panel;
	}
	
	/**
	 * Creating the Scene means to create and set the WebView into the JFXPanel.
	 */
	private void createScene() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				WebView webView = new WebView();
				webEngine = webView.getEngine();
				panel.setScene(new Scene(webView));
			}
			
		});
	}
	
	/**
	 * This method reloads the map of the WebView.
	 */
	public void reload() {
		try {
			candado.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		loaded = false;
		candado.release();
		loadURL();
	}
	
	/**
	 * This method loads the map in the WebView. To avoid problems with the Google Maps API of JavaScript,
	 * the method uses a listener to unlock a Semaphore when the page is 100% loaded.
	 */
	private void loadURL() {
		try {
			candado.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				webEngine.load(mapURL);
				webEngine.getLoadWorker().progressProperty().addListener(new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
						if (arg2.equals(1.0)) {
							stop.release();
							loaded = true;
						}
					}
				});
			}
			
		});
		candado.release();
	}
	
	/**
	 * The thread that need to interact with the WebView will check if the HTML is completely loaded with this class.
	 */
	private void check() {
		if (!loaded) {
			try {
				stop.acquire();
			} catch (InterruptedException e) {}
		}
	}
	
	/**
	 * This method interacts with the HTML web page and adds a marker using a JavaScript function in the HTML file.
	 * The funtion sends the command to create the marker to the JavaFX thread.
	 * 
	 * @param lat Double latitude of the marker.
	 * @param lng Double longitude of the marker.
	 */
	public void addPointer(double lat, double lng) {
		try {
			candado.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		check();
		Platform.runLater(() -> webEngine.executeScript("createMarker("+lat+", "+lng+");"));
		candado.release();
	}
	
	/**
	 * This method interacts with the HTML web page and updates a existing marker using a JavaScript function in the
	 * HTML file. The funtion sends the command to update the marker to the JavaFX thread.
	 * 
	 * @param lat Double latitude of the marker.
	 * @param lng Double longitude of the marker.
	 */
	public void updatePointer(double lat, double lng) {
		try {
			candado.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (loaded) {
			Platform.runLater(() -> webEngine.executeScript("changePositionMarker("+lat+", "+lng+");"));
		}
		candado.release();
	}
	
	/**
	 * This method interacts with the HTML web page and moves the mark to a position using a JavaScript function in
	 * the HTML file. The funtion sends the command to move the map to the JavaFX thread.
	 * 
	 * @param lat Double latitude of the marker.
	 * @param lng Double longitude of the marker.
	 */
	public void updateMap(double lat, double lng) {
		try {
			candado.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		check();
		Platform.runLater(() -> webEngine.executeScript("updateMap("+lat+", "+lng+");"));
		candado.release();
	}
	
	/**
	 * This method adds a text line to the alert text area.
	 * 
	 * @param text String text to write in the text area.
	 */
	public void addAlertText(String text) {
		alertArea.append("ALERTA: "+text+"\n");
	}
	
	/**
	 * This method adds a text line to the navigation text area.
	 * 
	 * @param text String text to write in the text area.
	 */
	public void addNavText(String text) {
		navArea.append("NAVEGADOR: "+text+"\n");
	}
	
	/**
	 * This method cclears the navigation text area.
	 */
	public void clearNavText() {
		navArea.setText("");
	}
	
	public void setID(int id) {
		lID.setText(String.valueOf(id));
	}
	
	/**
	 * Sets the state JLabel depending on the state number.
	 * 
	 * @param estado Integer state number.
	 */
	public void setEstado(int estado) {
		if (estado == 0) {
			lEstado.setText("LIBRE");
		} else if (estado == 1) {
			lEstado.setText("VIAJE IDA");
		} else if (estado == 2) {
			lEstado.setText("VIAJE VUELTA");
		}
	}
	
	public void setLocation(double lat, double lng) {
		lLat.setText("LAT: "+lat);
		lLng.setText("LNG: "+lng);
	}
	
	public void setURL(String url) {
		lURL.setText(url);
	}
	
	public void setSocket(int socket) {
		lSocket.setText(String.valueOf(socket));
	}

	/**
	 * Resets all the JLabels at the north panel.
	 */
	public void resetNav() {
		lDuracionTotal.setText("--");
		lDuracionRest.setText("--");
		lDistanciaTotal.setText("--");
		lDistanciaRest.setText("--");
	}
	
	public void setDuracionTotal(String text) {
		lDuracionTotal.setText(text);
	}
	
	public void setDuracionRest(String text) {
		lDuracionRest.setText(text);
	}
	
	public void setDistanciaTotal(String text) {
		lDistanciaTotal.setText(text);
	}
	
	public void setDistanciaRest(String text) {
		lDistanciaRest.setText(text);
	}
	
}
