package co.edu.uptc.servicio;

public class CredencialesRecordadas {
    private String usuario;
    private String contrasena;

    public CredencialesRecordadas() {} // Constructor vacío necesario para Gson

    public CredencialesRecordadas(String usuario, String contrasena) {
        this.usuario = usuario;
        this.contrasena = contrasena;
    }

    // Getters y Setters públicos
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}
