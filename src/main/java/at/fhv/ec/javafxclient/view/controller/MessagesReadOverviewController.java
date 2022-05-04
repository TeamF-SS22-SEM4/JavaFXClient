package at.fhv.ec.javafxclient.view.controller;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.communication.JMSClient;
import at.fhv.ec.javafxclient.model.CustomMessage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.util.List;

public class MessagesReadOverviewController {

    public static String topicName;

    @FXML
    private TableView<CustomMessage> messageTable;
    @FXML
    private Label headingLabel;
    @FXML
    private TableColumn<CustomMessage, Button> detailsButtonColumn;
    @FXML
    private TableColumn<CustomMessage, String> dateColumn;


    @FXML
    public void initialize() {
        formatTable();
        headingLabel.setText(topicName + " - message overview");

        List<CustomMessage> customMessages = JMSClient.getJmsClient().getMessagesByTopic(topicName);
        ObservableList<CustomMessage> customMessageList = FXCollections.observableArrayList(customMessages);
        messageTable.setItems(customMessageList);
        messageTable.getSortOrder().add(dateColumn);
        dateColumn.setComparator(dateColumn.getComparator().reversed());
        messageTable.sort();
    }

    private void formatTable() {
        detailsButtonColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<CustomMessage, Button> call(TableColumn<CustomMessage, Button> param) {
                return new TableCell<>() {

                    @Override
                    public void updateItem(Button item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Button detailsButton = new Button("Details");
                            detailsButton.getStyleClass().add("btn");

                            detailsButton.setOnAction(event -> {
                                MessagesReadController.topicName = topicName;
                                MessagesReadController.customMessage = getTableView().getItems().get(getIndex());
                                SceneManager.getInstance().switchView(SceneManager.VIEW_MESSAGES_READ);
                            });

                            setGraphic(detailsButton);
                        }
                    }
                };
            }
        });
    }

    public void onBackButtonClicked() {
        SceneManager.getInstance().switchView(SceneManager.VIEW_MESSAGES_READ_CHANNELS);
    }

}