package co.edu.uptc.vista;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import co.edu.uptc.controlador.ReciclajeControlador;
import co.edu.uptc.servicio.EmailService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
    @FXML private ComboBox<String> comboIdioma;

    private ReciclajeControlador controlador;
    private EmailService emailService;
    private String identificacionActual;
    private String correoActual;
    private ResourceBundle bundle;

    @FXML
    private void initialize() {
        emailService = EmailService.getInstance();
        bundle = AppContext.getBundle();

        btnEnviar.setOnAction(e -> enviarCodigo());
        btnValidar.setOnAction(e -> validarCodigo());
        btnGuardar.setOnAction(e -> guardarNuevaContrasena());
        btnInicio.setOnAction(e -> volverAInicioSesion());

        // ComboBox de idioma
        if (comboIdioma != null) {
            comboIdioma.getItems().setAll("Español", "English");
            comboIdioma.setValue(AppContext.getCurrentLocale().getLanguage().equals("en") ? "English" : "Español");

            comboIdioma.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    if (newVal.equals("Español") && !AppContext.getCurrentLocale().getLanguage().equals("es")) {
                        AppContext.switchLanguage("es");
                        recargarVista();
                    } else if (newVal.equals("English") && !AppContext.getCurrentLocale().getLanguage().equals("en")) {
                        AppContext.switchLanguage("en");
                        recargarVista();
                    }
                }
            });
        }
    }

    public void setControlador(ReciclajeControlador controlador) {
        this.controlador = controlador;
    }

    public void setResourceBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    private void enviarCodigo() {
        String id = txtCorreo.getText().trim();
        if (id.isEmpty()) {
            mostrarMensaje(bundle.getString("enterID"), true);
            return;
        }

        if (controlador.obtenerUsuario(id) == null) {
            mostrarMensaje(bundle.getString("userNotFound"), true);
            return;
        }

        identificacionActual = id;
        correoActual = controlador.obtenerUsuario(id).getCorreo();

        emailService.sendVerificationCode(correoActual);

        mostrarMensaje(MessageFormat.format(bundle.getString("codeSent"), correoActual), false);
    }

    private void validarCodigo() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            mostrarMensaje(bundle.getString("enterCode"), true);
            return;
        }

        if (emailService.verifyCode(correoActual, codigo)) {
            panelEnviarCodigo.setVisible(false);
            panelValidarCodigo.setVisible(false);
            panelNuevaContrasena.setVisible(true);
            panelConfirmarContrasena.setVisible(true);
            btnGuardar.setVisible(true);
            mostrarMensaje(bundle.getString("codeVerified"), false);
        } else {
            mostrarMensaje(bundle.getString("wrongCode"), true);
        }
    }

    private void guardarNuevaContrasena() {
        String nuevaContrasena = txtNuevaContrasena.getText();
        String confirmarContrasena = txtConfirmarContrasena.getText();

        if (nuevaContrasena.isEmpty() || confirmarContrasena.isEmpty()) {
            mostrarMensaje(bundle.getString("fillPasswords"), true);
            return;
        }

        if (!nuevaContrasena.equals(confirmarContrasena)) {
            mostrarMensaje(bundle.getString("passwordsDontMatch"), true);
            return;
        }

        actualizarContrasena(identificacionActual, nuevaContrasena);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(bundle.getString("passwordChanged"));
        alert.setContentText(bundle.getString("passwordChanged"));
        alert.showAndWait();

        volverAInicioSesion();
    }

    private void actualizarContrasena(String id, String nuevaContrasena) {
        var usuario = controlador.obtenerUsuario(id);
        if (usuario != null) {
            usuario.setContrasena(nuevaContrasena);
            controlador.guardarCambios();
        }
    }

    private void volverAInicioSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/co/edu/uptc/vista/inicio_sesion_view.fxml"),
                AppContext.getBundle()
            );
            Parent root = loader.load();

            InicioSesionViewController controller = loader.getController();
            controller.setControlador(controlador);

            Stage stage = (Stage) btnInicio.getScene().getWindow();
            stage.setScene(new Scene(root, 1440, 1024));
        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje(AppContext.getBundle().getString("errorBackToLogin"), true);
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

    private void recargarVista() {
        try {
            ResourceBundle bundle = AppContext.getBundle();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uptc/vista/recuperar_contrasena_view.fxml"), bundle);
            Parent root = loader.load();

            RecuperarContrasenaViewController controller = loader.getController();
            controller.setControlador(controlador);
            controller.setResourceBundle(bundle);

            Stage stage = (Stage) txtCorreo.getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setWidth(width);
            stage.setHeight(height);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Si necesitas enmascarar el correo
    private String enmascararCorreo(String correo) {
        if (correo == null || correo.isEmpty()) return "";
        int indexArroba = correo.indexOf('@');
        if (indexArroba == -1) return correo;
        String nombreUsuario = correo.substring(0, indexArroba);
        String dominio = correo.substring(indexArroba);
        if (nombreUsuario.length() >= 4) {
            return nombreUsuario.substring(0, 4)
                   + "***"
                   + nombreUsuario.charAt(nombreUsuario.length() - 1)
                   + dominio;
        }
        return "***" + dominio;
    }
}
