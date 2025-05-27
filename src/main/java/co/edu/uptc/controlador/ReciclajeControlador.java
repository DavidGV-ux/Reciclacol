package co.edu.uptc.controlador;

import java.sql.Date;

import co.edu.uptc.modelo.Residuo;
import co.edu.uptc.modelo.Usuario;
import co.edu.uptc.servicio.ReciclajeServicio;
import co.edu.uptc.util.ValidadorEntrada;
import co.edu.uptc.util.ValidadorResiduo;

public class ReciclajeControlador {
    private ReciclajeServicio servicio;
    private Usuario usuarioActual;

    public ReciclajeControlador(ReciclajeServicio servicio) {
        this.servicio = servicio;
    }

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


    public Usuario obtenerUsuario(String identificacion) {
        return servicio.obtenerUsuario(identificacion);
    }

	public Usuario obtenerUsuarioActual() {
		return usuarioActual;
	}	

    public boolean iniciarSesion(String identificacion, String contrasena) {
		Usuario usuario = servicio.obtenerUsuario(identificacion);
		if (usuario != null && usuario.getContrasena().equals(contrasena)) {
			usuarioActual = usuario;
			return true;
		}
		return false;
	}
	

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
    
    public String obtenerUsuarioPorNombre(String identificacion) {
    	return usuarioActual.getNombreCompleto();
    }

    public String listarResiduosDesdeJson() {
        return servicio.listarResiduosDesdeJson();
    }
    
    public void generarReportePDF() {
        servicio.generarReporte(); 
    }
    
    public void generarReportePDFPersonalizado(String ruta) {
        servicio.generarReportePersonalizado(ruta);
    }
    
    // Añadir este método a ReciclajeControlador
public void guardarCambios() {
    servicio.guardarResiduosEnJson();
}


}
