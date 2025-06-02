package co.edu.uptc.vista;

import java.io.IOException;

import co.edu.uptc.controlador.ReciclajeControlador;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.text.Text;
import javafx.scene.control.Tooltip;


public class RegistroViewController {

    @FXML private TextField txtPrimerNombre;
    @FXML private TextField txtSegundoNombre;
    @FXML private TextField txtPrimerApellido;
    @FXML private TextField txtSegundoApellido;
    @FXML private ComboBox<String> comboTipoDocumento;
    @FXML private TextField txtNumeroDocumento;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private PasswordField pfContrasena;
    @FXML private PasswordField pfConfirmarContrasena;
    @FXML private CheckBox chkTerminos;
    @FXML private Button btnRegistrar;
    @FXML private Button btnInicio;
    @FXML private Button btnAyuda;
    @FXML private ImageView logoImageView;
    @FXML private ComboBox<String> comboIdioma;
    @FXML private Hyperlink acceptTerms;
    @FXML private Label lblPasswordHelp;

    // Labels de error para cada campo
    @FXML private Label lblErrorPrimerNombre;
    @FXML private Label lblErrorSegundoNombre;
    @FXML private Label lblErrorPrimerApellido;
    @FXML private Label lblErrorSegundoApellido;
    @FXML private Label lblErrorTipoDocumento;
    @FXML private Label lblErrorNumeroDocumento;
    @FXML private Label lblErrorTelefono;
    @FXML private Label lblErrorCorreo;
    @FXML private Label lblErrorContrasena;
    @FXML private Label lblErrorConfirmarContrasena;
    @FXML private Label lblErrorTerminos;
    @FXML private ComboBox<String> comboAccesibilidad;
    @FXML private Hyperlink linkLogin;


    private ReciclajeControlador controlador;
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    @FXML
    private void initialize() {
        configurarIdiomas();
        cargarLogo();

        comboTipoDocumento.getItems().addAll("Cédula", "Pasaporte", "Tarjeta de identidad");
        comboTipoDocumento.setValue("Cédula");

        btnRegistrar.setOnAction(e -> registrarUsuario());
        btnInicio.setOnAction(e -> volverInicio());
        btnAyuda.setOnAction(e -> mostrarAyuda());
        linkLogin.setOnAction(e -> irALogin());

        comboAccesibilidad.getItems().addAll("Normal", "Alto Contraste", "Letra Grande");
        comboAccesibilidad.setValue("Normal");
        comboAccesibilidad.valueProperty().addListener((obs, oldVal, newVal) -> {
            aplicarAccesibilidad(newVal);
        });

        chkTerminos.setAllowIndeterminate(false);
            chkTerminos.setSelected(false);

            chkTerminos.setOnAction(e -> {
                if (chkTerminos.isSelected()) {
                    boolean aceptado = mostrarTerminosYCondiciones();
                    chkTerminos.setSelected(aceptado);
    }
});

        String passwordTooltip = AppContext.getBundle().getString("password.tooltip");
        Tooltip tooltip = new Tooltip(passwordTooltip);
        Tooltip.install(lblPasswordHelp, tooltip);

    }

