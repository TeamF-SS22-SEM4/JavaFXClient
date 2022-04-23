package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.view.utils.MessageListCell;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class MessageListController {
    public static String topicName;

    @FXML
    private ListView<String> messageListView;

    public void initialize() {
        messageListView.setCellFactory(lv -> new MessageListCell());

        // TODO: Get all messages of topic from JMS Provider
        // TODO: Add messages to list
    }
}
