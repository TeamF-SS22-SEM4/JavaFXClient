package at.fhv.ec.javafxclient;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;

public class SceneManager {

    private static final String VIEW_LOGIN = "login";
    public static final String VIEW_SHOP = "shop";
    public static final String VIEW_EXCHANGE = "exchange";
    public static final String VIEW_CUSTOMER = "customer";
    public static final String VIEW_MESSAGES_READ_CHANNELS = "messages-read-channels";
    public static final String VIEW_MESSAGES_WRITE_CHANNELS = "messages-write-channels";
    public static final String VIEW_ORDERS = "order";

    public static final String VIEW_SHOPPING_CART = "shoppingcart";

    public static final String VIEW_MESSAGES_WRITE = "messages-write";
    public static final String VIEW_MESSAGES_READ_OVERVIEW = "messages-read-overview";
    public static final String VIEW_MESSAGES_READ = "messages-read";

    private static SceneManager instance;
    private static Stage window;

    private SceneManager() {}

    public static SceneManager getInstance() {
        if(instance == null) {
            instance = new SceneManager();
            window = Application.getWindow();
        }
        return instance;
    }

    public void switchView(String viewName) {
        try {
            String view = "views/" + viewName + ".fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(view));
            Scene scene = new Scene(loader.load());

            for (int i = 0; i < Application.getStylesheets().size(); i++) {
                scene.getStylesheets().add(Application.getStylesheets().get(i));
            }

            window.setHeight(window.getHeight());
            window.setWidth(window.getWidth());
            window.setScene(scene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchTheme(String themeStyle, String themeColor) {
        for (int i = 0; i < Application.getStylesheets().size(); i++) {
            window.getScene().getStylesheets().remove(Application.getStylesheets().get(i));
        }

        ArrayList<String> stylesheets = new ArrayList<>();
        stylesheets.add(Application.getStylesheetDefault());

        if (themeStyle.equals(("light"))) {
            stylesheets.add(Application.getStylesheetLight());
        }

        if (themeColor.equals(("color1"))) {
            stylesheets.add(Application.getStylesheetColor1());
        } else if (themeColor.equals(("color2"))) {
            stylesheets.add(Application.getStylesheetColor2());
        } else if (themeColor.equals(("color3"))) {
            stylesheets.add(Application.getStylesheetColor3());
        }

        Application.setStylesheets(stylesheets);

        for (int i = 0; i < Application.getStylesheets().size(); i++) {
            window.getScene().getStylesheets().add(Application.getStylesheets().get(i));
        }

        window.setScene(window.getScene());
        window.show();
    }

    public void logout() {
        switchTheme("dark", "color2");
        switchView(SceneManager.VIEW_LOGIN);
    }
}