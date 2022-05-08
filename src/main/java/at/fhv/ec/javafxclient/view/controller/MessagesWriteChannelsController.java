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

public class MessagesWriteChannelsController {

    @FXML
    private TableView<Topic> channelTable;
    @FXML
    private TableColumn<Topic, String> nameColumn;
    @FXML
    private TableColumn<Topic, Button> actionColumn;

    @FXML
    public void initialize() {
        formatTable();

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
        channelTable.setItems(observableTopicList);
        channelTable.getSortOrder().add(nameColumn);
        channelTable.sort();
    }

    private void formatTable() {
        actionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Topic, Button> call(TableColumn<Topic, Button> param) {
                return new TableCell<>() {

                    @Override
                    public void updateItem(Button item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Button newMessageButton = new Button("New message");
                            newMessageButton.getStyleClass().add("btn");

                            newMessageButton.setOnAction(event -> {
                                MessagesWriteController.topicName = getTableView().getItems().get(getIndex()).getName();
                                SceneManager.getInstance().switchView(SceneManager.VIEW_MESSAGES_WRITE);
                            });

                            setGraphic(newMessageButton);
                        }
                    }
                };
            }
        });
    }

}