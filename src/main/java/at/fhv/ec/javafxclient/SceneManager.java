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
    private final static String SEARCH_VIEW = "views/search-view-copy.fxml";
    private final static String DETAILS_VIEW = "views/details-view.fxml";

    private SceneManager() {}

    public static SceneManager getInstance() {
        if(instance == null) {
            instance = new SceneManager();
            instance.primaryStage = Application.getPrimaryStage();
        }

        return instance;
    }

    public void switchToSearchView() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        SEARCH_VIEW
                )
        );

        switchView(loader);
    }

    public void switchToDetailsView(UUID productId) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        DETAILS_VIEW
                )
        );

        DetailsController detailsController = new DetailsController();
        detailsController.initData(productId);
        loader.setController(detailsController);

        switchView(loader);
    }

    private void switchView(FXMLLoader loader) throws IOException {
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add("style.css");
        scene.getStylesheets().add("org/kordamp/bootstrapfx/bootstrapfx.css");

        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
