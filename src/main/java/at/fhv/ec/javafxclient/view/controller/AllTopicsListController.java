package at.fhv.ec.javafxclient.view.controller;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.model.Topic;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.util.List;

public class AllTopicsListController {
    @FXML
    private TableView<Topic> topicTable;

    @FXML
    private TableColumn<Topic, String> nameColumn;

    @FXML
    private TableColumn<Topic, Button> newMessageButtonColumn;

    public void initialize() {
        initTable();

        List<Topic> topics = List.of(
                new Topic("Pop"),
                new Topic("Metal"),
                new Topic("Rock"),
                new Topic("Electronic"),
                new Topic("Grunge"),
                new Topic("Hip-Hop"),
                new Topic("Hard Rock")
        );

        ObservableList<Topic> observableTopicList = FXCollections.observableArrayList(topics);
        topicTable.setItems(observableTopicList);
        topicTable.getSortOrder().add(nameColumn);
        topicTable.sort();
    }

    private void initTable() {
        // Create buttons in table
        newMessageButtonColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Topic, Button> call(TableColumn<Topic, Button> param) {

                final Button newMessageButton = new Button("New Message");
                newMessageButton.getStyleClass().add("btn");

                return new TableCell<>() {
                    @Override
                    public void updateItem(Button item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            newMessageButton.setOnAction(event -> {
                                SendMessageController.topicName = getTableView().getItems().get(getIndex()).getName();
                                SceneManager.getInstance().switchView(SceneManager.VIEW_MESSAGES_SEND);
                            });
                            setGraphic(newMessageButton);
                            setText(null);
                        }
                    }
                };
            }
        });
    }
}
