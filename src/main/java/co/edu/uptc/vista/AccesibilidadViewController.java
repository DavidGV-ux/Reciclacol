package co.edu.uptc.vista;

import javafx.scene.Scene;

public class AccesibilidadViewController {

    private static String estiloActual = "";

    public static void aplicarEstilo(Scene scene, String opcion) {
        // Elimina los estilos anteriores de accesibilidad
        scene.getStylesheets().removeIf(s -> 
            s.contains("AltoContraste.css") || s.contains("LetraGrande.css")
        );

        switch (opcion) {
            case "Alto Contraste":
                scene.getStylesheets().add(
                    AppContext.class.getResource("/co/edu/uptc/Estilos/AltoContraste.css").toExternalForm()
                );
                estiloActual = "Alto Contraste";
                break;
            case "Letra Grande":
                scene.getStylesheets().add(
                    AppContext.class.getResource("/co/edu/uptc/Estilos/LetraGrande.css").toExternalForm()
                );
                estiloActual = "Letra Grande";
                break;
            default:
                estiloActual = "Normal";
                break;
        }
    }

    public static String getEstiloActual() {
        return estiloActual;
    }
}
