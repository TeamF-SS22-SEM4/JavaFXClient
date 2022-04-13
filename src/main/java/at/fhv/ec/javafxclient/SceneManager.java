package at.fhv.ec.javafxclient;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneManager {

    private static SceneManager instance;
    private static Stage window;

    private SceneManager() {}

    public static SceneManager getInstance() {
        if(instance == null) {
            instance = new SceneManager();
            window = Main.getWindow();
        }
        return instance;
    }

    public void switchView(String viewName) throws IOException {
        String view = "views/" + viewName + ".fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(view));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(Main.returnStylesheet());

        window.setHeight(window.getHeight());
        window.setWidth(window.getWidth());
        window.setScene(scene);
        window.show();
    }

    public void logout() throws IOException {
        switchView("login");
    }
}