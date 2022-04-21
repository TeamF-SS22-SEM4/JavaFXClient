package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;

public class TopicListEntryController {
    @FXML
    private Label topicNameLabel;

    @FXML
    private Button newMessageButton;

    public void setTopic(String topicName) {
        topicNameLabel.setText(topicName);

        newMessageButton.setOnAction(event -> {
            try {
                MessageController.topicName = topicNameLabel.getText();
                SceneManager.getInstance().switchView("send-message");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
