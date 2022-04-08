package at.fhv.ec.javafxclient;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Stack;


public class SceneManager {
    private static SceneManager instance;
    private static Stage primaryStage;
    private static Stack<String> viewHistory;

    private SceneManager() {}

    public static SceneManager getInstance() {
        if(instance == null) {
            instance = new SceneManager();
            primaryStage = Application.getPrimaryStage();
            viewHistory = new Stack<>();
        }

        return instance;
    }

    public void switchView(String oldView, String newView) throws IOException {
        viewHistory.push(oldView);
        loadView(newView);
    }

    public void back() throws IOException {
        // Take last view from stack but make it not possible to switch back to login view
        if((!viewHistory.empty()) && (!viewHistory.peek().equalsIgnoreCase("login"))) {
            loadView(viewHistory.pop());
        }
    }

    public String getLastView() {
        return viewHistory.peek();
    }

    private void loadView(String view) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        view
                )
        );

        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add("style.css");

        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
