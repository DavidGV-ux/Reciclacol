package co.edu.uptc.servicio;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import co.edu.uptc.modelo.Residuo;
import co.edu.uptc.modelo.Usuario;

public class ReporteServicio {
	private ReciclajeServicio servicio;

	public ReporteServicio(ReciclajeServicio servicio) {
		this.servicio = servicio;
	}

	public void generarReporte(String nombreArchivo) {
		Document documento = new Document();
		try {
			String ruta = "proyecto_final_fx/src/main/resources/reportes/" + nombreArchivo;
			PdfWriter.getInstance(documento, new FileOutputStream(ruta));
			documento.open();

			// Título
			Font tituloFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
			Paragraph titulo = new Paragraph("Reporte de Reciclaje", tituloFont);
			titulo.setAlignment(Element.ALIGN_CENTER);
			documento.add(titulo);
			documento.add(new Paragraph("\n"));

			agregarTablaTotalPorMaterialPorUsuario(documento);

			// Agregar el total reciclado por usuario
			agregarTablaTotalReciclado(documento);

			// Agregar ranking de usuarios con más reciclaje
			agregarRankingUsuarios(documento);

			// Agregar gráfico del material más reciclado
			agregarGraficoMaterialMasReciclado(documento);

			// Agregar total reciclado por material
			agregarTotalRecicladoPorMaterial(documento);

			documento.close();
			System.out.println("Reporte PDF generado con éxito!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void agregarTablaTotalReciclado(Document documento) throws DocumentException {
		PdfPTable tabla = new PdfPTable(2);
		tabla.setWidthPercentage(100);
		tabla.setSpacingBefore(10);
		tabla.addCell(new PdfPCell(new Phrase("Usuario", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));
		tabla.addCell(
				new PdfPCell(new Phrase("Total Reciclado (Kg)", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));

				for (Usuario usuario : servicio.obtenerTodosLosUsuarios()) {
					double totalReciclado = usuario.getResiduos().stream().mapToDouble(Residuo::getPeso).sum();
					tabla.addCell(usuario.getNombreCompleto());
					tabla.addCell(String.valueOf(totalReciclado));
				}

		documento
				.add(new Paragraph("Total Reciclado por Usuario", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
		documento.add(tabla);
		documento.add(new Paragraph("\n"));
	}

	private void agregarTotalRecicladoPorMaterial(Document documento) throws DocumentException {
		// Mapa para almacenar el total reciclado por tipo de material
		Map<String, Double> totalPorMaterial = new HashMap<>();

		// Sumar el peso de los residuos por tipo de material
		for (Usuario usuario : servicio.obtenerTodosLosUsuarios()) {
			for (Residuo residuo : usuario.getResiduos()) {
				totalPorMaterial.put(residuo.getTipoMaterial(),
						totalPorMaterial.getOrDefault(residuo.getTipoMaterial(), 0.0) + residuo.getPeso());
			}
		}
		// Crear una tabla para mostrar el total reciclado por material
		PdfPTable tablaMateriales = new PdfPTable(2);
		tablaMateriales.setWidthPercentage(100);
		tablaMateriales.setSpacingBefore(10);
		tablaMateriales
				.addCell(new PdfPCell(new Phrase("Tipo de Material", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));
		tablaMateriales.addCell(
				new PdfPCell(new Phrase("Total Reciclado (Kg)", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));

		// Llenar la tabla con los datos del total reciclado por material
		for (Map.Entry<String, Double> entry : totalPorMaterial.entrySet()) {
			tablaMateriales.addCell(entry.getKey());
			tablaMateriales.addCell(String.valueOf(entry.getValue()));
		}

		// Agregar la tabla al documento
		documento.add(new Paragraph("Total Reciclado por Tipo de Material",
				FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
		documento.add(tablaMateriales);
		documento.add(new Paragraph("\n"));
	}

	private void agregarGraficoMaterialMasReciclado(Document documento) throws Exception {
		Map<String, Double> materialReciclado = new HashMap<>();
		for (Usuario usuario : servicio.obtenerTodosLosUsuarios()) {
			for (Residuo residuo : usuario.getResiduos()) {
				materialReciclado.put(residuo.getTipoMaterial(),
						materialReciclado.getOrDefault(residuo.getTipoMaterial(), 0.0) + residuo.getPeso());
			}
		}

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (Map.Entry<String, Double> entry : materialReciclado.entrySet()) {
			dataset.addValue(entry.getValue(), "Kg", entry.getKey());
		}

		JFreeChart chart = ChartFactory.createBarChart("Material Más Reciclado", "Material", "Kg", dataset,
				PlotOrientation.VERTICAL, false, true, false);

		BufferedImage bufferedImage = chart.createBufferedImage(500, 300);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "png", baos);
		Image imagen = Image.getInstance(baos.toByteArray());

		documento.add(new Paragraph("Material Más Reciclado", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
		documento.add(imagen);
		documento.add(new Paragraph("\n"));
	}

	private void agregarRankingUsuarios(Document documento) throws DocumentException {
		List<Usuario> usuariosOrdenados = new ArrayList<>(servicio.obtenerTodosLosUsuarios());
		usuariosOrdenados.sort((u1, u2) -> Double.compare(u2.getResiduos().stream().mapToDouble(Residuo::getPeso).sum(),
				u1.getResiduos().stream().mapToDouble(Residuo::getPeso).sum()));

		PdfPTable tabla = new PdfPTable(2);
		tabla.setWidthPercentage(100);
		tabla.setSpacingBefore(10);
		tabla.addCell(new PdfPCell(new Phrase("Usuario", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));
		tabla.addCell(
				new PdfPCell(new Phrase("Total Reciclado (Kg)", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));

				for (Usuario usuario : usuariosOrdenados) {
					double totalReciclado = usuario.getResiduos().stream().mapToDouble(Residuo::getPeso).sum();
					tabla.addCell(usuario.getNombreCompleto());
					tabla.addCell(String.valueOf(totalReciclado));
				}

		documento.add(new Paragraph("Ranking de Usuarios con Más Reciclaje",
				FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
		documento.add(tabla);
	}

	private void agregarTablaTotalPorMaterialPorUsuario(Document documento) throws DocumentException {
		PdfPTable tabla = new PdfPTable(3); // 3 columnas: Usuario, Material, Total Reciclado
		tabla.setWidthPercentage(100);
		tabla.setSpacingBefore(10);
		tabla.addCell(new PdfPCell(new Phrase("Usuario", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));
		tabla.addCell(new PdfPCell(new Phrase("Material", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));
		tabla.addCell(
				new PdfPCell(new Phrase("Total Reciclado (Kg)", FontFactory.getFont(FontFactory.HELVETICA_BOLD))));

		// Iterar sobre cada usuario
		for (Usuario usuario : servicio.obtenerTodosLosUsuarios()) {
			// Mapa para almacenar el total reciclado por material para cada usuario
			Map<String, Double> totalPorMaterial = new HashMap<>();

			// Sumar el peso de los residuos por tipo de material para el usuario actual
			for (Residuo residuo : usuario.getResiduos()) {
				totalPorMaterial.put(residuo.getTipoMaterial(),
						totalPorMaterial.getOrDefault(residuo.getTipoMaterial(), 0.0) + residuo.getPeso());
			}

			// Agregar una fila por cada material reciclado por el usuario
			for (Map.Entry<String, Double> entry : totalPorMaterial.entrySet()) {
				tabla.addCell(usuario.getNombreCompleto());
				tabla.addCell(entry.getKey());
				tabla.addCell(String.valueOf(entry.getValue()));
			}
		}

		// Agregar la tabla al documento
		documento.add(new Paragraph("Total Reciclado por Material por Usuario",
				FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
		documento.add(tabla);
		documento.add(new Paragraph("\n"));
	}

	public void generarReportePersonalizado(String ruta) {
		Document documento = new Document();
		try {
			PdfWriter.getInstance(documento, new FileOutputStream(ruta));
			documento.open();
			// ... el resto igual que en generarReporte() ...
			agregarTablaTotalPorMaterialPorUsuario(documento);
			agregarTablaTotalReciclado(documento);
			agregarRankingUsuarios(documento);
			agregarGraficoMaterialMasReciclado(documento);
			agregarTotalRecicladoPorMaterial(documento);
			documento.close();
			System.out.println("Reporte PDF guardado con éxito!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