    private void configurarIdiomas() {
        comboIdioma.getItems().addAll("Español", "English");
        String lang = AppContext.getCurrentLocale().getLanguage();
        comboIdioma.setValue(lang.equals("en") ? "English" : "Español");

        comboIdioma.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                cambiarIdioma(newVal);
                recargarVista();
            }
        });
    }

    private void cambiarIdioma(String nombreIdioma) {
        switch(nombreIdioma) {
            case "Español":
                AppContext.switchLanguage("es");
                break;
            case "English":
                AppContext.switchLanguage("en");
                break;
            default:
                mostrarError("Idioma no soportado", "Seleccione un idioma válido");
        }
    }

    private void recargarVista() {
        try {
            Stage stage = (Stage) comboIdioma.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/co/edu/uptc/vista/registro_view.fxml"),
                AppContext.getBundle()
            );
            Parent root = loader.load();
            RegistroViewController controller = loader.getController();
            controller.setControlador(this.controlador);
            stage.setScene(new Scene(root, 1440, 1024));
        } catch (IOException e) {
            mostrarError("Error", "No se pudo actualizar el idioma: " + e.getMessage());
        }
    }

    private void cargarLogo() {
        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/co/edu/uptc/imagenes/registro.png"));
            logoImageView.setImage(logoImage);
        } catch (Exception e) {
            System.err.println("No se pudo cargar la imagen del logo");
            e.printStackTrace();
        }
    }

    public void setControlador(ReciclajeControlador controlador) {
        this.controlador = controlador;
    }

    private void registrarUsuario() {
        resetErrores();
        boolean valido = true;
    
        String primerNombre = txtPrimerNombre.getText().trim();
        String segundoNombre = txtSegundoNombre.getText().trim();
        String primerApellido = txtPrimerApellido.getText().trim();
        String segundoApellido = txtSegundoApellido.getText().trim();
        String tipoDocumento = comboTipoDocumento.getValue();
        String numeroDocumento = txtNumeroDocumento.getText().trim();
        String telefonoStr = txtTelefono.getText().trim();
        String correo = txtCorreo.getText().trim();
        String contrasena = pfContrasena.getText();
        String confirmarContrasena = pfConfirmarContrasena.getText();
    
        // Validaciones
        if (primerNombre.isEmpty() || !co.edu.uptc.util.ValidadorEntrada.validarNombre(primerNombre)) {
            marcarError(txtPrimerNombre, lblErrorPrimerNombre, AppContext.getBundle().getString("invalidFirstName"));
            valido = false;
        }
        if (!segundoNombre.isEmpty() && !co.edu.uptc.util.ValidadorEntrada.validarNombre(segundoNombre)) {
            marcarError(txtSegundoNombre, lblErrorSegundoNombre, AppContext.getBundle().getString("invalidSecondName"));
            valido = false;
        }
        if (primerApellido.isEmpty() || !co.edu.uptc.util.ValidadorEntrada.validarNombre(primerApellido)) {
            marcarError(txtPrimerApellido, lblErrorPrimerApellido, AppContext.getBundle().getString("invalidFirstSurname"));
            valido = false;
        }
        if (!segundoApellido.isEmpty() && !co.edu.uptc.util.ValidadorEntrada.validarNombre(segundoApellido)) {
            marcarError(txtSegundoApellido, lblErrorSegundoApellido, AppContext.getBundle().getString("invalidSecondSurname"));
            valido = false;
        }
        if (tipoDocumento == null || tipoDocumento.isEmpty()) {
            marcarError(comboTipoDocumento, lblErrorTipoDocumento, AppContext.getBundle().getString("selectDocumentType"));
            valido = false;
        }
        if (numeroDocumento.isEmpty() || !co.edu.uptc.util.ValidadorEntrada.validarIdentificacion(numeroDocumento)) {
            marcarError(txtNumeroDocumento, lblErrorNumeroDocumento, AppContext.getBundle().getString("invalidDocument"));
            valido = false;
        }
        if (telefonoStr.isEmpty() || !co.edu.uptc.util.ValidadorEntrada.validarTelefono(telefonoStr)) {
            marcarError(txtTelefono, lblErrorTelefono, AppContext.getBundle().getString("invalidPhone"));
            valido = false;
        }
        if (!contrasena.matches(PASSWORD_REGEX)) {
            marcarError(pfContrasena, lblErrorContrasena, AppContext.getBundle().getString("passwordInvalid"));
            valido = false;
        }
        if (!contrasena.equals(confirmarContrasena)) {
            marcarError(pfConfirmarContrasena, lblErrorConfirmarContrasena, AppContext.getBundle().getString("passwordsNoMatch"));
            valido = false;
        }
        if (!chkTerminos.isSelected()) {
            lblErrorTerminos.setText(AppContext.getBundle().getString("mustAcceptTerms"));
            lblErrorTerminos.setVisible(true);
            valido = false;
        }
        if (controlador.existeCorreo(correo)) {
            marcarError(txtCorreo, lblErrorCorreo, AppContext.getBundle().getString("emailRegistered"));
            valido = false;
        }
        if (controlador.existeNumeroDocumento(numeroDocumento)) {
            marcarError(txtNumeroDocumento, lblErrorNumeroDocumento, AppContext.getBundle().getString("documentRegistered"));
            valido = false;
        }
    
        int telefono = 0;
        try {
            telefono = Integer.parseInt(telefonoStr);
        } catch (NumberFormatException ex) {
            marcarError(txtTelefono, lblErrorTelefono, AppContext.getBundle().getString("phoneMustBeNumeric"));
            return;
        }
    
        if (!valido) return;
    
        // Registro del usuario
        boolean registrado = controlador.agregarUsuario(
            primerNombre,
            segundoNombre,
            primerApellido,
            segundoApellido,
            numeroDocumento,
            "", // dirección, si tienes el campo, cámbialo aquí
            correo,
            contrasena,
            telefono
        );
    
        if (registrado) {
            controlador.recargarUsuariosDesdeJson(); // <--- ¡Clave!
            mostrarInfo(AppContext.getBundle().getString("registerSuccess"));
            irALogin();
        } else {
            mostrarError(AppContext.getBundle().getString("registerFailed"));
        }
    }
    

    private void resetErrores() {
        limpiarError(txtPrimerNombre, lblErrorPrimerNombre);
        limpiarError(txtSegundoNombre, lblErrorSegundoNombre);
        limpiarError(txtPrimerApellido, lblErrorPrimerApellido);
        limpiarError(txtSegundoApellido, lblErrorSegundoApellido);
        limpiarError(comboTipoDocumento, lblErrorTipoDocumento);
        limpiarError(txtNumeroDocumento, lblErrorNumeroDocumento);
        limpiarError(txtTelefono, lblErrorTelefono);
        limpiarError(txtCorreo, lblErrorCorreo);
        limpiarError(pfContrasena, lblErrorContrasena);
        limpiarError(pfConfirmarContrasena, lblErrorConfirmarContrasena);
        lblErrorTerminos.setVisible(false);
    }

    private void limpiarError(Control control, Label label) {
        control.getStyleClass().remove("campo-invalido");
        label.setVisible(false);
        label.setText("");
    }

    private void marcarError(Control control, Label label, String mensaje) {
        if (!control.getStyleClass().contains("campo-invalido")) {
            control.getStyleClass().add("campo-invalido");
        }
        label.setText(mensaje);
        label.setVisible(true);
    }

    private void volverInicio() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/co/edu/uptc/vista/inicio_view.fxml"),
                AppContext.getBundle()
            );
            Parent root = loader.load();

            InicioViewController inicioController = loader.getController();
            inicioController.setControlador(controlador);

            Stage stage = (Stage) btnInicio.getScene().getWindow();
            stage.setScene(new Scene(root, 1440, 1024));
            stage.setTitle(AppContext.getBundle().getString("welcome"));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al cargar la ventana de inicio.");
        }
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        mostrarError("Error", mensaje);
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAyuda() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(AppContext.getBundle().getString("help"));
        alert.setHeaderText(AppContext.getBundle().getString("help"));
        alert.setContentText("Aquí puedes colocar información de ayuda para el usuario.");
        alert.showAndWait();
    }

    private void aplicarAccesibilidad(String opcion) {
        Scene scene = comboAccesibilidad.getScene();
        if (scene != null) {
            AccesibilidadViewController.aplicarEstilo(scene, opcion);
        }
    }

    private boolean mostrarTerminosYCondiciones() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(AppContext.getBundle().getString("terms.title"));
        dialog.setHeaderText(AppContext.getBundle().getString("terms.header"));
    
        String terminos = AppContext.getBundle().getString("terms.content")
            .replace("\\n", "\n"); // Para mostrar saltos de línea correctamente
    
        Text textoTerminos = new Text(terminos);
        textoTerminos.setWrappingWidth(450);
    
        ScrollPane scroll = new ScrollPane(textoTerminos);
        scroll.setPrefSize(480, 260);
        scroll.setFitToWidth(true);
    
        VBox content = new VBox(scroll);
        content.setPadding(new javafx.geometry.Insets(10));
        dialog.getDialogPane().setContent(content);
    
        ButtonType btnAceptar = new ButtonType(AppContext.getBundle().getString("terms.accept"), ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType(AppContext.getBundle().getString("terms.cancel"), ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().setAll(btnAceptar, btnCancelar);
    
        dialog.setResizable(true);
    
        ButtonType result = dialog.showAndWait().orElse(btnCancelar);
        return result == btnAceptar;
    }    

    private void irALogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/co/edu/uptc/vista/inicio_sesion_view.fxml"),
                AppContext.getBundle()
            );
            Parent root = loader.load();
            InicioSesionViewController inicioSesionController = loader.getController();
            inicioSesionController.setControlador(controlador);
    
            Stage stage = (Stage) linkLogin.getScene().getWindow();
            stage.setScene(new Scene(root, 1440, 1024));
            stage.setTitle(AppContext.getBundle().getString("register"));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo cargar la vista de registro");
        }
    }
}
