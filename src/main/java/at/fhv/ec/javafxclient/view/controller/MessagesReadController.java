package at.fhv.ec.javafxclient.view.controller;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.SessionManager;
import at.fhv.ec.javafxclient.communication.JMSClient;
import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ec.javafxclient.model.CustomMessage;
import at.fhv.ss22.ea.f.communication.exception.NoPermissionForOperation;
import at.fhv.ss22.ea.f.communication.exception.SessionExpired;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.jms.JMSException;
import java.rmi.RemoteException;
import java.time.LocalDateTime;

public class MessagesReadController {

    public static String topicName;
    public static CustomMessage customMessage;

    @FXML
    private Label titleLabel;
    @FXML
    private TextField titleTextField;
    @FXML
    private TextArea contentTextArea;
    @FXML
    private Button acknowledgeButton;
    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        try {
            RMIClient.getRmiClient()
                    .getRmiFactory()
                    .getMessagingService()
                    .updateLastViewed(SessionManager.getInstance().getSessionId(), LocalDateTime.now());

            SessionManager.getInstance().onMessageViewed();
        } catch (RemoteException | SessionExpired | NoPermissionForOperation e) {
            throw new RuntimeException(e);
        }

        statusLabel.setVisible(false);
        titleLabel.setText(topicName + " - " + customMessage.getTitle());

        titleTextField.setText(customMessage.getTitle());
        titleTextField.setEditable(false);
        titleTextField.setFocusTraversable(false);

        contentTextArea.setText(customMessage.getContent());
        contentTextArea.setEditable(false);
        contentTextArea.setWrapText(true);
    }

    @FXML
    public void onAcknowledgeButtonClicked() {
        statusLabel.getStyleClass().remove("alert");

        try {
            JMSClient.getJmsClient().acknowledgeMessage(topicName, customMessage);
            statusLabel.setText("Successfully acknowledged message");
        } catch (JMSException e) {
            statusLabel.getStyleClass().add("alert");
            statusLabel.setText("Couldn't acknowledge message");
            throw new RuntimeException(e);
        }

        acknowledgeButton.setVisible(false);
        statusLabel.setVisible(true);
    }

    public void onBackButtonClicked() {
        SceneManager.getInstance().switchView(SceneManager.VIEW_MESSAGES_READ_OVERVIEW);
    }
}
