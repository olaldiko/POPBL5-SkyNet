package connections;

public class Message {
	
	public int id;
	public String tipo;
	public String msg;
	public int cont;
	
	public Message() {}
	
	public Message(int id, String tipo, String msg, int cont) {
		this.id = id;
		this.tipo = tipo;
		this.msg = msg;
		this.cont = cont;
	}
	
}
