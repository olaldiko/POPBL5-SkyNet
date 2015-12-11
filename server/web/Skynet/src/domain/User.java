package domain;

public class User {
	
	private String usuarioId;
	private String privilegios;
	private String nombre;
	private String apellido;
	private String direccion;
	private String telefono;
	private String DNI;
	private String notas;
	private String username;
	private String password;
	
	public User() {}
	
	public User(String usuarioId, String privilegios, String nombre, String apellido, String direccion, 
			String telefono, String DNI, String notas, String username, String password) {
		this.usuarioId = usuarioId;
		this.privilegios = privilegios;
		this.nombre = nombre;
		this.apellido = apellido;
		this.direccion = direccion;
		this.telefono = telefono;
		this.DNI = DNI;
		this.notas = notas;
		this.username = username;
		this.password = password;
	}
	
	public User(QueryResult r, int index) {
		this.usuarioId = r.getResult(index, "usuarioId");
		this.privilegios = r.getResult(index, "privilegios");
		this.nombre = r.getResult(index, "nombre");
		this.apellido = r.getResult(index, "apellido");
		this.direccion = r.getResult(index, "direccion");
		this.telefono = r.getResult(index, "telefono");
		this.DNI = r.getResult(index, "DNI");
		this.notas = r.getResult(index, "notas");
		this.username = r.getResult(index, "username");
		this.password = r.getResult(index, "password");
	}
	
	public String getUsuarioId() {
		return usuarioId;
	}
	public void setUsuarioId(String usuarioId) {
		this.usuarioId = usuarioId;
	}
	public String getPrivilegios() {
		return privilegios;
	}
	public void setPrivilegios(String privilegios) {
		this.privilegios = privilegios;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getDNI() {
		return DNI;
	}
	public void setDNI(String dNI) {
		DNI = dNI;
	}
	public String getNotas() {
		return notas;
	}
	public void setNotas(String notas) {
		this.notas = notas;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
