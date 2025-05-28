package co.edu.uptc;

import java.io.IOException;

import co.edu.uptc.controlador.ReciclajeControlador;
import co.edu.uptc.servicio.ReciclajeServicio;
import co.edu.uptc.vista.AppContext;
import co.edu.uptc.vista.InicioViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Inicializar el servicio y controlador
        ReciclajeServicio servicio = new ReciclajeServicio();
        ReciclajeControlador controlador = new ReciclajeControlador(servicio);
        
        
        // Cargar la vista de inicio
         FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/co/edu/uptc/vista/inicio_view.fxml"), AppContext.getBundle());
        Parent root = loader.load();
        
        // Obtener el controlador y pasarle el ReciclajeControlador
        InicioViewController inicioController = loader.getController();
        inicioController.setControlador(controlador);
        
        // Configurar y mostrar la escena
        Scene scene = new Scene(root, 1440, 1024);
        stage.setScene(scene);
        stage.setTitle("Sistema de Reciclaje");
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
