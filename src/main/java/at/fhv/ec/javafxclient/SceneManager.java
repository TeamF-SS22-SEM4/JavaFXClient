package at.fhv.ec.javafxclient;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

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


        for (int i = 0; i < Main.getStylesheets().size(); i++) {
            scene.getStylesheets().add(Main.getStylesheets().get(i));
        }


        window.setHeight(window.getHeight());
        window.setWidth(window.getWidth());
        window.setScene(scene);
        window.show();
    }

    public void switchTheme(String themeStyle, String themeColor) {

        for (int i = 0; i < Main.getStylesheets().size(); i++) {
            window.getScene().getStylesheets().remove(Main.getStylesheets().get(i));
        }

        ArrayList<String> stylesheets = new ArrayList<>();
        stylesheets.add(Main.getStylesheetDefault());

        if (themeStyle.equals("light")) {
            stylesheets.add(Main.getStylesheetLight());
        }

        if (themeColor.equals("color1")) {
            stylesheets.add(Main.getStylesheetColor1());
        } else if (themeColor.equals("color2")) {
            stylesheets.add(Main.getStylesheetColor2());
        } else if (themeColor.equals("color3")) {
            stylesheets.add(Main.getStylesheetColor3());
        }

        Main.setStylesheets(stylesheets);

        for (int i = 0; i < Main.getStylesheets().size(); i++) {
            window.getScene().getStylesheets().add(Main.getStylesheets().get(i));
        }

        window.setScene(window.getScene());
        window.show();
    }

    public void logout() throws IOException {
        switchTheme("dark","color2");
        switchView("login");
    }
}