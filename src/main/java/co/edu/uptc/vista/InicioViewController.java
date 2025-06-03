package co.edu.uptc.vista;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.HostServices;
import javafx.util.Duration;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;



public class InicioViewController {

    @FXML private Button btnIniciarSesion;
    @FXML private Button btnRegistro;
    @FXML private Button btnAcercaDe;
    @FXML private Button btnAccesibilidad;
    @FXML private Button btnAyuda;
    @FXML private ImageView logoImageView;
    @FXML private ComboBox<String> comboIdioma;
    @FXML private ComboBox<String> comboAccesibilidad;
    @FXML private Label lblDatoCurioso;

    private ReciclajeControlador controlador;
    private Timeline timeline;
    private int indiceActual = 0;
    private HostServices hostServices;
    
    private final java.util.List<String> datosCuriosos = java.util.Arrays.asList(
        "¿Sabías que reciclar una lata de aluminio ahorra energía para 3 horas de TV?",
        "El vidrio puede reciclarse infinitamente sin perder calidad.",
        "Reciclar papel salva árboles y ahorra agua/energía.",
        "1 tonelada de plástico reciclado = 700 kg de petróleo ahorrado.",
        "Reciclar una botella plástica = energía para un foco de 60W por 6 horas.",
        "El 75% de la basura es reciclable, pero solo reciclamos el 30%.",
        "1 tonelada de papel reciclado salva 17 árboles y 26,500 litros de agua."
    );

    public InicioViewController() {}

    @FXML
    private void initialize() {
        comboIdioma.getItems().addAll("Español", "English");
        comboIdioma.setValue(AppContext.getCurrentLocale().getLanguage().equals("en") ? "English" : "Español");
        btnAcercaDe.setOnAction(e -> handleAcercaDe());
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

        // ---- DATO CURIOSO ----
        if (lblDatoCurioso != null && !datosCuriosos.isEmpty()) {
            lblDatoCurioso.setText(datosCuriosos.get(0));
            timeline = new Timeline(
                new KeyFrame(Duration.seconds(30), e -> {
                    indiceActual = (indiceActual + 1) % datosCuriosos.size();
                    lblDatoCurioso.setText(datosCuriosos.get(indiceActual));
                })
            );
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
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
            loginController.setHostServices(this.hostServices);

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
            registroController.setHostServices(this.hostServices);

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
    
        @FXML
        private void handleAcercaDe() {
            String titulo = AppContext.getBundle().getString("aboutTitle");
            String version = AppContext.getBundle().getString("aboutVersion");
            String licencia = AppContext.getBundle().getString("aboutLicense");
            String contacto = AppContext.getBundle().getString("aboutContact");
    
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(titulo);
            alert.setHeaderText(version);
            alert.setContentText(licencia + "\n\n" + contacto);
            alert.showAndWait();
        }

        @FXML
        private void handleAyuda() {
            try {
                // 1. Crear un directorio temporal para la ayuda
                Path tempDir = Files.createTempDirectory("ayuda_inicio");
                // 2. Copiar el archivo HTML principal
                Path htmlFile = tempDir.resolve("Inicio.html");
                try (var in = getClass().getResourceAsStream("/co/edu/uptc/Ayuda/Inicio/Inicio.html")) {
                    if (in == null) {
                        mostrarError("No se encontró el archivo de ayuda", "No se encontró el recurso HTML.");
                        return;
                    }
                    Files.copy(in, htmlFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
        
                // 3. Copiar el CSS
                try (var in = getClass().getResourceAsStream("/co/edu/uptc/Ayuda/Inicio/inicio.css")) {
                    if (in != null) {
                        Files.copy(in, tempDir.resolve("inicio.css"), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    }
                }
        
                // 4. Copiar todas las imágenes que usa el tutorial
                String[] imagenes = {
                    "Accesibilidad.png",
                    "AcercaDe.png",
                    "Cerrar.png",
                    "DatosCuriosos.png",
                    "Idioma.png",
                    "Inicio.png",
                    "InicioGeneral.png",
                    "InicioUsuario.png",
                    "Minimizar.png",
                    "Registro.png",
                    "RegistroUsuario.png"
                };
                for (String img : imagenes) {
                    try (var in = getClass().getResourceAsStream("/co/edu/uptc/Ayuda/Inicio/" + img)) {
                        if (in != null) {
                            Files.copy(in, tempDir.resolve(img), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                }
        
                // 5. Abrir el archivo HTML en el navegador usando HostServices
                if (hostServices != null) {
                    hostServices.showDocument(htmlFile.toUri().toString());
                } else {
                    mostrarError("Error", "No se pudo obtener HostServices para abrir el navegador.");
                }
        
                // 6. Opcional: marcar archivos temporales para borrar al salir
                htmlFile.toFile().deleteOnExit();
                tempDir.toFile().deleteOnExit();
        
            } catch (Exception e) {
                mostrarError("No se pudo abrir la ayuda en el navegador", e.getMessage());
            }
        }
        
public void setHostServices(HostServices hostServices) {
    this.hostServices = hostServices;
}


    }

