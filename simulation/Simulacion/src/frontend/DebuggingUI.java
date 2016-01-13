package frontend;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import data.Definitions;

public class DebuggingUI {
	
	JFrame window;
	JPanel panel;
	JTextArea area;
	JScrollPane scroll;
	
	public DebuggingUI(String title) {
		window = new JFrame();
		window.setTitle("Panel de control de hilo "+title);
        window.setSize(300, 200);
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
	
	public void println(String text) {
		area.append(text+"\n");
	}
	
	public void print(String text) {
		area.append(text);
	}

}
