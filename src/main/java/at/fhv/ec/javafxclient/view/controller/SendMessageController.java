package at.fhv.ec.javafxclient.view.controller;

import at.fhv.ec.javafxclient.SessionManager;
import at.fhv.ec.javafxclient.communication.EJBClient;
import at.fhv.ss22.ea.f.communication.api.MessagingService;
import at.fhv.ss22.ea.f.communication.dto.MessageDTO;
import at.fhv.ss22.ea.f.communication.exception.NoPermissionForOperation;
import at.fhv.ss22.ea.f.communication.exception.SessionExpired;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.naming.NamingException;

public class SendMessageController {
    public static String topicName;

    @FXML
    private Label topicNameLabel;
    @FXML
    private TextField titleTextField;

    @FXML
    private TextArea contentTextArea;

    @FXML
    private Label statusLabel;

    @FXML
    private void initialize() {
        topicNameLabel.setText(topicName);
        statusLabel.setVisible(false);
        contentTextArea.setWrapText(true);
    }

    @FXML
    protected void onSendButtonClicked() {
        statusLabel.setVisible(false);

        if((!titleTextField.getText().isEmpty()) && (!contentTextArea.getText().isEmpty())) {
            try {
                MessagingService messagingService = EJBClient.getEjbClient().getMessagingService();

                MessageDTO message = MessageDTO.builder()
                        .withTitle(titleTextField.getText())
                        .withContent(contentTextArea.getText())
                        .withTopicName(topicName)
                        .build();

                messagingService.publish(SessionManager.getInstance().getSessionId(), message);

                titleTextField.clear();
                contentTextArea.clear();

                statusLabel.setText("Sent message successfully to topic " + topicName);
                statusLabel.setVisible(true);
            } catch (SessionExpired | NoPermissionForOperation e) {
                throw new RuntimeException(e);
            } catch (NamingException e) {
                throw new RuntimeException(e);
            }
        } else {
            statusLabel.setText("Title and message can't be empty");
            statusLabel.setVisible(true);
        }
    }
}
