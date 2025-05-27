package co.edu.uptc.vista;

import java.sql.Date;

import javax.swing.JOptionPane;

import co.edu.uptc.controlador.ReciclajeControlador;
import co.edu.uptc.modelo.Usuario;

public class ConsolaVista {
	private ReciclajeControlador controlador;
	private static final String[] TIPOS_RESIDUO = { "Plástico", "Vidrio", "Cartón", "Metal" };

	public ConsolaVista(ReciclajeControlador controlador) {
		this.controlador = controlador;
	}

	public void mostrarMenu() {
		while (true) {
			String opcion = JOptionPane.showInputDialog(null,
					"1. Registrar Usuario\n2. Iniciar Sesión\n3. Listar Residuos\n4. Consultar Puntos\n5. Generar Reporte PDF\n6. Salir",
					"Menú", JOptionPane.QUESTION_MESSAGE);
			if (opcion == null || opcion.equals("6"))
				return;
			switch (opcion) {
			case "1":
				registrarUsuario();
				break;
			case "2":
				iniciarSesion();
				break;
			case "3":
				listarResiduos();
				break;
			case "4":
				consultarPuntos();
				break;
			case "5":
				controlador.generarReportePDF();
				JOptionPane.showMessageDialog(null, "Reporte generado: Reporte_Reciclaje.pdf", "Éxito",
						JOptionPane.INFORMATION_MESSAGE);
				break;
			}
		}
	}

    public void mostrarVentanaInicio() {
        while (true) {
            String[] opciones = {"Iniciar Sesión", "Registrar Usuario", "Salir"};
            int seleccion = JOptionPane.showOptionDialog(
                null,
                "Seleccione una opción:",
                "Bienvenido",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
            );
            if (seleccion == 0) { // Iniciar Sesión
                if (iniciarSesion()) {
                    menuUsuario();
                }
            } else if (seleccion == 1) { // Registrar Usuario
                registrarUsuario();
            } else { // Salir o cerrar ventana
                break;
            }
        }
    }
    

