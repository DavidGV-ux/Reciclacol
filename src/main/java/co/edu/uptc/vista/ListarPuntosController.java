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

public class ListarPuntosController implements Internacionalizable{

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
            lblMensaje.setText(AppContext.getBundle().getString("noUser"));
            return;
        }
        List<Residuo> residuos = usuario.getResiduos();
        if (residuos == null || residuos.isEmpty()) {
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText(AppContext.getBundle().getString("noPoints"));
            tablaResiduos.setItems(FXCollections.observableArrayList());
            lblPuntosTotales.setText(AppContext.getBundle().getString("totalPoints").replace("{0}", "0"));
            return;
        }
    
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
                if (fecha.compareTo(row.fechaUltimo) > 0) row.fechaUltimo = fecha;
            }
        }
        ObservableList<ResiduoRow> rows = FXCollections.observableArrayList(resumen.values());
        tablaResiduos.setItems(rows);
    
        int total = resumen.values().stream().mapToInt(r -> r.puntos).sum();
        lblPuntosTotales.setText(AppContext.getBundle().getString("totalPoints").replace("{0}", String.valueOf(total)));
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

    @Override
public void actualizarTextos() {
    // Encabezados de columnas
    colTipo.setText(AppContext.getBundle().getString("wasteType"));
    colPuntos.setText(AppContext.getBundle().getString("points"));
    colFecha.setText(AppContext.getBundle().getString("lastDate"));

    // Placeholder de la tabla
    tablaResiduos.setPlaceholder(new Label(AppContext.getBundle().getString("noPoints")));

    // Botón cerrar
    btnCerrar.setText(AppContext.getBundle().getString("closeList"));

    // Vuelve a cargar los datos para actualizar los textos de resumen y mensajes
    cargarDatos();
}

}
