package co.edu.uptc.vista;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import co.edu.uptc.controlador.ReciclajeControlador;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

public class MenuUsuarioViewController implements Internacionalizable{

    @FXML private Button btnInicio;
    @FXML private Button btnRegistrarResiduos;
    @FXML private Button btnListarResiduos;
    @FXML private Button btnConsultarPuntos;
    @FXML private Button btnGenerarReporte;
    @FXML private Button btnAyuda;
    @FXML private ComboBox<String> comboIdioma;
    @FXML private Label lblNombreUsuario;
    @FXML private BorderPane contentPane;
    @FXML private ImageView logoImageView;
    @FXML private ComboBox<String> comboAccesibilidad;
    @FXML private Button btnActualizarUsuario;
    private MenuUsuarioViewController menuUsuarioViewController;


    private ReciclajeControlador controlador;   
    private HostServices hostServices;

    @FXML
    private void initialize() {
        configurarComboIdioma();
        cargarLogo();

        btnInicio.setOnAction(e -> confirmarCerrarSesion());
        btnRegistrarResiduos.setOnAction(e -> mostrarRegistrarResiduos());
        btnListarResiduos.setOnAction(e -> mostrarListarResiduos());
        btnConsultarPuntos.setOnAction(e -> mostrarConsultarPuntos());
        btnGenerarReporte.setOnAction(e -> mostrarGenerarReporte());
        btnActualizarUsuario.setOnAction(e -> mostrarActualizarUsuario());


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
                recargarVista(); // <-- Esto recarga todo el menú con el idioma correcto
            }
        });
    }
    
    
    
    private void notificarCambioIdiomaEnVistaActual() {
        if (contentPane.getCenter() != null) {
            Object controller = contentPane.getCenter().getUserData();
            if (controller instanceof Internacionalizable) {
                ((Internacionalizable) controller).actualizarTextos();
            }
        }
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
                mostrarError("Idioma no soportado");
        }
    }

    private void recargarVista() {
        try {
            Stage stage = (Stage) comboIdioma.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/co/edu/uptc/vista/menu_usuario_view.fxml"),
                AppContext.getBundle()
            );
            Parent root = loader.load();
            MenuUsuarioViewController controller = loader.getController();
            controller.setControlador(this.controlador);
    
            controller.ultimaOpcion = this.ultimaOpcion;
            controller.abrirUltimaOpcion();
    
            stage.setScene(new Scene(root, 1440, 1024));
        } catch (IOException e) {
            mostrarError("No se pudo actualizar el idioma: " + e.getMessage());
        }
    }
    
    private void abrirUltimaOpcion() {
        switch (ultimaOpcion) {
            case REGISTRAR_RESIDUOS:
                mostrarRegistrarResiduos();
                break;
            case LISTAR_RESIDUOS:
                mostrarListarResiduos();
                break;
            case CONSULTAR_PUNTOS:
                mostrarConsultarPuntos();
                break;
            case GENERAR_REPORTE:
                mostrarGenerarReporte();
                break;
            default:
                // No hace nada, se queda en la pantalla principal del menú
                break;
        }
    }
    

    private void cargarLogo() {
        String imagePath = "/co/edu/uptc/imagenes/opciones.png";
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
        if (controlador != null) {
            lblNombreUsuario.setText(controlador.obtenerUsuarioActual().getNombreCompleto()); // O el método correcto
        }
    }
    

    private void actualizarDatosUsuario() {
        if (controlador != null) {
            lblNombreUsuario.setText(controlador.obtenerUsuarioPorNombre(""));
        }
    }
    
    public void mostrarMenuPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/co/edu/uptc/vista/menu_usuario_view.fxml"),
                AppContext.getBundle()
            );
            Parent menuRoot = loader.load();
            MenuUsuarioViewController controller = loader.getController();
            controller.setControlador(this.controlador); // <-- Mantiene el usuario y lógica
            Stage stage = (Stage) contentPane.getScene().getWindow();
            stage.getScene().setRoot(menuRoot);
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al cargar el menú principal");
        }
    }

    @FXML
