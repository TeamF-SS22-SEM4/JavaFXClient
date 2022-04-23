package at.fhv.ec.javafxclient.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MessageListEntryController {
    @FXML
    private Label messageTitleLabel;

    @FXML
    private Button detailsButton;

    public void setMessage(String title) {
        messageTitleLabel.setText(title);

        detailsButton.setOnAction(event -> {
            System.out.println("Not implemented yet");
        });
    }
}
