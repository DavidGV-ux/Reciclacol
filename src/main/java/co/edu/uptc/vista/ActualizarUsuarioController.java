package co.edu.uptc.vista;

import co.edu.uptc.controlador.ReciclajeControlador;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ActualizarUsuarioController {
    // Campos de texto
    @FXML private TextField txtPrimerNombre;
    @FXML private TextField txtSegundoNombre;
    @FXML private TextField txtPrimerApellido;
    @FXML private TextField txtSegundoApellido;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;

    // Botones de edición
    @FXML private Button btnEditarPrimerNombre;
    @FXML private Button btnEditarSegundoNombre;
    @FXML private Button btnEditarPrimerApellido;
    @FXML private Button btnEditarSegundoApellido;
    @FXML private Button btnEditarTelefono;
    @FXML private Button btnEditarCorreo;

    // Botones principales
    @FXML private Button btnGuardar;
    @FXML private Button btnVolver;

    private ReciclajeControlador controlador;

    public void setControlador(ReciclajeControlador controlador) {
        this.controlador = controlador;
        cargarDatosUsuario();
    }

    private void cargarDatosUsuario() {
        var usuario = controlador.obtenerUsuarioActual();
        txtPrimerNombre.setText(usuario.getPrimerNombre());
        txtSegundoNombre.setText(usuario.getSegundoNombre());
        txtPrimerApellido.setText(usuario.getPrimerApellido());
        txtSegundoApellido.setText(usuario.getSegundoApellido());
        txtTelefono.setText(String.valueOf(usuario.getTelefono()));
        txtCorreo.setText(usuario.getCorreo());

        // Bloquear todos los campos al cargar
        bloquearCampos();
    }

    @FXML
    private void initialize() {
        // Botones de edición: solo desbloquean su campo correspondiente
        btnEditarPrimerNombre.setOnAction(e -> {
            bloquearCampos();
            txtPrimerNombre.setEditable(true);
            txtPrimerNombre.requestFocus();
        });
        btnEditarSegundoNombre.setOnAction(e -> {
            bloquearCampos();
            txtSegundoNombre.setEditable(true);
            txtSegundoNombre.requestFocus();
        });
        btnEditarPrimerApellido.setOnAction(e -> {
            bloquearCampos();
            txtPrimerApellido.setEditable(true);
            txtPrimerApellido.requestFocus();
        });
        btnEditarSegundoApellido.setOnAction(e -> {
            bloquearCampos();
            txtSegundoApellido.setEditable(true);
            txtSegundoApellido.requestFocus();
        });
        btnEditarTelefono.setOnAction(e -> {
            bloquearCampos();
            txtTelefono.setEditable(true);
            txtTelefono.requestFocus();
        });
        btnEditarCorreo.setOnAction(e -> {
            bloquearCampos();
            txtCorreo.setEditable(true);
            txtCorreo.requestFocus();
        });

        // Botón guardar
        btnGuardar.setOnAction(e -> guardarCambios());

        // Botón volver
        btnVolver.setOnAction(e -> volverPantallaAnterior());
    }

    private void bloquearCampos() {
        txtPrimerNombre.setEditable(false);
        txtSegundoNombre.setEditable(false);
        txtPrimerApellido.setEditable(false);
        txtSegundoApellido.setEditable(false);
        txtTelefono.setEditable(false);
        txtCorreo.setEditable(false);
    }

    private void guardarCambios() {
        try {
            var usuario = controlador.obtenerUsuarioActual();
            usuario.setPrimerNombre(txtPrimerNombre.getText());
            usuario.setSegundoNombre(txtSegundoNombre.getText());
            usuario.setPrimerApellido(txtPrimerApellido.getText());
            usuario.setSegundoApellido(txtSegundoApellido.getText());
            usuario.setTelefono(Integer.parseInt(txtTelefono.getText()));
            usuario.setCorreo(txtCorreo.getText());
            controlador.guardarCambios();

            bloquearCampos();

            mostrarAlerta("Éxito", "Los datos se han actualizado correctamente.", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException ex) {
            mostrarAlerta("Error", "El teléfono debe ser un número válido.", Alert.AlertType.ERROR);
        } catch (Exception ex) {
            mostrarAlerta("Error", "Ha ocurrido un error al guardar los cambios.", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void volverPantallaAnterior() {
        if (menuUsuarioViewController != null) {
            menuUsuarioViewController.mostrarMenuPrincipal();
        }
    }
    

    private MenuUsuarioViewController menuUsuarioViewController;

public void setMenuUsuarioViewController(MenuUsuarioViewController controller) {
    this.menuUsuarioViewController = controller;
}

}
