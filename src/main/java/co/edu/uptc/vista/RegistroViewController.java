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
    private ReciclajeControlador controlador;

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

    @FXML
    private void initialize() {
        comboTipoDocumento.getItems().addAll("Cédula", "Pasaporte", "Tarjeta de identidad");
        comboTipoDocumento.setValue("Cédula");

        btnRegistrar.setOnAction(e -> registrarUsuario());
        btnInicio.setOnAction(e -> volverInicio());
        btnAyuda.setOnAction(e -> mostrarAyuda());

        String imagePath = "/co/edu/uptc/imagenes/Logo.png";
        try {
            Image logoImage = new Image(getClass().getResourceAsStream(imagePath));
            if (logoImage.isError()) {
                System.err.println("Error al cargar la imagen: " + logoImage.getException());
            }
            logoImageView.setImage(logoImage);
        } catch (Exception e) {
            System.err.println("No se pudo cargar la imagen: " + imagePath);
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

        // Validación primer nombre
        if (primerNombre.isEmpty() || !co.edu.uptc.util.ValidadorEntrada.validarNombre(primerNombre)) {
            marcarError(txtPrimerNombre, lblErrorPrimerNombre, "Nombre inválido (solo letras)");
            valido = false;
        }

        // Validación segundo nombre (opcional, pero si no es vacío debe ser válido)
        if (!segundoNombre.isEmpty() && !co.edu.uptc.util.ValidadorEntrada.validarNombre(segundoNombre)) {
            marcarError(txtSegundoNombre, lblErrorSegundoNombre, "Nombre inválido (solo letras)");
            valido = false;
        }

        // Validación primer apellido
        if (primerApellido.isEmpty() || !co.edu.uptc.util.ValidadorEntrada.validarNombre(primerApellido)) {
            marcarError(txtPrimerApellido, lblErrorPrimerApellido, "Apellido inválido");
            valido = false;
        }

        // Validación segundo apellido (opcional, pero si no es vacío debe ser válido)
        if (!segundoApellido.isEmpty() && !co.edu.uptc.util.ValidadorEntrada.validarNombre(segundoApellido)) {
            marcarError(txtSegundoApellido, lblErrorSegundoApellido, "Apellido inválido");
            valido = false;
        }

        // Validación tipo documento
        if (tipoDocumento == null || tipoDocumento.isEmpty()) {
            marcarError(comboTipoDocumento, lblErrorTipoDocumento, "Seleccione un tipo de documento");
            valido = false;
        }

        // Validación número documento
        if (numeroDocumento.isEmpty() || !co.edu.uptc.util.ValidadorEntrada.validarIdentificacion(numeroDocumento)) {
            marcarError(txtNumeroDocumento, lblErrorNumeroDocumento, "Documento inválido");
            valido = false;
        }

        // Validación teléfono
        if (telefonoStr.isEmpty() || !co.edu.uptc.util.ValidadorEntrada.validarTelefono(telefonoStr)) {
            marcarError(txtTelefono, lblErrorTelefono, "Teléfono inválido (solo números)");
            valido = false;
        }

        // Validación correo
        if (correo.isEmpty() || !co.edu.uptc.util.ValidadorEntrada.validarCorreo(correo)) {
            marcarError(txtCorreo, lblErrorCorreo, "Correo electrónico inválido");
            valido = false;
        }

        // Validación contraseña
        if (contrasena.length() < 4) {
            marcarError(pfContrasena, lblErrorContrasena, "Mínimo 4 caracteres");
            valido = false;
        }

        // Validación confirmación contraseña
        if (!contrasena.equals(confirmarContrasena)) {
            marcarError(pfConfirmarContrasena, lblErrorConfirmarContrasena, "Las contraseñas no coinciden");
            valido = false;
        }

        // Validación términos
        if (!chkTerminos.isSelected()) {
            lblErrorTerminos.setText("Debe aceptar los términos y condiciones");
            lblErrorTerminos.setVisible(true);
            valido = false;
        }

        if (!valido) return;

        // Registrar usuario
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
                "", // Dirección no está en la imagen, puedes agregar si quieres
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uptc/vista/inicio_view.fxml"));
            Parent root = loader.load();

            InicioViewController inicioController = loader.getController();
            inicioController.setControlador(controlador);

            Stage stage = (Stage) btnInicio.getScene().getWindow();
            stage.setScene(new Scene(root, 1440, 1024));
            stage.setTitle("Inicio");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al cargar la ventana de inicio.");
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
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
        alert.setTitle("Ayuda");
        alert.setHeaderText("Ayuda");
        alert.setContentText("Aquí puedes colocar información de ayuda para el usuario.");
        alert.showAndWait();
    }
}
