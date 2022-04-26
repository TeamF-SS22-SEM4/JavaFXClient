package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.communication.JMSClient;
import at.fhv.ec.javafxclient.model.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageListController {
    public static String topicName;

    @FXML
    private TableView<Message> messageTable;

    @FXML
    private TableColumn<Message, Button> detailsButton;

    public void initialize() {
        // Get plain messages from jms and create Messages for the table
        List<Message> messages = new ArrayList<>();
        List<String> plainMessages = JMSClient.getJmsClient().getMessagesByTopic(topicName);

        if(plainMessages != null) {
            plainMessages.forEach(plainMessage -> {
                String title = plainMessage.split("\n")[0];
                String content = plainMessage.split("\n")[1];
                messages.add(new Message(title, content));
            });
        }

        initTable();

        ObservableList<Message> messageList = FXCollections.observableArrayList(messages);
        messageTable.setItems(messageList);
    }

    private void initTable() {
        detailsButton.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Message, Button> call(TableColumn<Message, Button> param) {

                final Button detailsButton = new Button("Details");
                detailsButton.getStyleClass().add("btn");

                return new TableCell<>() {
                    @Override
                    public void updateItem(Button item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            setGraphic(detailsButton);
                            setText(null);
                        }
                    }
                };
            }
        });
    }
}
