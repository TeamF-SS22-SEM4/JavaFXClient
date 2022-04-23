package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.SessionManager;
import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ss22.ea.f.communication.exception.NoPermissionForOperation;
import at.fhv.ss22.ea.f.communication.exception.SessionExpired;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.rmi.RemoteException;

public class TopicListEntryController {
    @FXML
    private Label topicNameLabel;

    @FXML
    private Button subscribersButton;

    @FXML
    private Button viewMessagesButton;

    @FXML
    private Button newMessageButton;

    @FXML
    private Button unsubscribeButton;

    public void setTopic(String topicName) {
        topicNameLabel.setText(topicName);

        viewMessagesButton.setOnAction(event -> {
            try {
                SceneManager.getInstance().switchView("message-list");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        if(SessionManager.getInstance().getRoles().contains("Operator")) {
            unsubscribeButton.setVisible(false);

            newMessageButton.setOnAction(event -> {
                try {
                    SendMessageController.topicName = topicNameLabel.getText();
                    SceneManager.getInstance().switchView("send-message");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            subscribersButton.setVisible(false);
            newMessageButton.setVisible(false);

            unsubscribeButton.setOnAction(event -> {
                try {
                    SendMessageController.topicName = topicNameLabel.getText();
                    RMIClient.getRmiClient().getRmiFactory().getMessagingService().unsubscribeFrom(
                            SessionManager.getInstance().getSessionId(), topicName
                    );
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } catch (SessionExpired e) {
                    throw new RuntimeException(e);
                } catch (NoPermissionForOperation e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
