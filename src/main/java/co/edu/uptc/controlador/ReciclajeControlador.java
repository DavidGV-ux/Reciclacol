package co.edu.uptc.controlador;

import java.sql.Date;

import co.edu.uptc.modelo.Residuo;
import co.edu.uptc.modelo.Usuario;
import co.edu.uptc.servicio.ReciclajeServicio;
import co.edu.uptc.util.ValidadorEntrada;
import co.edu.uptc.util.ValidadorResiduo;

/**
 * Controlador principal de la lógica de negocio para la aplicación de reciclaje.
 * Gestiona usuarios, residuos y la interacción con el servicio de persistencia.
 */
public class ReciclajeControlador {
    private ReciclajeServicio servicio;
    private Usuario usuarioActual;

    /**
     * Crea un nuevo controlador de reciclaje con el servicio especificado.
     * @param servicio Servicio de reciclaje para persistencia y lógica de negocio.
     */
    public ReciclajeControlador(ReciclajeServicio servicio) {
        this.servicio = servicio;
    }

    /**
     * Agrega un nuevo usuario al sistema si los datos son válidos y no existe ya el usuario.
     * @param primerNombre Primer nombre del usuario.
     * @param segundoNombre Segundo nombre del usuario.
     * @param primerApellido Primer apellido del usuario.
     * @param segundoApellido Segundo apellido del usuario.
     * @param identificacion Identificación única del usuario.
     * @param direccion Dirección del usuario.
     * @param correo Correo electrónico del usuario.
     * @param contrasena Contraseña del usuario.
     * @param telefono Número de teléfono del usuario.
     * @return true si el usuario fue agregado exitosamente, false en caso contrario.
     */
    public boolean agregarUsuario(String primerNombre, String segundoNombre, String primerApellido, String segundoApellido,
                                 String identificacion, String direccion, String correo, String contrasena, int telefono) {
        if (!ValidadorEntrada.validarNombre(primerNombre)) return false;
        if (!ValidadorEntrada.validarNombre(primerApellido)) return false;
        if (!ValidadorEntrada.validarIdentificacion(identificacion)) return false;
        if (!ValidadorEntrada.validarCorreo(correo)) return false;
        if (servicio.obtenerUsuario(identificacion) == null) {
            servicio.registrarUsuario(new Usuario(primerNombre, segundoNombre, primerApellido, segundoApellido,
                                                  identificacion, direccion, correo, contrasena, telefono));
            return true;
        }
        return false;
    }

    /**
     * Obtiene un usuario por su identificación.
     * @param identificacion Identificación del usuario.
     * @return Usuario correspondiente o null si no existe.
     */
    public Usuario obtenerUsuario(String identificacion) {
        return servicio.obtenerUsuario(identificacion);
    }

    /**
     * Obtiene el usuario actualmente autenticado en la sesión.
     * @return Usuario actual o null si no hay sesión activa.
     */
    public Usuario obtenerUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Intenta iniciar sesión con la identificación y contraseña proporcionadas.
     * @param identificacion Identificación del usuario.
     * @param contrasena Contraseña del usuario.
     * @return true si el inicio de sesión es exitoso, false en caso contrario.
     */
    public boolean iniciarSesion(String identificacion, String contrasena) {
        Usuario usuario = servicio.obtenerUsuario(identificacion);
        if (usuario != null && usuario.getContrasena().equals(contrasena)) {
            usuarioActual = usuario;
            return true;
        }
        return false;
    }

    /**
     * Agrega un residuo al usuario actual si los datos son válidos.
     * @param tipoMaterial Tipo de material del residuo.
     * @param peso Peso del residuo.
     * @param fechaEntrega Fecha de entrega del residuo.
     * @return true si el residuo fue agregado exitosamente, false en caso contrario.
     */
    public boolean agregarResiduo(String tipoMaterial, double peso, Date fechaEntrega) {
        if (usuarioActual == null) {
            return false;
        }

        if (!ValidadorResiduo.validarTipoMaterial(tipoMaterial)) {
            return false; // Tipo de material inválido
        }
        if (!ValidadorResiduo.validarPeso(peso)) {
            return false; // Peso inválido
        }

        usuarioActual.agregarResiduo(new Residuo(tipoMaterial, peso, fechaEntrega));
        servicio.guardarResiduosEnJson(); // Guardar cambios en el JSON
        return true; // Residuo agregado exitosamente
    }

    /**
     * Obtiene el nombre completo del usuario actualmente autenticado.
     * @param identificacion Identificación del usuario (no se usa, se mantiene por compatibilidad).
     * @return Nombre completo del usuario actual.
     */
    public String obtenerUsuarioPorNombre(String identificacion) {
        return usuarioActual.getNombreCompleto();
    }

    /**
     * Lista todos los residuos almacenados en el sistema en formato String.
     * @return Cadena con la lista de residuos.
     */
    public String listarResiduosDesdeJson() {
        return servicio.listarResiduosDesdeJson();
    }

    /**
     * Genera un reporte PDF estándar de los residuos.
     */
    public void generarReportePDF() {
        servicio.generarReporte();
    }

    /**
     * Genera un reporte PDF personalizado en la ruta especificada.
     * @param ruta Ruta donde se guardará el reporte PDF.
     */
    public void generarReportePDFPersonalizado(String ruta) {
        servicio.generarReportePersonalizado(ruta);
    }

    /**
     * Guarda los cambios actuales de residuos en el archivo JSON.
     */
    public void guardarCambios() {
        servicio.guardarResiduosEnJson();
    }

    /**
 * Verifica si ya existe un usuario registrado con el correo dado.
 * @param correo Correo electrónico a verificar.
 * @return true si el correo ya está registrado, false en caso contrario.
 */
public boolean existeCorreo(String correo) {
    return servicio.obtenerUsuarioPorCorreo(correo) != null;
}

/**
 * Verifica si ya existe un usuario registrado con el número de documento dado.
 * @param numeroDocumento Número de documento a verificar.
 * @return true si el número de documento ya está registrado, false en caso contrario.
 */
public boolean existeNumeroDocumento(String numeroDocumento) {
    return servicio.obtenerUsuario(numeroDocumento) != null;
}

public void recargarUsuariosDesdeJson() {
    servicio.cargarDatosDesdeJson();
}


}
