package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.view.utils.TopicListCell;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class TopicController {
    @FXML
    private ListView<String> topicListView;

    public void initialize() {
        topicListView.setCellFactory(lv -> new TopicListCell());

        topicListView.getItems().add("Pop");
        topicListView.getItems().add("Metal");
        topicListView.getItems().add("Rock");
        topicListView.getItems().add("Electronic");
        topicListView.getItems().add("Grunge");
        topicListView.getItems().add("Hip-Hop");
        topicListView.getItems().add("Hard Rock");
    }
}
