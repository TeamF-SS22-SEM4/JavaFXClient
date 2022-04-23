package at.fhv.ec.javafxclient.view.utils;

import at.fhv.ec.javafxclient.Main;
import at.fhv.ec.javafxclient.view.MessageListEntryController;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.io.UncheckedIOException;

public class MessageListCell extends ListCell<String> {
    private final MessageListEntryController messageListEntryController;

    public MessageListCell() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/messageListEntry.fxml"));
            Pane topicListEntry = loader.load();
            messageListEntryController = loader.getController();
            setGraphic(topicListEntry);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        } catch (IOException exc) {
            // IOException here is fatal:
            throw new UncheckedIOException(exc);
        }
    }

    @Override
    protected void updateItem(String messageTitle, boolean empty) {
        super.updateItem(messageTitle, empty);
        messageListEntryController.setMessage(messageTitle);
    }
}
