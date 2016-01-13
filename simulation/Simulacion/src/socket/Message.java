package socket;

/**
 * Message sera la clase con la que se crearán los objetos para interpretar los datos que llegan mediante sockets.
 * 
 * @author Skynet Team
 *
 */
public class Message {
	
	private int id;
	private String type, data;
	
	public Message(int id, String type, String data) {
		this.id = id;
		this.type = type;
		this.data = data;
	}
	
	public String getType() {
		return type;
	}

	public String getData() {
		return data;
	}

	public int getID() {
		return id;
	}

	public String toSocketFormat() {
		return (((char) 2)+String.valueOf(id)+((char) 29)+type+((char) 29)+data+((char) 3));
	}
	
	@Override
	public String toString() {
		return "<STX>"+id+"<GS>"+type+"<GS>"+data+"<ETX>";
	}
	
}
