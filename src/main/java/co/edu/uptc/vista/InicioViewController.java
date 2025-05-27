package co.edu.uptc.vista;


import co.edu.uptc.controlador.ReciclajeControlador;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;


public class InicioViewController {
    
    @FXML
    private Button btnIniciarSesion;
    
    @FXML
    private Button btnRegistro;
    
    @FXML
    private Button btnAcercaDe;
    
    @FXML
    private Button btnAccesibilidad;
    
    @FXML
    private Button btnAyuda;
    
    @FXML
    private ImageView logoImageView;

    @FXML
    private ComboBox<String> comboIdioma;
    
    private ReciclajeControlador controlador;
    
    // Constructor para inicializar el controlador
    public InicioViewController() {
        // El controlador será inicializado desde la clase Main
    }
    
    @FXML
    // Método que será llamado automáticamente después de la inicialización del FXML
    private void initialize() {
        comboIdioma.getItems().addAll("Español", "English", "Français");
        comboIdioma.setValue("Español");
    
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
    
    // Método para manejar el evento del botón Iniciar Sesión
    @FXML
private void handleIniciarSesion() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uptc/vista/inicio_sesion_view.fxml"));
        Parent root = loader.load();
        
        InicioSesionViewController loginController = loader.getController();
        loginController.setControlador(controlador);
        
        Stage stage = (Stage) btnIniciarSesion.getScene().getWindow();
        stage.setScene(new Scene(root, 1440, 1024));
        stage.setTitle("Inicio de Sesión");
    } catch (Exception e) {
        e.printStackTrace();
        mostrarError("Error al cargar la vista de inicio de sesión", e.getMessage());
    }
}

    
    @FXML
private void handleRegistro() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uptc/vista/registro_view.fxml"));
        Parent root = loader.load();

        RegistroViewController registroController = loader.getController();
        registroController.setControlador(controlador);

        Stage stage = (Stage) btnRegistro.getScene().getWindow();
        stage.setScene(new Scene(root, 1440, 1024));
        stage.setTitle("Registro de Usuario");
        stage.show();
    } catch (Exception e) {
        e.printStackTrace();
        mostrarError("Error al cargar la vista de registro", e.getMessage());
    }
}

    // Método para establecer el controlador desde fuera
    public void setControlador(ReciclajeControlador controlador) {
        this.controlador = controlador;
    }
    
    // Métodos de utilidad para mostrar alertas
    private void mostrarAlertaNoImplementado(String funcionalidad) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Funcionalidad en Desarrollo");
        alert.setHeaderText(null);
        alert.setContentText("La funcionalidad \"" + funcionalidad + "\" está en desarrollo.");
        alert.showAndWait();
    }
    
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
