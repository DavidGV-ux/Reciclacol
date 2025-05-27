package co.edu.uptc.vista;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import co.edu.uptc.controlador.ReciclajeControlador;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class GenerarReporteViewController {

    @FXML private Button btnGenerar;
    @FXML private Button btnDescargar;
    @FXML private ScrollPane scrollPreview;
    @FXML private ImageView imgPreview;
    @FXML private Label lblMensaje;

    private ReciclajeControlador controlador;
    private boolean previewMostrada = false;
    private File tempPdfFile;

    public void setControlador(ReciclajeControlador controlador) {
        this.controlador = controlador;
    }

    @FXML
    private void initialize() {
        btnGenerar.setOnAction(e -> mostrarVistaPrevia());
        btnDescargar.setOnAction(e -> descargarReporte());
    }

    private void mostrarVistaPrevia() {
    try {
        // 1. Generar el PDF temporal en resources/reportes
        String ruta = "proyecto_final_fx/src/main/resources/reportes/Reporte_Reciclaje_preview.pdf";
        tempPdfFile = new File(ruta);
        controlador.generarReportePDFPersonalizado(tempPdfFile.getAbsolutePath());

        // 2. Renderizar la primera página del PDF a imagen (usando PDFBox 3.x)
        try (PDDocument document = Loader.loadPDF(tempPdfFile)) {
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage bim = renderer.renderImageWithDPI(0, 120); // Página 0, 120dpi
            Image fxImage = SwingFXUtils.toFXImage(bim, null);
            imgPreview.setImage(fxImage);
            scrollPreview.setContent(imgPreview);
        }

        lblMensaje.setStyle("-fx-text-fill: green;");
        lblMensaje.setText("Vista previa generada correctamente.");
        previewMostrada = true;
    } catch (Exception ex) {
        ex.printStackTrace();
        lblMensaje.setStyle("-fx-text-fill: red;");
        lblMensaje.setText("Error al generar la vista previa.");
    }
}

    private void descargarReporte() {
        if (!previewMostrada) {
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText("Primero debes generar una vista previa.");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar reporte como");
        fileChooser.setInitialFileName("Reporte_Reciclaje.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));

        Stage stage = (Stage) btnDescargar.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null && tempPdfFile != null && tempPdfFile.exists()) {
            try {
                java.nio.file.Files.copy(tempPdfFile.toPath(), file.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                lblMensaje.setStyle("-fx-text-fill: green;");
                lblMensaje.setText("Reporte guardado en: " + file.getAbsolutePath());
            } catch (IOException ex) {
                ex.printStackTrace();
                lblMensaje.setStyle("-fx-text-fill: red;");
                lblMensaje.setText("Error al guardar el reporte.");
            }
        }
    }
}
