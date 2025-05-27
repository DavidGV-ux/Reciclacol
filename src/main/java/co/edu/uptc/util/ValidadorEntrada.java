package co.edu.uptc.util;

public class ValidadorEntrada {
    public static boolean validarNombre(String nombre) {
        return nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+");
    }

    public static boolean validarIdentificacion(String identificacion) {
        return identificacion.matches("\\d+");
    }

    public static boolean validarCorreo(String correo) {
        return correo.matches(".+@.+");
    }

    public static boolean validarTelefono(String telefono) {
        return telefono != null && telefono.matches("\\d+");
    }
    
}

