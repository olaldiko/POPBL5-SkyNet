package connections;

public class Message {
	
	public int id;
	public String tipo;
	public String msg;
	public MessageParser origin;
	
	public Message() {}
	
	public Message(int id, String tipo, String msg) {
		this.id = id;
		this.tipo = tipo;
		this.msg = msg;
	}
	
	public String toString() {
		return "ID: "+id+" | TIPO: "+tipo+" | MSG: "+msg;
	}
	
}
