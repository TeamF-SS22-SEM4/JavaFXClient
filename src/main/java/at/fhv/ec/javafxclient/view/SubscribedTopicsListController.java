package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.SessionManager;
import at.fhv.ec.javafxclient.communication.JMSClient;
import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ec.javafxclient.model.Topic;
import at.fhv.ss22.ea.f.communication.exception.NoPermissionForOperation;
import at.fhv.ss22.ea.f.communication.exception.SessionExpired;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class SubscribedTopicsListController {
    @FXML
    private TableView<Topic> topicTable;

    @FXML
    private TableColumn<Topic, String> nameColumn;

    @FXML
    private TableColumn<Topic, Button> viewMessagesButtonColumn;

    public void initialize() {
        initTable();

        // Add all subscribed topics of user
        try {
            List<Topic> topics = new ArrayList<>();
            List<String> subscribedTopics = RMIClient.getRmiClient()
                                                        .getRmiFactory()
                                                        .getMessagingService()
                                                        .getSubscribedTopics(SessionManager.getInstance().getSessionId());

            subscribedTopics.forEach(name ->
                    topics.add(new Topic(name, JMSClient.getJmsClient().getAmountOfMessagesByTopic(name)))
            );

            ObservableList<Topic> observableTopicList = FXCollections.observableArrayList(topics);
            topicTable.setItems(observableTopicList);
            topicTable.getSortOrder().add(nameColumn);
            topicTable.sort();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (SessionExpired e) {
            throw new RuntimeException(e);
        } catch (NoPermissionForOperation e) {
            throw new RuntimeException(e);
        }
    }

    private void initTable() {
        // Create buttons in table
        viewMessagesButtonColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Topic, Button> call(TableColumn<Topic, Button> param) {

                final Button viewMessagesButton = new Button("View Messages");
                viewMessagesButton.getStyleClass().add("btn");

                return new TableCell<>() {
                    @Override
                    public void updateItem(Button item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            viewMessagesButton.setOnAction(event -> {
                                MessageListController.topicName = getTableView().getItems().get(getIndex()).getName();
                                SceneManager.getInstance().switchView("message-list");
                            });

                            setGraphic(viewMessagesButton);
                            setText(null);
                        }
                    }
                };
            }
        });
    }
}
