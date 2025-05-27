package co.edu.uptc.vista;

import co.edu.uptc.controlador.ReciclajeControlador;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MenuUsuarioViewController {
    
    @FXML private Button btnInicio;
    @FXML private Button btnRegistrarResiduos;
    @FXML private Button btnListarResiduos;
    @FXML private Button btnConsultarPuntos;
    @FXML private Button btnGenerarReporte;
    @FXML private Button btnAyuda;
    @FXML private Button btnAccesibilidad;
    @FXML private ComboBox<String> comboIdioma;
    @FXML private Label lblNombreUsuario;
    @FXML private BorderPane contentPane;
        
    @FXML
    private ImageView logoImageView;
    
    private ReciclajeControlador controlador;
    
    @FXML
    private void initialize() {
        comboIdioma.getItems().addAll("Español", "English", "Français");
        comboIdioma.setValue("Español");
        
        btnInicio.setOnAction(e -> cerrarSesion());
        btnAyuda.setOnAction(e -> mostrarAyuda());
        btnAccesibilidad.setOnAction(e -> mostrarAccesibilidad());
        
        btnRegistrarResiduos.setOnAction(e -> mostrarRegistrarResiduos());
        btnListarResiduos.setOnAction(e -> mostrarListarResiduos());
        btnConsultarPuntos.setOnAction(e -> mostrarConsultarPuntos());
        btnGenerarReporte.setOnAction(e -> mostrarGenerarReporte());

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
        actualizarDatosUsuario();
    }
    
    private void actualizarDatosUsuario() {
        if (controlador != null) {
            lblNombreUsuario.setText(controlador.obtenerUsuarioPorNombre(""));
        }
    }
    
    private void cerrarSesion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cerrar sesión");
        alert.setHeaderText("¿Está seguro que desea cerrar la sesión?");
        alert.setContentText("Se perderán los datos no guardados.");
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/co/edu/uptc/vista/inicio_view.fxml"));
                javafx.scene.Parent root = loader.load();
                
                InicioViewController controller = loader.getController();
                controller.setControlador(controlador);
                
                javafx.scene.Scene scene = new javafx.scene.Scene(root, 800, 400);
                Stage stage = (Stage) btnInicio.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Inicio");
            } catch (Exception e) {
                e.printStackTrace();
                mostrarError("Error al cerrar sesión");
            }
        }
    }
    
    private void mostrarRegistrarResiduos() {
    try {
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/co/edu/uptc/vista/registrar_residuo_form.fxml"));
        VBox form = loader.load();

        RegistrarResiduoController formController = loader.getController();
        formController.setControlador(controlador);

        contentPane.setCenter(form);
    } catch (Exception e) {
        e.printStackTrace();
        mostrarError("No se pudo cargar el formulario de registro de residuo.");
    }
}
    
private void mostrarListarResiduos() {
    try {
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/co/edu/uptc/vista/listar_residuos_view.fxml"));
        VBox vista = loader.load();

        ListarResiduosController controller = loader.getController();
        controller.setControlador(controlador);
        controller.setOnClose(() -> contentPane.setCenter(null)); // Cierra la lista y limpia el área central

        contentPane.setCenter(vista);
    } catch (Exception e) {
        e.printStackTrace();
        mostrarError("No se pudo cargar la lista de residuos.");
    }
}
    
private void mostrarConsultarPuntos() {
    try {
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/co/edu/uptc/vista/listar_puntos_view.fxml"));
        VBox vista = loader.load();

        ListarPuntosController controller = loader.getController();
        controller.setControlador(controlador);
        controller.setOnClose(() -> contentPane.setCenter(null)); // Cierra la lista y limpia el área central

        contentPane.setCenter(vista);
    } catch (Exception e) {
        e.printStackTrace();
        mostrarError("No se pudo cargar la lista de puntos.");
    }
}
    
private void mostrarGenerarReporte() {
    try {
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/co/edu/uptc/vista/generar_reporte_view.fxml"));
        VBox vista = loader.load();

        GenerarReporteViewController controller = loader.getController();
        controller.setControlador(controlador);

        contentPane.setCenter(vista);
    } catch (Exception e) {
        e.printStackTrace();
        mostrarError("No se pudo cargar la vista de generar reporte.");
    }
}
    
    private void mostrarAyuda() {
        mostrarMensajeEnDesarrollo("Ayuda");
    }
    
    private void mostrarAccesibilidad() {
        mostrarMensajeEnDesarrollo("Accesibilidad");
    }
    
    private void mostrarMensajeEnDesarrollo(String funcionalidad) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("En desarrollo");
        alert.setHeaderText(null);
        alert.setContentText("La funcionalidad '" + funcionalidad + "' está en desarrollo.");
        alert.showAndWait();
    }
    
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
