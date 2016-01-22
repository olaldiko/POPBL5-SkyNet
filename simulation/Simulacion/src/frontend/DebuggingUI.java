package frontend;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import data.Definitions;

/**
 * DebuggingUI
 * 
 * DebuggingUI creates a JTextArea and offers a method to the threads to write log messages into the area.
 * 
 * @author Skynet Team
 *
 */
public class DebuggingUI {
	
	JFrame window;
	JPanel panel;
	JTextArea area;
	JScrollPane scroll;
	
	/**
	 * The constructor creates the window with a title.
	 * 
	 * @param title String to be set in the title.
	 */
	public DebuggingUI(String title) {
		window = new JFrame();
		window.setTitle("Panel de control de hilo "+title);
        window.setSize(600, 200);
        window.getContentPane().add(createMainPanel());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(Definitions.debugging);
	}
	
	private Container createMainPanel() {
		panel = new JPanel(new BorderLayout());
		panel.add(textArea(), BorderLayout.CENTER);
		return panel;
	}
	
	private Component textArea() {
		area = new JTextArea(12, 20);
		JScrollPane scrollPane = new JScrollPane(area); 
		area.setEditable(false);
		return scrollPane;
	}
	
	/**
	 * This method prints the message in the text area and adds a "\n" to create a new line.
	 * 
	 * @param text String to be printed in the text area.
	 */
	public void println(String text) {
		area.append(text+"\n");
	}
	
	/**
	 * This methods adds to the area text without creating a new line.
	 * 
	 * @param text String to be printed in the text area.
	 */
	public void print(String text) {
		area.append(text);
	}

}
