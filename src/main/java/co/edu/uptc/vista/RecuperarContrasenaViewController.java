package co.edu.uptc.vista;

import java.io.IOException;

import co.edu.uptc.controlador.ReciclajeControlador;
import co.edu.uptc.servicio.EmailService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class RecuperarContrasenaViewController {

    @FXML private TextField txtCorreo;
    @FXML private TextField txtCodigo;
    @FXML private PasswordField txtNuevaContrasena;
    @FXML private PasswordField txtConfirmarContrasena;
    @FXML private Button btnEnviar;
    @FXML private Button btnValidar;
    @FXML private Button btnGuardar;
    @FXML private Button btnInicio;
    @FXML private Label lblMensaje;
    @FXML private HBox panelNuevaContrasena;
    @FXML private HBox panelConfirmarContrasena;
    @FXML private HBox panelEnviarCodigo;
    @FXML private HBox panelValidarCodigo;

    private ReciclajeControlador controlador;
    private EmailService emailService;
    private String identificacionActual;
    private String correoActual;

    @FXML
    private void initialize() {
        emailService = EmailService.getInstance();
        
        btnEnviar.setOnAction(e -> enviarCodigo());
        btnValidar.setOnAction(e -> validarCodigo());
        btnGuardar.setOnAction(e -> guardarNuevaContrasena());
        btnInicio.setOnAction(e -> volverAInicioSesion());
    }

    public void setControlador(ReciclajeControlador controlador) {
        this.controlador = controlador;
    }

    private void enviarCodigo() {
        String id = txtCorreo.getText().trim();
        if (id.isEmpty()) {
            mostrarMensaje("Debe ingresar su ID", true);
            return;
        }

        // Buscar el usuario por ID
        if (controlador.obtenerUsuario(id) == null) {
            mostrarMensaje("No se encontró un usuario con ese ID", true);
            return;
        }

        // Guardar el ID para uso posterior
        identificacionActual = id;
        correoActual = controlador.obtenerUsuario(id).getCorreo();

        // Enviar código al correo
        emailService.sendVerificationCode(correoActual);
        
        mostrarMensaje("Se ha enviado un código de verificación a " + correoActual, false);
    }

    private void validarCodigo() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            mostrarMensaje("Debe ingresar el código de verificación", true);
            return;
        }

        if (emailService.verifyCode(correoActual, codigo)) {
            // Ocultar paneles de ID y código
            panelEnviarCodigo.setVisible(false);
            panelValidarCodigo.setVisible(false);
            
            // Mostrar campos de nueva contraseña
            panelNuevaContrasena.setVisible(true);
            panelConfirmarContrasena.setVisible(true);
            btnGuardar.setVisible(true);
            
            mostrarMensaje("Código verificado correctamente", false);
        } else {
            mostrarMensaje("Código incorrecto", true);
        }
    }

    private void guardarNuevaContrasena() {
        String nuevaContrasena = txtNuevaContrasena.getText();
        String confirmarContrasena = txtConfirmarContrasena.getText();

        if (nuevaContrasena.isEmpty() || confirmarContrasena.isEmpty()) {
            mostrarMensaje("Debe completar ambos campos de contraseña", true);
            return;
        }

        if (!nuevaContrasena.equals(confirmarContrasena)) {
            mostrarMensaje("Las contraseñas no coinciden", true);
            return;
        }

        // Actualizar la contraseña en el usuario
        actualizarContrasena(identificacionActual, nuevaContrasena);
        
        // Mostrar mensaje de éxito y volver a inicio
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Contraseña actualizada");
        alert.setContentText("Su contraseña ha sido actualizada correctamente");
        alert.showAndWait();
        
        volverAInicioSesion();
    }
    
    private void actualizarContrasena(String id, String nuevaContrasena) {
        // Obtener el usuario
        var usuario = controlador.obtenerUsuario(id);
        if (usuario != null) {
            // Actualizar la contraseña
            usuario.setContrasena(nuevaContrasena);
            // Guardar los cambios
            controlador.guardarCambios();
        }
    }

    private void volverAInicioSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uptc/vista/inicio_sesion_view.fxml"));
            Parent root = loader.load();
            
            InicioSesionViewController controller = loader.getController();
            controller.setControlador(controlador);
            
            Stage stage = (Stage) btnInicio.getScene().getWindow();
            stage.setScene(new Scene(root, 1440, 1024));
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error al volver a inicio de sesión", true);
        }
    }

    private void mostrarMensaje(String mensaje, boolean esError) {
        lblMensaje.setText(mensaje);
        lblMensaje.getStyleClass().clear();
        if (esError) {
            lblMensaje.getStyleClass().add("error-mensaje");
        } else {
            lblMensaje.getStyleClass().add("mensaje-exito");
        }
    }
    

    private String enmascararCorreo(String correo) {
        if (correo == null || correo.isEmpty()) return "";
        
        int indexArroba = correo.indexOf('@');
        if (indexArroba == -1) return correo; // Si no tiene formato válido
        
        String nombreUsuario = correo.substring(0, indexArroba);
        String dominio = correo.substring(indexArroba);
        
        // Mostrar primeros 4 caracteres + *** + último carácter antes del @
        if (nombreUsuario.length() >= 4) {
            return nombreUsuario.substring(0, 4) 
                   + "***" 
                   + nombreUsuario.charAt(nombreUsuario.length() - 1) 
                   + dominio;
        }
        return "***" + dominio; // Para correos muy cortos
    }
    
}
