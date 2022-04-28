package at.fhv.ec.javafxclient.view.controller;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.communication.JMSClient;
import at.fhv.ec.javafxclient.model.CustomMessage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.util.List;

public class MessageListController {
    public static String topicName;

    @FXML
    private TableView<CustomMessage> messageTable;

    @FXML
    private TableColumn<CustomMessage, Button> detailsButtonColumn;

    public void initialize() {
        initTable();

        List<CustomMessage> customMessages = JMSClient.getJmsClient().getMessagesByTopic(topicName);
        ObservableList<CustomMessage> customMessageList = FXCollections.observableArrayList(customMessages);
        messageTable.setItems(customMessageList);
    }

    private void initTable() {
        detailsButtonColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<CustomMessage, Button> call(TableColumn<CustomMessage, Button> param) {

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
                            detailsButton.setOnAction(event -> {
                                MessageDetailsController.topicName = topicName;
                                MessageDetailsController.customMessage = getTableView().getItems().get(getIndex());
                                SceneManager.getInstance().switchView(SceneManager.VIEW_MESSAGES_READ_DETAIL);
                            });

                            setGraphic(detailsButton);
                            setText(null);
                        }
                    }
                };
            }
        });
    }
}
