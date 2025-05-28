package co.edu.uptc.vista;

import java.util.Locale;
import java.util.ResourceBundle;

import co.edu.uptc.controlador.ReciclajeControlador;
import co.edu.uptc.servicio.ReciclajeServicio;

public class AppContext {
    private static final ReciclajeServicio reciclajeServicio = new ReciclajeServicio();
    private static final ReciclajeControlador reciclajeControlador = new ReciclajeControlador(reciclajeServicio);
    private static Locale currentLocale = new Locale("es");
    private static ResourceBundle bundle = ResourceBundle.getBundle("co.edu.uptc.I18N.mensajes", currentLocale);

    public static ReciclajeControlador getReciclajeControlador() {
        return reciclajeControlador;
    }
    
    public static void switchLanguage(String lang) {
        currentLocale = new Locale(lang);
        bundle = ResourceBundle.getBundle("co.edu.uptc.I18N.mensajes", currentLocale);
    }
    
    public static ResourceBundle getBundle() {
        return bundle;
    }

    public static Locale getCurrentLocale() {
        return currentLocale;
    }
}
