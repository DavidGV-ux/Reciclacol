package co.edu.uptc.vista;

import java.io.IOException;

import co.edu.uptc.controlador.ReciclajeControlador;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class InicioViewController {

    @FXML private Button btnIniciarSesion;
    @FXML private Button btnRegistro;
    @FXML private Button btnAcercaDe;
    @FXML private Button btnAccesibilidad;
    @FXML private Button btnAyuda;
    @FXML private ImageView logoImageView;
    @FXML private ComboBox<String> comboIdioma;
    @FXML private ComboBox<String> comboAccesibilidad;

    private ReciclajeControlador controlador;

    public InicioViewController() {}

    @FXML
    private void initialize() {
        comboIdioma.getItems().addAll("Español", "English");
        comboIdioma.setValue(AppContext.getCurrentLocale().getLanguage().equals("en") ? "English" : "Español");

        // Listener para cambiar idioma automáticamente
        comboIdioma.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                switch (newVal) {
                    case "Español":
                        AppContext.switchLanguage("es");
                        break;
                    case "English":
                        AppContext.switchLanguage("en");
                        break;
                    default:
                        mostrarError("Idioma no soportado", "Seleccione un idioma válido");
                        return;
                }
                recargarVista();
            }
        });

        cargarLogo();

        comboAccesibilidad.getItems().addAll("Normal", "Alto Contraste", "Letra Grande");
        comboAccesibilidad.setValue("Normal");
        comboAccesibilidad.valueProperty().addListener((obs, oldVal, newVal) -> {
            aplicarAccesibilidad(newVal);
        });
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

    private void recargarVista() {
        try {
            Stage stage = (Stage) comboIdioma.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/co/edu/uptc/vista/inicio_view.fxml"),
                AppContext.getBundle()
            );
            Parent root = loader.load();
            InicioViewController controller = loader.getController();
            controller.setControlador(this.controlador);
            stage.setScene(new Scene(root, 1440, 1024));
            Scene newScene = new Scene(root, 1440, 1024);
            AccesibilidadViewController.aplicarEstilo(newScene, AccesibilidadViewController.getEstiloActual());
            stage.setScene(newScene);
        } catch (IOException e) {
            mostrarError("Error al recargar", "No se pudo actualizar el idioma: " + e.getMessage());
        }
    }

    @FXML
    private void handleIniciarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/co/edu/uptc/vista/inicio_sesion_view.fxml"),
                AppContext.getBundle()
            );
            Parent root = loader.load();
            InicioSesionViewController loginController = loader.getController();
            loginController.setControlador(controlador);

            Stage stage = (Stage) btnIniciarSesion.getScene().getWindow();
            stage.setScene(new Scene(root, 1440, 1024));
            stage.setTitle(AppContext.getBundle().getString("login"));
        } catch (Exception e) {
            mostrarError("Error al cargar la vista de inicio de sesión", e.getMessage());
        }
    }

    @FXML
    private void handleRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/co/edu/uptc/vista/registro_view.fxml"),
                AppContext.getBundle()
            );
            Parent root = loader.load();
            RegistroViewController registroController = loader.getController();
            registroController.setControlador(controlador);

            Stage stage = (Stage) btnRegistro.getScene().getWindow();
            stage.setScene(new Scene(root, 1440, 1024));
            stage.setTitle(AppContext.getBundle().getString("register"));
            stage.show();
        } catch (Exception e) {
            mostrarError("Error al cargar la vista de registro", e.getMessage());
        }
    }

    public void setControlador(ReciclajeControlador controlador) {
        this.controlador = controlador;
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void aplicarAccesibilidad(String opcion) {
        Scene scene = comboAccesibilidad.getScene();
        if (scene != null) {
            AccesibilidadViewController.aplicarEstilo(scene, opcion);
        }
    }
    
}
