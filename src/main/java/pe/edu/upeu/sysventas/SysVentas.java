package pe.edu.upeu.sysventas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import pe.edu.upeu.sysventas.config.AppContext;

import java.io.IOException;

public class SysVentas extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        AppContext context=AppContext.getInstance();
        FXMLLoader fxmlLoader = new FXMLLoader(
                SysVentas.class.getResource("/view/main_producto.fxml")
        );
        fxmlLoader.setControllerFactory(context::getBean);
        Screen screen=Screen.getPrimary();
        Rectangle2D r2d=screen.getVisualBounds();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Sistema de Ventas");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}