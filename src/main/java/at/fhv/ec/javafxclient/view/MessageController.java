package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.SessionManager;
import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ss22.ea.f.communication.api.MessagingService;
import at.fhv.ss22.ea.f.communication.api.ProductSearchService;
import at.fhv.ss22.ea.f.communication.api.RMIFactory;
import at.fhv.ss22.ea.f.communication.dto.MessageDTO;
import at.fhv.ss22.ea.f.communication.exception.NoPermissionForOperation;
import at.fhv.ss22.ea.f.communication.exception.SessionExpired;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.rmi.RemoteException;

public class MessageController {

    @FXML
    private TextField titleTextField;

    @FXML
    private TextArea contentTextArea;

    @FXML
    private TextField topicTextField;

    @FXML
    protected void onSendButtonClicked() {
        System.out.println("On send button clicked");

        try {
            MessagingService messagingService = RMIClient.getRmiClient().getRmiFactory().getMessagingService();

            MessageDTO message = MessageDTO.builder()
                    .withTitle(titleTextField.getText())
                    .withContent(contentTextArea.getText())
                    .withTopicName(topicTextField.getText())
                    .build();

            //messagingService.publish(SessionManager.getInstance().getSessionId(), message);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
