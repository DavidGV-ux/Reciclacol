package co.edu.uptc.vista;

import java.util.ResourceBundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import co.edu.uptc.controlador.ReciclajeControlador;
import co.edu.uptc.servicio.CredencialesRecordadas;
import javafx.application.HostServices;
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
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.application.HostServices;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

public class InicioSesionViewController {

    @FXML private TextField txtCorreoID;
    @FXML private PasswordField pfContrasena;
    @FXML private Button btnIniciarSesion;
    @FXML private Button btnVolver;
    @FXML private ComboBox<String> comboIdioma;
    @FXML private Hyperlink linkOlvidasteContrasena;
    @FXML private Button btnAyuda;
    @FXML private ImageView logoImageView;
    @FXML private ComboBox<String> comboAccesibilidad;
    @FXML private Hyperlink linkRegistrarse;
    @FXML private CheckBox chkRecordar;
    

    private static final String RECORDAR_PATH = "proyecto_final_fx/src/main/resources/datos/recordar.json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private HostServices hostServices;

    private ReciclajeControlador controlador;

    @FXML
    private void initialize() {
        configurarComboIdioma();
        cargarLogo();
        cargarDatosRecordar();
        btnVolver.setOnAction(e -> volverInicio());
        if (controlador == null) {
            controlador = AppContext.getReciclajeControlador();
        }

        linkOlvidasteContrasena.setOnAction(e -> mostrarRecuperarContrasena());
        btnIniciarSesion.setOnAction(e -> iniciarSesion());
        btnVolver.setOnAction(e -> volverInicio());
        linkRegistrarse.setOnAction(e -> irARegistro());

        comboAccesibilidad.getItems().addAll("Normal", "Alto Contraste", "Letra Grande");
        comboAccesibilidad.setValue("Normal");
        comboAccesibilidad.valueProperty().addListener((obs, oldVal, newVal) -> {
            aplicarAccesibilidad(newVal);
        });

        chkRecordar.setOnAction(e -> {
            if (!chkRecordar.isSelected()) {
                eliminarDatosRecordar();
            }
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

    private void irARegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/co/edu/uptc/vista/registro_view.fxml"),
                AppContext.getBundle()
            );
            Parent root = loader.load();
            RegistroViewController registroController = loader.getController();
            registroController.setControlador(controlador);
            registroController.setHostServices(this.hostServices);
            Stage stage = (Stage) linkRegistrarse.getScene().getWindow();
            stage.setScene(new Scene(root, 1440, 1024));
            stage.setTitle(AppContext.getBundle().getString("register"));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo cargar la vista de registro");
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
            controller.setHostServices(this.hostServices);
            stage.setScene(new Scene(root, 1440, 1024));
        } catch (IOException e) {
            mostrarError("Error", "No se pudo actualizar el idioma: " + e.getMessage());
        }
    }

