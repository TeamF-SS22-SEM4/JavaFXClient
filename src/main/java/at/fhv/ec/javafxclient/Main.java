package at.fhv.ec.javafxclient;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;

public class Main extends javafx.application.Application {

    private static final String STYLESHEET_DEFAULT = "style.css";
    private static final String STYLESHEET_LIGHT = "light.css";
    private static final String STYLESHEET_COLOR1 = "color1.css";
    private static final String STYLESHEET_COLOR2 = "color2.css";
    private static final String STYLESHEET_COLOR3 = "color3.css";


    private static Stage window;
    private static ArrayList<String> stylesheets = new ArrayList<>();


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("views/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stylesheets.add(STYLESHEET_DEFAULT);
        scene.getStylesheets().add(STYLESHEET_DEFAULT);


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


    public static ArrayList<String> getStylesheets() {
        return stylesheets;
    }

    public static void setStylesheets(ArrayList<String> stylesheets) {
        Main.stylesheets = stylesheets;
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

}