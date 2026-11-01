package cl.duoc.maquinaria;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Clase de inicio: carga la vista, crea la escena y abre la ventana.
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // getResource busca el FXML dentro de src/main/resources/cl/duoc/maquinaria/
        FXMLLoader loader = new FXMLLoader(App.class.getResource("view/main-view.fxml"));
        Scene scene = new Scene(loader.load());   // load() crea los controles Y el controlador
        stage.setTitle("Arriendo de Maquinaria");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
