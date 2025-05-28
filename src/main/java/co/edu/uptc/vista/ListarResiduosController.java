package co.edu.uptc.vista;

import java.text.SimpleDateFormat;
import java.util.List;

import co.edu.uptc.controlador.ReciclajeControlador;
import co.edu.uptc.modelo.Residuo;
import co.edu.uptc.modelo.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ListarResiduosController implements Internacionalizable{

    @FXML private TableView<ResiduoRow> tablaResiduos;
    @FXML private TableColumn<ResiduoRow, String> colTipo;
    @FXML private TableColumn<ResiduoRow, Double> colCantidad;
    @FXML private TableColumn<ResiduoRow, String> colFecha;
    @FXML private Label lblResumenPuntos;
    @FXML private Label lblResumenCantidad;
    @FXML private Button btnCerrar;
    @FXML private Label lblMensaje;
    @FXML private Label lblTitulo;

    private ReciclajeControlador controlador;
    private Runnable onClose;

    public void setControlador(ReciclajeControlador controlador) {
        this.controlador = controlador;
        cargarDatos();
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    @FXML
    private void initialize() {
        colTipo.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTipo()));
        colCantidad.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getCantidad()));
        colFecha.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFecha()));
        btnCerrar.setOnAction(e -> {
            if (onClose != null) onClose.run();
        });
        tablaResiduos.setPlaceholder(new Label("No tiene residuos para listar.")); // [7][8]
    }

    private void cargarDatos() {
        lblMensaje.setText("");
        if (controlador == null) return;
        Usuario usuario = controlador.obtenerUsuarioActual();
        if (usuario == null) {
            lblMensaje.setText("No hay usuario autenticado.");
            return;
        }
        List<Residuo> residuos = usuario.getResiduos();
        if (residuos == null || residuos.isEmpty()) {
            tablaResiduos.setItems(FXCollections.observableArrayList());
            lblResumenPuntos.setText("Resumen: Puntos: 0");
            lblResumenCantidad.setText("Cantidad reciclada: 0 kg");
            return;
        }

        // Mostrar cada residuo como fila
        ObservableList<ResiduoRow> rows = FXCollections.observableArrayList();
        int totalPuntos = 0;
        double totalKg = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Residuo r : residuos) {
            rows.add(new ResiduoRow(
                    r.getTipoMaterial(),
                    r.getPeso(),
                    sdf.format(r.getFechaEntrega())
            ));
            totalPuntos += (int) (r.getPeso() * 10);
            totalKg += r.getPeso();
        }
        tablaResiduos.setItems(rows);
        lblResumenPuntos.setText("Resumen: Puntos: " + totalPuntos);
        lblResumenCantidad.setText("Cantidad reciclada: " + String.format("%.2f", totalKg) + " kg");
    }

    public static class ResiduoRow {
        private final String tipo;
        private final double cantidad;
        private final String fecha;
        public ResiduoRow(String tipo, double cantidad, String fecha) {
            this.tipo = tipo;
            this.cantidad = cantidad;
            this.fecha = fecha;
        }
        public String getTipo() { return tipo; }
        public double getCantidad() { return cantidad; }
        public String getFecha() { return fecha; }
    }

    @Override
    public void actualizarTextos() {
    // Encabezados de columnas
    colTipo.setText(AppContext.getBundle().getString("wasteType"));
    colCantidad.setText(AppContext.getBundle().getString("wasteAmount"));
    colFecha.setText(AppContext.getBundle().getString("wasteDate"));

    // Placeholder de la tabla
    tablaResiduos.setPlaceholder(new Label(AppContext.getBundle().getString("noWaste")));

    // Botón cerrar
    btnCerrar.setText(AppContext.getBundle().getString("closeList"));

    // Título
    lblTitulo.setText(AppContext.getBundle().getString("listWasteTitle"));

    // Vuelve a cargar los datos para actualizar los textos de resumen y mensajes
    cargarDatos();

        // Resúmenes (extrae los valores actuales)
        String puntos = "0";
        String kg = "0";
        if (lblResumenPuntos.getText() != null && lblResumenPuntos.getText().matches(".*\\d+.*")) {
            puntos = lblResumenPuntos.getText().replaceAll("\\D+", "");
        }
        if (lblResumenCantidad.getText() != null && lblResumenCantidad.getText().matches(".*\\d.*")) {
            kg = lblResumenCantidad.getText().replaceAll("[^\\d.,]", "");
        }
        lblResumenPuntos.setText(AppContext.getBundle().getString("summaryPoints").replace("{0}", puntos));
        lblResumenCantidad.setText(AppContext.getBundle().getString("summaryKg").replace("{0}", kg));
    
        // Mensajes de error
        if ("No hay usuario autenticado.".equals(lblMensaje.getText()) || "No authenticated user.".equals(lblMensaje.getText())) {
            lblMensaje.setText(AppContext.getBundle().getString("noUser"));
        } else if ("No tiene residuos para listar.".equals(lblMensaje.getText()) || "You have no waste to list.".equals(lblMensaje.getText())) {
            lblMensaje.setText(AppContext.getBundle().getString("noWaste"));
        }
    }
    
}