    public void registrarUsuario() {
        String primerNombre, segundoNombre, primerApellido, segundoApellido, id, direccion, correo, contrasena, telefonoStr;
        int telefono = 0;
    
        // Primer nombre (obligatorio)
        do {
            primerNombre = JOptionPane.showInputDialog("Primer nombre:");
            if (!co.edu.uptc.util.ValidadorEntrada.validarNombre(primerNombre)) {
                JOptionPane.showMessageDialog(null, "Primer nombre inválido. Solo letras y espacios.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } while (!co.edu.uptc.util.ValidadorEntrada.validarNombre(primerNombre));
    
        // Segundo nombre (opcional)
        segundoNombre = JOptionPane.showInputDialog("Segundo nombre (opcional):");
        if (segundoNombre != null && segundoNombre.trim().isEmpty()) segundoNombre = null;
    
        // Primer apellido (obligatorio)
        do {
            primerApellido = JOptionPane.showInputDialog("Primer apellido:");
            if (!co.edu.uptc.util.ValidadorEntrada.validarNombre(primerApellido)) {
                JOptionPane.showMessageDialog(null, "Primer apellido inválido. Solo letras y espacios.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } while (!co.edu.uptc.util.ValidadorEntrada.validarNombre(primerApellido));
    
        // Segundo apellido (opcional)
        segundoApellido = JOptionPane.showInputDialog("Segundo apellido (opcional):");
        if (segundoApellido != null && segundoApellido.trim().isEmpty()) segundoApellido = null;
    
        // Identificación
        do {
            id = JOptionPane.showInputDialog("Identificación:");
            if (!co.edu.uptc.util.ValidadorEntrada.validarIdentificacion(id)) {
                JOptionPane.showMessageDialog(null, "Identificación inválida. Solo números.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } while (!co.edu.uptc.util.ValidadorEntrada.validarIdentificacion(id));
    
        // Contraseña
        do {
            contrasena = JOptionPane.showInputDialog("Contraseña:");
            if (contrasena == null || contrasena.length() < 4) {
                JOptionPane.showMessageDialog(null, "La contraseña debe tener al menos 4 caracteres.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } while (contrasena == null || contrasena.length() < 4);
    
        direccion = JOptionPane.showInputDialog("Dirección:");
    
        // Correo
        do {
            correo = JOptionPane.showInputDialog("Correo:");
            if (!co.edu.uptc.util.ValidadorEntrada.validarCorreo(correo)) {
                JOptionPane.showMessageDialog(null, "Correo inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } while (!co.edu.uptc.util.ValidadorEntrada.validarCorreo(correo));
    
        // Teléfono (solo números, obligatorio)
        do {
            telefonoStr = JOptionPane.showInputDialog("Teléfono (solo números):");
            if (!co.edu.uptc.util.ValidadorEntrada.validarTelefono(telefonoStr)) {
                JOptionPane.showMessageDialog(null, "El teléfono debe contener solo números.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } while (!co.edu.uptc.util.ValidadorEntrada.validarTelefono(telefonoStr));
        telefono = Integer.parseInt(telefonoStr);
    
        // Ahora sí, registrar usuario con todos los datos correctos
        if (controlador.agregarUsuario(primerNombre, segundoNombre, primerApellido, segundoApellido, id, direccion, correo, contrasena, telefono)) {
            JOptionPane.showMessageDialog(null, "Usuario registrado exitosamente!");
        } else {
            JOptionPane.showMessageDialog(null, "Error al registrar usuario. Puede que la identificación ya exista.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    

        private boolean iniciarSesion() {
            String id = JOptionPane.showInputDialog("Ingrese su identificación:");
            String contrasena = JOptionPane.showInputDialog("Ingrese su contraseña:");
            if (controlador.iniciarSesion(id, contrasena)) {
                JOptionPane.showMessageDialog(null, "Inicio de sesión exitoso!");
                menuUsuario();
            } else {
                JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos.");
            }
            return false;
        }
        
        

        private void menuUsuario() {
            while (true) {
                String opcion = JOptionPane.showInputDialog(
                    null,
                    "1. Registrar Residuo\n2. Listar Residuos\n3. Consultar Puntos\n4. Generar Reporte PDF\n5. Cerrar Sesión",
                    "Menú Usuario - " + controlador.obtenerUsuarioPorNombre(""),
                    JOptionPane.QUESTION_MESSAGE
                );
                if (opcion == null || opcion.equals("5")) // Cerrar sesión
                    return;
                switch (opcion) {
                    case "1":
                        registrarResiduo();
                        break;
                    case "2":
                        listarResiduos();
                        break;
                    case "3":
                        consultarPuntos();
                        break;
                    case "4":
                        controlador.generarReportePDF();
                        JOptionPane.showMessageDialog(null, "Reporte generado: Reporte_Reciclaje.pdf", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        break;
                }
            }
        }
        

	private void registrarResiduo() {
        String tipo = (String) JOptionPane.showInputDialog(null, "Seleccione el tipo de material:", "Registrar Residuo",
                JOptionPane.QUESTION_MESSAGE, null, TIPOS_RESIDUO, TIPOS_RESIDUO[0]);
        if (tipo == null)
            return;

        String pesoStr = JOptionPane.showInputDialog("Peso (Kg):");
        try {
            double peso = Double.parseDouble(pesoStr);
            if (peso < 0) {
                JOptionPane.showMessageDialog(null, "No puede poner pesos negativos", "Error", JOptionPane.ERROR_MESSAGE);
                registrarResiduo();
            } else {
                if (controlador.agregarResiduo(tipo, peso, new Date(System.currentTimeMillis()))) {
                    JOptionPane.showMessageDialog(null, "Residuo registrado exitosamente.");
                } else {
                    JOptionPane.showMessageDialog(null, "Error al registrar el residuo.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Peso inválido. Intente de nuevo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

	private void listarResiduos() {
		String datos = controlador.listarResiduosDesdeJson();
		JOptionPane.showMessageDialog(null, datos, "Lista de Residuos", JOptionPane.INFORMATION_MESSAGE);
	}

	private void consultarPuntos() {
		String id = JOptionPane.showInputDialog("Ingrese la identificación del usuario:");
		Usuario usuario = controlador.obtenerUsuario(id);
		if (usuario != null) {
			JOptionPane.showMessageDialog(null, "Nombre: " + usuario.getNombreCompleto() + "\nPuntos ecológicos acumulados: " + usuario.getPuntosEcologicos(),
					"Consulta de usuario y puntos", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(null, "Usuario no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

}
