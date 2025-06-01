package co.edu.uptc.vista;

import java.io.IOException;
import java.util.ResourceBundle;

import co.edu.uptc.controlador.ReciclajeControlador;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class InicioSesionViewController {

    @FXML private TextField txtCorreoID;
    @FXML private PasswordField pfContrasena;
    @FXML private Button btnIniciarSesion;
    @FXML private Button btnVolver;
    @FXML private ComboBox<String> comboIdioma;
    @FXML private Hyperlink linkOlvidasteContrasena;
    @FXML private Button btnAcercaDe;
    @FXML private Button btnAyuda;
    @FXML private ImageView logoImageView;
    @FXML private ComboBox<String> comboAccesibilidad;

    private ReciclajeControlador controlador;

    @FXML
    private void initialize() {
        configurarComboIdioma();
        cargarLogo();

        linkOlvidasteContrasena.setOnAction(e -> mostrarRecuperarContrasena());
        btnAcercaDe.setOnAction(e -> mostrarAlertaNoImplementado("Acerca de"));
        btnAyuda.setOnAction(e -> mostrarAlertaNoImplementado("Ayuda"));
        btnIniciarSesion.setOnAction(e -> iniciarSesion());
        btnVolver.setOnAction(e -> volverInicio());

        comboAccesibilidad.getItems().addAll("Normal", "Alto Contraste", "Letra Grande");
        comboAccesibilidad.setValue("Normal");
        comboAccesibilidad.valueProperty().addListener((obs, oldVal, newVal) -> {
            aplicarAccesibilidad(newVal);
        });
    }

    private void configurarComboIdioma() {
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
                mostrarError("Error", "Idioma no soportado");
        }
    }

    private void recargarVista() {
        try {
            Stage stage = (Stage) comboIdioma.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/co/edu/uptc/vista/inicio_sesion_view.fxml"),
                AppContext.getBundle()
            );
            Parent root = loader.load();
            InicioSesionViewController controller = loader.getController();
            controller.setControlador(this.controlador);
            stage.setScene(new Scene(root, 1440, 1024));
        } catch (IOException e) {
            mostrarError("Error", "No se pudo actualizar el idioma: " + e.getMessage());
        }
    }

    private void cargarLogo() {
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

    private void iniciarSesion() {
        String correoID = txtCorreoID.getText().trim();
        String contrasena = pfContrasena.getText();

        if (correoID.isEmpty() || contrasena.isEmpty()) {
            mostrarError("Campos vacíos", "Todos los campos son obligatorios");
            return;
        }

        boolean exito = controlador.iniciarSesion(correoID, contrasena);

        if (exito) {
            mostrarInfo("Validación exitosa", "Ingresando al sistema...");
            Stage loadingStage = new Stage();
            loadingStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            loadingStage.initStyle(javafx.stage.StageStyle.UNDECORATED);

            javafx.scene.control.ProgressIndicator progressIndicator = new javafx.scene.control.ProgressIndicator();
            progressIndicator.setPrefSize(100, 100);

            javafx.scene.layout.VBox loadingVBox = new javafx.scene.layout.VBox(10);
            loadingVBox.setAlignment(javafx.geometry.Pos.CENTER);
            loadingVBox.setPadding(new javafx.geometry.Insets(20));
            loadingVBox.getChildren().addAll(progressIndicator, new javafx.scene.control.Label("Cargando..."));

            loadingStage.setScene(new javafx.scene.Scene(loadingVBox, 200, 150));
            loadingStage.show();

            javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
            delay.setOnFinished(event -> {
                loadingStage.close();
                mostrarMenuUsuario();
            });
            delay.play();
        } else {
            mostrarError("Error de autenticación", "Credenciales incorrectas");
        }
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarMenuUsuario() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/co/edu/uptc/vista/menu_usuario_view.fxml"),
                AppContext.getBundle()
            );
            Parent root = loader.load();
            MenuUsuarioViewController menuController = loader.getController();
            menuController.setControlador(controlador);

            Stage stage = (Stage) btnIniciarSesion.getScene().getWindow();
            stage.setScene(new Scene(root, 1440, 1024));
            stage.setTitle(AppContext.getBundle().getString("welcome"));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo cargar el menú de usuario");
        }
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

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root, 1440, 1024));
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo cargar la vista de inicio");
        }
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarRecuperarContrasena() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/co/edu/uptc/vista/recuperar_contrasena_view.fxml"),
                AppContext.getBundle()
            );
            Parent root = loader.load();
            RecuperarContrasenaViewController controller = loader.getController();
            controller.setControlador(controlador);

            Stage stage = (Stage) linkOlvidasteContrasena.getScene().getWindow();
            stage.setScene(new Scene(root, 1440, 1024));
            stage.setTitle(AppContext.getBundle().getString("register"));
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo cargar la vista de recuperación de contraseña");
        }
    }

    private void mostrarAlertaNoImplementado(String funcionalidad) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Funcionalidad en Desarrollo");
        alert.setHeaderText(null);
        alert.setContentText("La funcionalidad \"" + funcionalidad + "\" está en desarrollo.");
        alert.showAndWait();
    }

    private ResourceBundle bundle;

    public void setResourceBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    private void aplicarAccesibilidad(String opcion) {
        Scene scene = comboAccesibilidad.getScene();
        if (scene != null) {
            AccesibilidadViewController.aplicarEstilo(scene, opcion);
        }
    }
}
