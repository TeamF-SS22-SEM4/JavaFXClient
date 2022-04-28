package at.fhv.ec.javafxclient.view;

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

public class MessageDetailsController {
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

    public void initialize() {
        // Update last viewed of employee
        try {
            RMIClient.getRmiClient()
                    .getRmiFactory()
                    .getMessagingService()
                    .updateLastViewed(SessionManager.getInstance().getSessionId(), LocalDateTime.now());

            SessionManager.getInstance().onMessageViewed();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (SessionExpired e) {
            throw new RuntimeException(e);
        } catch (NoPermissionForOperation e) {
            throw new RuntimeException(e);
        }

        statusLabel.setVisible(false);
        titleLabel.setText("Message '" + customMessage.getTitle() + "' from topic " + topicName);

        // Fill textfield and make it read only
        titleTextField.setText(customMessage.getTitle());
        titleTextField.setEditable(false);
        titleTextField.setFocusTraversable(false);

        // fill textarea and make it read only
        contentTextArea.setText(customMessage.getContent());
        contentTextArea.setEditable(false);
        contentTextArea.setWrapText(true);
    }

    @FXML
    protected void onAcknowledgeButtonClicked() {
        try {
            JMSClient.getJmsClient().acknowledgeMessage(topicName, customMessage);

            statusLabel.setText("Successfully acknowledged message");
        } catch (JMSException e) {
            statusLabel.setText("Couldn't acknowledge message");
            throw new RuntimeException(e);
        }

        acknowledgeButton.setVisible(false);
        statusLabel.setVisible(true);
    }
}