private void confirmarCerrarSesion() {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle(AppContext.getBundle().getString("home")); // Título de la ventana
    alert.setHeaderText(null);
    alert.setContentText(AppContext.getBundle().getString("cerrarSesionConfirm")); // Mensaje del properties

    Optional<ButtonType> result = alert.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {
        cerrarSesionYVolverInicio();
    }
}

private void cerrarSesionYVolverInicio() {
    try {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/co/edu/uptc/vista/inicio_view.fxml"),
            AppContext.getBundle()
        );
        Parent root = loader.load();
        InicioViewController inicioController = loader.getController();
        inicioController.setControlador(AppContext.getReciclajeControlador());
        inicioController.setHostServices(this.hostServices); 
        Stage stage = (Stage) btnInicio.getScene().getWindow();
        stage.setScene(new Scene(root, 1440, 1024));
        stage.setTitle(AppContext.getBundle().getString("welcome"));
        stage.show();
        // Aquí puedes limpiar los datos de sesión si es necesario
    } catch (Exception e) {
        e.printStackTrace();
        mostrarError("Error al cerrar sesión");
    }
}


    private void mostrarRegistrarResiduos() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/co/edu/uptc/vista/registrar_residuo_form.fxml"),
                AppContext.getBundle()
            );
            VBox form = loader.load();
    
            RegistrarResiduoController formController = loader.getController();
            formController.setControlador(controlador);
    
            // Guarda el controlador en el nodo para acceso posterior
            form.setUserData(formController);
    
            contentPane.setCenter(form);
            ultimaOpcion = OpcionMenu.REGISTRAR_RESIDUOS;
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("No se pudo cargar el formulario de registro de residuo.");
        }
    }
    

    private void mostrarListarResiduos() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/co/edu/uptc/vista/listar_residuos_view.fxml"),
                AppContext.getBundle()
            );
            VBox vista = loader.load();

            ListarResiduosController controller = loader.getController();
            controller.setControlador(controlador);
            controller.setOnClose(() -> contentPane.setCenter(null));

            contentPane.setCenter(vista);
            ultimaOpcion = OpcionMenu.LISTAR_RESIDUOS;
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("No se pudo cargar la lista de residuos.");
        }
    }

    private void mostrarConsultarPuntos() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/co/edu/uptc/vista/listar_puntos_view.fxml"),
                AppContext.getBundle()
            );
            VBox vista = loader.load();

            ListarPuntosController controller = loader.getController();
            controller.setControlador(controlador);
            controller.setOnClose(() -> contentPane.setCenter(null));

            contentPane.setCenter(vista);
            ultimaOpcion = OpcionMenu.CONSULTAR_PUNTOS;
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("No se pudo cargar la lista de puntos.");
        }
    }

    private void mostrarGenerarReporte() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/co/edu/uptc/vista/generar_reporte_view.fxml"),
                AppContext.getBundle()
            );
            VBox vista = loader.load();

            GenerarReporteViewController controller = loader.getController();
            controller.setControlador(controlador);

            contentPane.setCenter(vista);
            ultimaOpcion = OpcionMenu.GENERAR_REPORTE;
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("No se pudo cargar la vista de generar reporte.");
        }
    }

    private void mostrarAyuda() {
        mostrarMensajeEnDesarrollo(AppContext.getBundle().getString("help"));
    }

    private void mostrarAccesibilidad() {
        mostrarMensajeEnDesarrollo(AppContext.getBundle().getString("accessibility"));
    }

    private void mostrarMensajeEnDesarrollo(String funcionalidad) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(AppContext.getBundle().getString("about"));
        alert.setHeaderText(null);
        alert.setContentText(AppContext.getBundle().getString("fact") + "\n(" + funcionalidad + ")");
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(AppContext.getBundle().getString("help"));
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @Override
    public void actualizarTextos() {
    }

    private enum OpcionMenu {
        REGISTRAR_RESIDUOS,
        LISTAR_RESIDUOS,
        CONSULTAR_PUNTOS,
        GENERAR_REPORTE,
        NINGUNA
    }
    
    private OpcionMenu ultimaOpcion = OpcionMenu.NINGUNA;

    private void aplicarAccesibilidad(String opcion) {
        Scene scene = comboAccesibilidad.getScene();
        if (scene != null) {
            AccesibilidadViewController.aplicarEstilo(scene, opcion);
        }
    }
    
    private void mostrarActualizarUsuario() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/co/edu/uptc/vista/actualizar_usuario_form.fxml"),
                AppContext.getBundle()
            );
            VBox form = loader.load();
            ActualizarUsuarioController formController = loader.getController();
            if (formController == null) {
                throw new IllegalStateException("El controlador de actualizar_usuario_form.fxml no fue inyectado. Verifica fx:controller en el FXML.");
            }
            formController.setControlador(this.controlador);
            formController.setMenuUsuarioViewController(this); // <--- Agrega esto
            contentPane.setCenter(form);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("No se pudo cargar el formulario de actualización de usuario.");
        }
    }    
    
    @FXML
    private void handleAyuda() {
        try {
            Path tempDir = Files.createTempDirectory("ayuda_menu_usuario");
            Path htmlFile = tempDir.resolve("MenuUsuario.html");
            try (var in = getClass().getResourceAsStream("/co/edu/uptc/Ayuda/MenuUsuario/MenuUsuario.html")) {
                if (in == null) {
                    mostrarError("No se encontró el recurso HTML de ayuda.");
                    return;
                }
                Files.copy(in, htmlFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
    
            try (var in = getClass().getResourceAsStream("/co/edu/uptc/Ayuda/MenuUsuario/MenuUsuario.css")) {
                if (in != null) {
                    Files.copy(in, tempDir.resolve("MenuUsuario.css"), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
            }
    
            String[] imagenes = {
                "AccesibilidadeIdioma.png",
                "ActualizarDatos.png",
                "CerrarListaPuntos.png",
                "CerrarListaResiduos.png",
                "ConsultarPuntosPermiteVerLosPuntosPorTipoDeMaterialYFechaDelUltimoAgregadoDeCadaUno.png",
                "GenerarReportePermiteGenerarUnPDFConLasEstadisticasPersonalesYgrupalesEnreciclajeGuardarloenElPcYunaVistaPrevia.png",
                "GuardarActualizacionDatos.png",
                "GuardarLosResiduosAAgregar.png",
                "ListarResiduosPermiteListarLosResiduosCantidadenKgyLaFechaEnqueseagrego.png",
                "MinimizarOCerrar.png",
                "RegistrarResiduosPermiteAgregarResiduosYmodificaroeliminarselecciones.png",
                "SeleccionarLapizParaEditarDatoDeCasilla.png",
                "VistaGeneralMenuUsuario.png",
                "VolverAlInicioYCerrarSesion.png",
                "VolverMenuInicial.png"
            };
            for (String img : imagenes) {
                try (var in = getClass().getResourceAsStream("/co/edu/uptc/Ayuda/MenuUsuario/" + img)) {
                    if (in != null) {
                        Files.copy(in, tempDir.resolve(img), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
    
            if (hostServices != null) {
                hostServices.showDocument(htmlFile.toUri().toString());
            } else {
                mostrarError("No se pudo obtener HostServices para abrir el navegador.");
            }
    
            htmlFile.toFile().deleteOnExit();
            tempDir.toFile().deleteOnExit();
    
        } catch (Exception e) {
            mostrarError("No se pudo abrir la ayuda en el navegador: " + e.getMessage());
        }
    }
    

public void setHostServices(HostServices hostServices) {
    this.hostServices = hostServices;
}
}
