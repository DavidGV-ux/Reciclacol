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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


public class RecuperarContrasenaViewController {

    @FXML private TextField txtDocumento;
    @FXML private TextField txtCodigo;
    @FXML private PasswordField txtNuevaContrasena;
    @FXML private PasswordField txtConfirmarContrasena;
    @FXML private Button btnEnviar;
    @FXML private Button btnValidar;
    @FXML private Button btnGuardar;
    @FXML private Button btnInicio;
    @FXML private Label lblMensajeEnvio;
    @FXML private Label lblMensajeContrasena;
    @FXML private HBox panelNuevaContrasena;
    @FXML private HBox panelConfirmarContrasena;
    @FXML private HBox panelEnviarCodigo;
    @FXML private HBox panelValidarCodigo;
    @FXML private ComboBox<String> comboIdioma;
    @FXML private Label lblErrorContrasena;
    @FXML private Label lblErrorConfirmarContrasena;
    @FXML private PasswordField pfContrasena;
    @FXML private PasswordField pfConfirmarContrasena;
    @FXML private Label lblPasswordHelp;

    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

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

          String passwordTooltip = AppContext.getBundle().getString("password.tooltip");
        Tooltip tooltip = new Tooltip(passwordTooltip);
        Tooltip.install(lblPasswordHelp, tooltip);
    }

    public void setControlador(ReciclajeControlador controlador) {
        this.controlador = controlador;
    }

    public void setResourceBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    private void enviarCodigo() {
        limpiarMensajes();
        String id = txtDocumento.getText().trim();
        if (id.isEmpty()) {
            mostrarMensajeEnvio(bundle.getString("enterID"), true);
            return;
        }

        if (controlador.obtenerUsuario(id) == null) {
            mostrarMensajeEnvio(bundle.getString("userNotFound"), true);
            return;
        }

        identificacionActual = id;
        correoActual = controlador.obtenerUsuario(id).getCorreo();

        emailService.sendVerificationCode(correoActual);
        mostrarMensajeEnvio(MessageFormat.format(bundle.getString("codeSent"), enmascararCorreo(correoActual)), false);
    }

    private void validarCodigo() {
        limpiarMensajes();
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            mostrarMensajeEnvio(bundle.getString("enterCode"), true);
            return;
        }

        if (emailService.verifyCode(correoActual, codigo)) {
            panelEnviarCodigo.setVisible(false);
            panelValidarCodigo.setVisible(false);
            panelNuevaContrasena.setVisible(true);
            panelConfirmarContrasena.setVisible(true);
            btnGuardar.setVisible(true);
            limpiarMensajes();
        } else {
            mostrarMensajeEnvio(bundle.getString("wrongCode"), true);
        }
    }

    private void guardarNuevaContrasena() {
        limpiarMensajes();
        boolean valido = true;
        String nuevaContrasena = txtNuevaContrasena.getText();
        String confirmarContrasena = txtConfirmarContrasena.getText();
    
        // Validar campos vacíos
        if (nuevaContrasena.isEmpty() || confirmarContrasena.isEmpty()) {
            mostrarMensajeContrasena(bundle.getString("fillPasswords"), true);
            return;
        }
    
        // Validar formato de la contraseña
        if (!nuevaContrasena.matches(PASSWORD_REGEX)) {
            marcarError(txtNuevaContrasena, lblMensajeContrasena, bundle.getString("passwordInvalid"));
            valido = false;
        }
    
        // Validar coincidencia de contraseñas
        if (!nuevaContrasena.equals(confirmarContrasena)) {
            mostrarMensajeContrasena(bundle.getString("passwordsDontMatch"), true);
            return;
        }
    
        if (!valido) {
            return;
        }
    
        // Actualizar la contraseña
        actualizarContrasena(identificacionActual, nuevaContrasena);
        mostrarMensajeContrasena(bundle.getString("passwordChanged"), false);
    
        // Oculta el mensaje y vuelve al inicio después de 2 segundos
        new Thread(() -> {
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            javafx.application.Platform.runLater(this::volverAInicioSesion);
        }).start();
    }

    private void marcarError(Control control, Label label, String mensaje) {
        if (!control.getStyleClass().contains("campo-invalido")) {
            control.getStyleClass().add("campo-invalido");
        }
        label.setText(mensaje);
        label.setVisible(true);
    }


    private void actualizarContrasena(String id, String nuevaContrasena) {
        var usuario = controlador.obtenerUsuario(id);
        if (usuario != null) {
            usuario.setContrasena(nuevaContrasena);
            controlador.guardarCambios();
        }
    }

    private void mostrarMensajeEnvio(String mensaje, boolean esError) {
        lblMensajeEnvio.setText(mensaje);
        lblMensajeEnvio.setStyle(esError ? "-fx-text-fill: #d32f2f;" : "-fx-text-fill: #388e3c;");
    }

    private void mostrarMensajeContrasena(String mensaje, boolean esError) {
        lblMensajeContrasena.setText(mensaje);
        lblMensajeContrasena.setStyle(esError ? "-fx-text-fill: #d32f2f;" : "-fx-text-fill: #388e3c;");
    }

    private void limpiarMensajes() {
        lblMensajeEnvio.setText("");
        lblMensajeContrasena.setText("");
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
            mostrarMensajeEnvio(AppContext.getBundle().getString("errorBackToLogin"), true);
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

            Stage stage = (Stage) txtDocumento.getScene().getWindow();
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
