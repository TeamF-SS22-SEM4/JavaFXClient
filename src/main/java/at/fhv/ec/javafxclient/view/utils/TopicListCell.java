package at.fhv.ec.javafxclient.view.utils;

import at.fhv.ec.javafxclient.Main;
import at.fhv.ec.javafxclient.view.TopicListEntryController;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.io.UncheckedIOException;

public class TopicListCell extends ListCell<String> {

    private final TopicListEntryController topicListEntryController;

    public TopicListCell() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/topicListEntry.fxml"));
            Pane topicListEntry = loader.load();
            topicListEntryController = loader.getController();
            setGraphic(topicListEntry);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        } catch (IOException exc) {
            // IOException here is fatal:
            throw new UncheckedIOException(exc);
        }
    }

    @Override
    protected void updateItem(String topic, boolean empty) {
        super.updateItem(topic, empty);
        topicListEntryController.setTopic(topic);
    }
}
