package socket;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * Buzon es la clase que utilizaremos para almacenar generalmente Message-s para comunicar diferentes hilos
 * del programa.
 * 
 * @author Skynet Team
 *
 * @param <T>
 */
public class Buzon<T> {
		
	private ArrayList<T> buzon;
	private Semaphore lleno, vacio;
	private Semaphore candado, contador;
	
	public Buzon(int capacidad) {
		buzon = new ArrayList<>();
		lleno = new Semaphore(capacidad);
		candado = new Semaphore(1);
		contador = new Semaphore(0);
		vacio = new Semaphore(0);
	}
			
	public void send(T msg) throws InterruptedException {
		lleno.acquire();
		candado.acquire();
		buzon.add(msg);
		candado.release();
		contador.release();
		vacio.release();
	}
	
	public void send() throws InterruptedException {
		lleno.acquire();
		candado.acquire();
		buzon.add(null);
		candado.release();
		vacio.release();
	}
	
	public T receive() throws InterruptedException {
		T valor;
		vacio.acquire();
		candado.acquire();
		valor = buzon.remove(0);
		candado.release();
		lleno.release();
		return valor;
	}
	
	/**
	 * Este metodo se utiliza para encontrar Message-s con un type concreto.
	 * 
	 * @param type
	 * @return
	 * @throws InterruptedException
	 */
	public Message receive(String type) throws InterruptedException {
		Message valor = null;
		vacio.acquire();
		int index = -1;
		while (index == -1) {
			contador.acquire();
			candado.acquire();
			for (int i = 0; i < buzon.size(); i++) {
				if (type.equals(((Message) buzon.get(i)).getType())) {
					index = i;
				}
			}
			if (index != -1) valor = (Message) buzon.remove(index);
			candado.release();
		}
		lleno.release();
		return valor;
	}
	
	public int getSize() {
		return buzon.size();
	}
	
}
