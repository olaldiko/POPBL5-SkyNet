package ia;

//Lock that can be unlocked from any thread

public class MonoSemaphore {

	private volatile boolean locked;
	
	public MonoSemaphore() {
		locked = true;
	}
	
	public synchronized void acquire() {
		try {while(locked) wait();} catch(Exception e) {}
		locked = true;
	}
	
	public synchronized void release() {
		locked = false;
		notify();
	}
	
}
