package at.fhv.ec.javafxclient;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import java.io.IOException;

//TODO: Try and catch!!!
public class Main extends javafx.application.Application {

    private static Stage window;
    private static final String stylesheet = "style.css";

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("views/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(stylesheet);

        window = stage;
        window.getIcons().add(new Image("images/icon.png"));
        window.setTitle("Tomify");
        window.setScene(scene);
        window.setMaximized(true);
        window.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static Stage getWindow() {
        return window;
    }

    public static String returnStylesheet() {
        return stylesheet;
    }

}