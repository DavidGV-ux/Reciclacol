package co.edu.uptc.vista;

import java.sql.Date;

import co.edu.uptc.controlador.ReciclajeControlador;
import co.edu.uptc.modelo.Residuo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class RegistrarResiduoController implements Internacionalizable{

    @FXML private ComboBox<String> comboTipoMaterial;
    @FXML private TextField txtPeso;
    @FXML private Button btnAgregar;
    @FXML private TableView<Residuo> tablaResumen;
    @FXML private TableColumn<Residuo, String> colMaterial;
    @FXML private TableColumn<Residuo, Double> colPeso;
    @FXML private Button btnModificar;
    @FXML private Button btnEliminar;
    @FXML private Button btnGuardar;
    @FXML private Label lblMensaje;
    @FXML private Label lblTitulo;
    @FXML private Label lblMaterial;
    @FXML private Label lblPeso;


    private ReciclajeControlador controlador;
    private final ObservableList<Residuo> residuosTemp = FXCollections.observableArrayList();

    public void setControlador(ReciclajeControlador controlador) {
        this.controlador = controlador;
    }

    @FXML
    private void initialize() {
        comboTipoMaterial.getItems().addAll("Plástico", "Vidrio", "Cartón", "Metal");
        colMaterial.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTipoMaterial()));
        colPeso.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getPeso()));
        tablaResumen.setItems(residuosTemp);

        btnAgregar.setOnAction(e -> agregarResiduoTemp());
        btnEliminar.setOnAction(e -> eliminarResiduo());
        btnModificar.setOnAction(e -> modificarResiduo());
        btnGuardar.setOnAction(e -> guardarResiduos());
        lblMensaje.setText("");
    }

    private void agregarResiduoTemp() {
        String tipo = comboTipoMaterial.getValue();
        String pesoStr = txtPeso.getText().trim();

        lblMensaje.setStyle("-fx-text-fill: red;");
        if (tipo == null || tipo.isEmpty()) {
            lblMensaje.setText("Seleccione un tipo de material.");
            return;
        }
        double peso;
        try {
            peso = Double.parseDouble(pesoStr.replace(",", "."));
            if (peso <= 0) {
                lblMensaje.setText("El peso debe ser mayor a cero.");
                return;
            }
        } catch (NumberFormatException ex) {
            lblMensaje.setText("Peso inválido.");
            return;
        }

        residuosTemp.add(new Residuo(tipo, peso, new Date(System.currentTimeMillis())));
        txtPeso.clear();
        comboTipoMaterial.getSelectionModel().clearSelection();
        lblMensaje.setStyle("-fx-text-fill: green;");
        lblMensaje.setText("Residuo agregado al resumen.");
    }

    private void eliminarResiduo() {
        Residuo seleccionado = tablaResumen.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            residuosTemp.remove(seleccionado);
            lblMensaje.setStyle("-fx-text-fill: green;");
            lblMensaje.setText("Residuo eliminado.");
        } else {
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText("Seleccione un residuo para eliminar.");
        }
    }

    private void modificarResiduo() {
        Residuo seleccionado = tablaResumen.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            comboTipoMaterial.setValue(seleccionado.getTipoMaterial());
            txtPeso.setText(String.valueOf(seleccionado.getPeso()));
            residuosTemp.remove(seleccionado);
            lblMensaje.setText("Edite y vuelva a agregar el residuo.");
        } else {
            lblMensaje.setText("Seleccione un residuo para modificar.");
        }
    }

    private void guardarResiduos() {
        if (residuosTemp.isEmpty()) {
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText("No hay residuos para guardar.");
            return;
        }
        boolean exito = true;
        for (Residuo r : residuosTemp) {
            if (!controlador.agregarResiduo(r.getTipoMaterial(), r.getPeso(), r.getFechaEntrega())) {
                exito = false;
            }
        }
        if (exito) {
            residuosTemp.clear();
            lblMensaje.setStyle("-fx-text-fill: green;");
            lblMensaje.setText("Todos los residuos fueron guardados exitosamente.");
        } else {
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText("Error al guardar algunos residuos.");
        }
    }

    @Override
    public void actualizarTextos() {
        lblTitulo.setText(AppContext.getBundle().getString("wasteWelcome"));
        lblMaterial.setText(AppContext.getBundle().getString("materialType"));
        lblPeso.setText(AppContext.getBundle().getString("weight"));
        comboTipoMaterial.getItems().setAll(
            AppContext.getBundle().getString("plastic"),
            AppContext.getBundle().getString("glass"),
            AppContext.getBundle().getString("cardboard"),
            AppContext.getBundle().getString("metal")
        );
        comboTipoMaterial.setPromptText(AppContext.getBundle().getString("selectMaterial"));
        btnAgregar.setText(AppContext.getBundle().getString("add"));
        btnModificar.setText(AppContext.getBundle().getString("modify"));
        btnEliminar.setText(AppContext.getBundle().getString("delete"));
        btnGuardar.setText(AppContext.getBundle().getString("save"));
        colMaterial.setText(AppContext.getBundle().getString("materialColumn"));
        colPeso.setText(AppContext.getBundle().getString("weightColumn"));
    }
    

}
