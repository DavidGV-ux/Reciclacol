package co.edu.uptc.vista;

import co.edu.uptc.controlador.ReciclajeControlador;
import co.edu.uptc.modelo.Residuo;
import co.edu.uptc.modelo.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.text.SimpleDateFormat;
import java.util.List;

public class ListarPuntosController {

    @FXML private TableView<ResiduoRow> tablaResiduos;
    @FXML private TableColumn<ResiduoRow, String> colTipo;
    @FXML private TableColumn<ResiduoRow, Integer> colPuntos;
    @FXML private TableColumn<ResiduoRow, String> colFecha;
    @FXML private Label lblPuntosTotales;
    @FXML private Button btnCerrar;
    @FXML private Label lblMensaje;

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
        colPuntos.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getPuntos()));
        colFecha.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFechaUltimo()));
        btnCerrar.setOnAction(e -> {
            if (onClose != null) onClose.run();
        });
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
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText("No tiene puntos para listar.");
            tablaResiduos.setItems(FXCollections.observableArrayList());
            lblPuntosTotales.setText("Puntos totales: 0");
            return;
        }

        // Agrupar por tipo de residuo, sumar puntos y mostrar fecha del último
        java.util.Map<String, ResiduoRow> resumen = new java.util.HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Residuo r : residuos) {
            String tipo = r.getTipoMaterial();
            int puntos = (int) (r.getPeso() * 10);
            String fecha = sdf.format(r.getFechaEntrega());
            if (!resumen.containsKey(tipo)) {
                resumen.put(tipo, new ResiduoRow(tipo, puntos, fecha));
            } else {
                ResiduoRow row = resumen.get(tipo);
                row.puntos += puntos;
                // Actualiza la fecha si es más reciente
                if (fecha.compareTo(row.fechaUltimo) > 0) row.fechaUltimo = fecha;
            }
        }
        ObservableList<ResiduoRow> rows = FXCollections.observableArrayList(resumen.values());
        tablaResiduos.setItems(rows);

        int total = resumen.values().stream().mapToInt(r -> r.puntos).sum();
        lblPuntosTotales.setText("Puntos totales: " + total);
    }

    public static class ResiduoRow {
        private String tipo;
        private int puntos;
        private String fechaUltimo;

        public ResiduoRow(String tipo, int puntos, String fechaUltimo) {
            this.tipo = tipo;
            this.puntos = puntos;
            this.fechaUltimo = fechaUltimo;
        }
        public String getTipo() { return tipo; }
        public int getPuntos() { return puntos; }
        public String getFechaUltimo() { return fechaUltimo; }
    }
}
