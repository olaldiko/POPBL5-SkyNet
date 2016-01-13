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
 * Esta clase controlara la UI del recurso. Iniciaremos una primera pantalla con las opciones del recurso y después
 * lanzaremos la UI principal (esta clase)
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
	
	@Override
	public void run() {
		resetNav();
		addNavText("Esperando ruta...");
		loadURL();
		updateMap(Definitions.lat, Definitions.lng);
	}
	
	private Container createMainPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(createNorthPanel(), BorderLayout.NORTH);
		panel.add(mapView(), BorderLayout.CENTER);
		panel.add(createSouthPanel(), BorderLayout.SOUTH);
		return panel;
	}
	
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
		panel4.setBorder(BorderFactory.createTitledBorder("URL del Servidor"));
		lURL = new JLabel("--", SwingConstants.CENTER);
		panel4.add(lURL);
		JPanel panel5 = new JPanel(new BorderLayout());
		panel5.setBorder(BorderFactory.createTitledBorder("Nº de Socket"));
		lSocket = new JLabel("--", SwingConstants.CENTER);
		panel5.add(lSocket);
		panel.add(panel1);
		panel.add(panel2);
		panel.add(panel3);
		panel.add(panel4);
		panel.add(panel5);
		return panel;
	}
	
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
	
	private Component mapView() {
		panel = new JFXPanel();
		return panel;
	}
	
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
	
	private void check() {
		if (!loaded) {
			try {
				stop.acquire();
			} catch (InterruptedException e) {}
		}
	}
	
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
	
	public void addAlertText(String text) {
		alertArea.append("ALERTA: "+text+"\n");
	}
	
	public void addNavText(String text) {
		navArea.append("NAVEGADOR: "+text+"\n");
	}
	
	public void clearNavText() {
		navArea.setText("");
	}
	
	public void setID(int id) {
		lID.setText(String.valueOf(id));
	}
	
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
