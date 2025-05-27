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
    @FXML private Button btnAccesibilidad;
    @FXML private Button btnAyuda;
    @FXML private ImageView logoImageView;

    private ReciclajeControlador controlador;

    @FXML
    private void initialize() {
        comboIdioma.getItems().addAll("Español", "English", "Français");
        comboIdioma.setValue("Español");
        linkOlvidasteContrasena.setOnAction(e -> mostrarRecuperarContrasena());
        btnAcercaDe.setOnAction(e -> mostrarAlertaNoImplementado("Acerca de"));
        btnAccesibilidad.setOnAction(e -> mostrarAlertaNoImplementado("Accesibilidad"));
        btnAyuda.setOnAction(e -> mostrarAlertaNoImplementado("Ayuda"));
        btnIniciarSesion.setOnAction(e -> iniciarSesion());
        btnVolver.setOnAction(e -> volverInicio());

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
            // Mostrar diálogo de validación exitosa
            mostrarInfo("Validación exitosa", "Ingresando al sistema...");
            
            // Crear una ventana de carga
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
            
            // Usar PauseTransition para simular la carga durante 3 segundos
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uptc/vista/menu_usuario_view.fxml"));
            Parent root = loader.load();
            
            // Obtener el controlador del menú y configurarlo
            MenuUsuarioViewController menuController = loader.getController();
            menuController.setControlador(controlador);
            
            // Crear y mostrar la escena
            Stage stage = (Stage) btnIniciarSesion.getScene().getWindow();
            stage.setScene(new Scene(root, 1440, 1024));
            stage.setTitle("Menú de Usuario");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo cargar el menú de usuario");
        }
    }
    

    private void volverInicio() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uptc/vista/inicio_view.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uptc/vista/recuperar_contrasena_view.fxml"));
            Parent root = loader.load();
            
            RecuperarContrasenaViewController controller = loader.getController();
            controller.setControlador(controlador);

            Stage stage = (Stage) linkOlvidasteContrasena.getScene().getWindow();
            stage.setScene(new Scene(root, 1440, 1024));
            stage.setTitle("Recuperar Contraseña");
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
}
