package at.fhv.ec.javafxclient;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

// TODO: Try and catch!!!
// TODO: Use consistent initialize
// TODO: Remove Popups and use statusLabels
public class Application extends javafx.application.Application {

    private static final String STYLESHEET_DEFAULT = "stylesheets/style.css";
    private static final String STYLESHEET_LIGHT = "stylesheets/light.css";
    private static final String STYLESHEET_COLOR1 = "stylesheets/color1.css";
    private static final String STYLESHEET_COLOR2 = "stylesheets/color2.css";
    private static final String STYLESHEET_COLOR3 = "stylesheets/color3.css";

    private static Stage window;
    private static ArrayList<String> stylesheets = new ArrayList<>();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("views/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stylesheets.add(STYLESHEET_DEFAULT);
        scene.getStylesheets().add(STYLESHEET_DEFAULT);

        window = stage;
        window.getIcons().add(new Image("images/icon.png"));
        window.setTitle("Tomify");
        window.setScene(scene);
        window.setHeight(800);
        window.setWidth(1400);
        window.show();
    }

    public static String getStylesheetDefault() {
        return STYLESHEET_DEFAULT;
    }

    public static String getStylesheetLight() {
        return STYLESHEET_LIGHT;
    }

    public static String getStylesheetColor1() {
        return STYLESHEET_COLOR1;
    }

    public static String getStylesheetColor2() {
        return STYLESHEET_COLOR2;
    }

    public static String getStylesheetColor3() {
        return STYLESHEET_COLOR3;
    }

    public static ArrayList<String> getStylesheets() {
        return stylesheets;
    }

    public static void setStylesheets(ArrayList<String> stylesheets) {
        Application.stylesheets = stylesheets;
    }

    public static Stage getWindow() {
        return window;
    }

    public static void main(String[] args) {
        launch();
    }

}