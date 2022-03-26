package at.fhv.ec.javafxclient;

import at.fhv.ec.javafxclient.view.DetailsController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.UUID;

public class SceneManager {
    private static SceneManager instance;
    private Stage primaryStage;

    // Views
    private final static String SEARCH_VIEW = "views/details-view.fxml";

    private SceneManager() {

    }

    public static SceneManager getInstance() {
        if(instance == null) {
            instance = new SceneManager();
            instance.primaryStage = Application.getPrimaryStage();
        }

        return instance;
    }

    public void switchToDetailsView(UUID productId) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        SEARCH_VIEW
                )
        );

        DetailsController detailsController = new DetailsController();
        detailsController.initData(productId);
        loader.setController(detailsController);

        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
