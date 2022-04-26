package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.model.Message;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class MessageDetailsController {
    public static String topicName;
    public static Message message;

    @FXML
    private Label titleLabel;

    @FXML
    private TextField titleTextField;

    @FXML
    private TextArea contentTextArea;

    public void initialize() {
        titleLabel.setText("Message '" + message.getTitle() + "' from topic " + topicName);

        // Fill textfield and make it read only
        titleTextField.setText(message.getTitle());
        titleTextField.setEditable(false);
        titleTextField.setFocusTraversable(false);

        // fill textarea and make it read only
        contentTextArea.setText(message.getContent());
        contentTextArea.setEditable(false);
        contentTextArea.setWrapText(true);
    }
}
