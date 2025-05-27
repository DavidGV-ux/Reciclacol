package co.edu.uptc.modelo;

import java.util.ArrayList;
import java.util.List;

public class Usuario {
	private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private String identificacion;
    private String contrasena;
    private String direccion;
    private String correo;
	private int telefono;
    private List<Residuo> residuos;
    private int puntosEcologicos;

	public Usuario(String primerNombre, String segundoNombre, String primerApellido, String segundoApellido,
	String identificacion, String direccion, String correo, String contrasena, int telefono) {
this.primerNombre = primerNombre;
this.segundoNombre = segundoNombre;
this.primerApellido = primerApellido;
this.segundoApellido = segundoApellido;
this.identificacion = identificacion;
this.contrasena = contrasena;
this.direccion = direccion;
this.correo = correo;
this.residuos = new ArrayList<>();
this.puntosEcologicos = 0;
this.telefono = telefono;

}

	public String getPrimerNombre() {
		return primerNombre;
	}

	public void setPrimerNombre(String primerNombre) {
		this.primerNombre = primerNombre;
	}

	public String getSegundoNombre() {
		return segundoNombre;
	}

	public void setSegundoNombre(String segundoNombre) {
		this.segundoNombre = segundoNombre;
	}

	public String getPrimerApellido() {
		return primerApellido;
	}

	public void setPrimerApellido(String primerApellido) {
		this.primerApellido = primerApellido;
	}

	public String getSegundoApellido() {
		return segundoApellido;
	}

	public void setSegundoApellido(String segundoApellido) {
		this.segundoApellido = segundoApellido;
	}

	public String getIdentificacion() {
		return identificacion;
	}

	public void setIdentificacion(String identificacion) {
		this.identificacion = identificacion;
	}

	public String getContrasena() {
		return contrasena;
	}

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public List<Residuo> getResiduos() {
		return residuos;
	}

	public void setResiduos(List<Residuo> residuos) {
		this.residuos = residuos;
	}

	public int getPuntosEcologicos() {
		return puntosEcologicos;
	}

	public void setPuntosEcologicos(int puntosEcologicos) {
		this.puntosEcologicos = puntosEcologicos;
	}

	

public String getNombreCompleto() {
    StringBuilder sb = new StringBuilder();
    sb.append(primerNombre);
    if (segundoNombre != null && !segundoNombre.trim().isEmpty()) sb.append(" ").append(segundoNombre);
    sb.append(" ").append(primerApellido);
    if (segundoApellido != null && !segundoApellido.trim().isEmpty()) sb.append(" ").append(segundoApellido);
    return sb.toString();
}

public void agregarResiduo(Residuo residuo) {
	residuos.add(residuo);
	calcularPuntos();
}

private void calcularPuntos() {
	this.puntosEcologicos = 0;
	for (Residuo r : residuos) {
		this.puntosEcologicos += (int) (r.getPeso() * 10); // 10 puntos por Kg
	}
}

public int getTelefono() {
	return telefono;
}

public void setTelefono(int telefono) {
	this.telefono = telefono;
}

}