    private void cargarLogo() {
        String imagePath = "/co/edu/uptc/imagenes/login.png";
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
        if (controlador == null) {
            this.controlador = AppContext.getReciclajeControlador();
        } else {
            this.controlador = controlador;
        }
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
            if (chkRecordar.isSelected()) {
                guardarDatosRecordar(correoID, contrasena);
            } else {
                eliminarDatosRecordar();
            }
        
            // ...ventana de carga y continuar...
            Stage loadingStage = new Stage();
            loadingStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            loadingStage.initStyle(javafx.stage.StageStyle.UNDECORATED);
        
            javafx.scene.control.ProgressIndicator progressIndicator = new javafx.scene.control.ProgressIndicator();
            progressIndicator.setPrefSize(100, 100);
        
            javafx.scene.control.Label mensajeExito = new javafx.scene.control.Label("Inicio exitoso");
            mensajeExito.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #388e3c; -fx-padding: 14 0 0 0;");
        
            javafx.scene.layout.VBox loadingVBox = new javafx.scene.layout.VBox(10);
            loadingVBox.setAlignment(javafx.geometry.Pos.CENTER);
            loadingVBox.setPadding(new javafx.geometry.Insets(20));
            loadingVBox.getChildren().addAll(progressIndicator, mensajeExito);
        
            loadingStage.setScene(new javafx.scene.Scene(loadingVBox, 240, 160));
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
            menuController.setControlador(controlador); // <-- ESTA LÍNEA ES CLAVE
            menuController.setHostServices(this.hostServices);

            Stage stage = (Stage) btnIniciarSesion.getScene().getWindow();
            stage.setScene(new Scene(root, 1440, 1024));
            stage.setTitle(AppContext.getBundle().getString("welcome"));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo cargar el menú de usuario");
        }
    }
    
    @FXML
    private void volverInicio() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/co/edu/uptc/vista/inicio_view.fxml"),
                AppContext.getBundle()
            );
            Parent root = loader.load();
            InicioViewController inicioController = loader.getController();
            inicioController.setControlador(AppContext.getReciclajeControlador());
            inicioController.setHostServices(this.hostServices);
    
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root, 1440, 1024));
            stage.setTitle(AppContext.getBundle().getString("welcome"));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error", "No se pudo cargar la vista de inicio de sesión");
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

    private void guardarDatosRecordar(String usuario, String contrasena) {
        CredencialesRecordadas datos = new CredencialesRecordadas(usuario, contrasena);
        try (Writer writer = new FileWriter(RECORDAR_PATH)) {
            gson.toJson(datos, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

private void eliminarDatosRecordar() {
    File file = new File(RECORDAR_PATH);
    if (file.exists()) {
        file.delete();
    }
}

private void cargarDatosRecordar() {
    File file = new File(RECORDAR_PATH);
    if (file.exists()) {
        try (Reader reader = new FileReader(file)) {
            CredencialesRecordadas datos = gson.fromJson(reader, CredencialesRecordadas.class);
            if (datos != null) {
                txtCorreoID.setText(datos.getUsuario());
                pfContrasena.setText(datos.getContrasena());
                chkRecordar.setSelected(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

@FXML
private void handleAyuda() {
    try {
        // 1. Crear un directorio temporal para la ayuda
        Path tempDir = Files.createTempDirectory("ayuda_inicio_sesion");
        // 2. Copiar el archivo HTML principal
        Path htmlFile = tempDir.resolve("InicioSesion.html");
        try (var in = getClass().getResourceAsStream("/co/edu/uptc/Ayuda/InicioSesion/InicioSesion.html")) {
            if (in == null) {
                mostrarError("No se encontró el archivo de ayuda", "No se encontró el recurso HTML.");
                return;
            }
            Files.copy(in, htmlFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }

        // 3. Copiar el CSS
        try (var in = getClass().getResourceAsStream("/co/edu/uptc/Ayuda/InicioSesion/InicioSesion.css")) {
            if (in != null) {
                Files.copy(in, tempDir.resolve("InicioSesion.css"), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        }

        // 4. Copiar todas las imágenes que usa el tutorial
        String[] imagenes = {
            "Accesibilidad e idioma.png",
            "cerrar o minimizar.png",
            "CodigoRecibido.png",
            "Credenciales_NumeroDocumento_Contrasena.png",
            "IngresaNumeroDocumentoParaEnviarCodigoAcorreoEnlazado.png",
            "Ingresar_paravalidaryentrar.png",
            "NoTehasregistrado_hazlo_hipervinculo.png",
            "OlvidoContarsena.png",
            "RecordarDatos.png",
            "SeleccionarIdioma.png",
            "VistaGeneralClave.png",
            "VistaGeneral.png",
            "VolverPantallaDeInicio.png"
        };
        for (String img : imagenes) {
            try (var in = getClass().getResourceAsStream("/co/edu/uptc/Ayuda/InicioSesion/" + img)) {
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
