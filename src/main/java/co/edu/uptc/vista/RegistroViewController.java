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

    private ReciclajeControlador controlador;
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    @FXML
    private void initialize() {
        configurarIdiomas();
        cargarLogo();

        comboTipoDocumento.getItems().addAll("Cédula", "Pasaporte", "Tarjeta de identidad");
        comboTipoDocumento.setValue("Cédula");

        btnRegistrar.setOnAction(e -> registrarUsuario());
        btnInicio.setOnAction(e -> volverInicio());
        btnAyuda.setOnAction(e -> mostrarAyuda());

        comboAccesibilidad.getItems().addAll("Normal", "Alto Contraste", "Letra Grande");
        comboAccesibilidad.setValue("Normal");
        comboAccesibilidad.valueProperty().addListener((obs, oldVal, newVal) -> {
            aplicarAccesibilidad(newVal);
        });
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
            Image logoImage = new Image(getClass().getResourceAsStream("/co/edu/uptc/imagenes/Logo.png"));
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
            marcarError(txtPrimerNombre, lblErrorPrimerNombre, "Nombre inválido (solo letras)");
            valido = false;
        }
        if (!segundoNombre.isEmpty() && !co.edu.uptc.util.ValidadorEntrada.validarNombre(segundoNombre)) {
            marcarError(txtSegundoNombre, lblErrorSegundoNombre, "Nombre inválido (solo letras)");
            valido = false;
        }
        if (primerApellido.isEmpty() || !co.edu.uptc.util.ValidadorEntrada.validarNombre(primerApellido)) {
            marcarError(txtPrimerApellido, lblErrorPrimerApellido, "Apellido inválido");
            valido = false;
        }
        if (!segundoApellido.isEmpty() && !co.edu.uptc.util.ValidadorEntrada.validarNombre(segundoApellido)) {
            marcarError(txtSegundoApellido, lblErrorSegundoApellido, "Apellido inválido");
            valido = false;
        }
        if (tipoDocumento == null || tipoDocumento.isEmpty()) {
            marcarError(comboTipoDocumento, lblErrorTipoDocumento, "Seleccione un tipo de documento");
            valido = false;
        }
        if (numeroDocumento.isEmpty() || !co.edu.uptc.util.ValidadorEntrada.validarIdentificacion(numeroDocumento)) {
            marcarError(txtNumeroDocumento, lblErrorNumeroDocumento, "Documento inválido");
            valido = false;
        }
        if (telefonoStr.isEmpty() || !co.edu.uptc.util.ValidadorEntrada.validarTelefono(telefonoStr)) {
            marcarError(txtTelefono, lblErrorTelefono, "Teléfono inválido (solo números)");
            valido = false;
        }
        if (correo.isEmpty()) {
            marcarError(txtCorreo, lblErrorCorreo, AppContext.getBundle().getString("emailRequired"));
            valido = false;
        } else if (correo.contains(" ")) {
            marcarError(txtCorreo, lblErrorCorreo, AppContext.getBundle().getString("emailNoSpaces"));
            valido = false;
        } else if (!correo.matches(EMAIL_REGEX)) {
            marcarError(txtCorreo, lblErrorCorreo, AppContext.getBundle().getString("emailInvalid"));
            valido = false;
        }
        if (contrasena.length() < 4) {
            marcarError(pfContrasena, lblErrorContrasena, "Mínimo 4 caracteres");
            valido = false;
        }
        if (!contrasena.equals(confirmarContrasena)) {
            marcarError(pfConfirmarContrasena, lblErrorConfirmarContrasena, "Las contraseñas no coinciden");
            valido = false;
        }
        if (!chkTerminos.isSelected()) {
            lblErrorTerminos.setText("Debe aceptar los términos y condiciones");
            lblErrorTerminos.setVisible(true);
            valido = false;
        }

        if (!valido) return;

        int telefono;
        try {
            telefono = Integer.parseInt(telefonoStr);
        } catch (NumberFormatException ex) {
            marcarError(txtTelefono, lblErrorTelefono, "Teléfono debe ser numérico");
            return;
        }

        boolean exito = controlador.agregarUsuario(
                primerNombre,
                segundoNombre.isEmpty() ? null : segundoNombre,
                primerApellido,
                segundoApellido.isEmpty() ? null : segundoApellido,
                numeroDocumento,
                "", // Dirección opcional
                correo,
                contrasena,
                telefono);

        if (exito) {
            mostrarInfo("Usuario registrado exitosamente.");
            volverInicio();
        } else {
            mostrarError("Error al registrar usuario. Puede que la identificación ya exista.");
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

    
}
