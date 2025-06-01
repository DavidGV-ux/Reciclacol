package co.edu.uptc.vista;

import java.io.IOException;

import co.edu.uptc.controlador.ReciclajeControlador;
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

    private ReciclajeControlador controlador;

    @FXML
    private void initialize() {
        configurarComboIdioma();
        cargarLogo();

        btnInicio.setOnAction(e -> cerrarSesion());
        btnAyuda.setOnAction(e -> mostrarAyuda());
        btnRegistrarResiduos.setOnAction(e -> mostrarRegistrarResiduos());
        btnListarResiduos.setOnAction(e -> mostrarListarResiduos());
        btnConsultarPuntos.setOnAction(e -> mostrarConsultarPuntos());
        btnGenerarReporte.setOnAction(e -> mostrarGenerarReporte());

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
    
            // Reabrir la última opción seleccionada tras recargar el menú
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
        alert.setTitle(AppContext.getBundle().getString("login"));
        alert.setHeaderText(AppContext.getBundle().getString("help"));
        alert.setContentText(AppContext.getBundle().getString("fact"));

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/co/edu/uptc/vista/inicio_view.fxml"),
                    AppContext.getBundle()
                );
                Parent root = loader.load();

                InicioViewController controller = loader.getController();
                controller.setControlador(controlador);

                Scene scene = new Scene(root, 1440, 1024);
                Stage stage = (Stage) btnInicio.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle(AppContext.getBundle().getString("welcome"));
            } catch (Exception e) {
                e.printStackTrace();
                mostrarError("Error al cerrar sesión");
            }
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
    
}
