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

import javax.jms.JMSException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class TopicListController {
    @FXML
    private TableView<Topic> topicTable;

    @FXML
    private TableColumn<Topic, Button> viewMessagesButtonColumn;

    @FXML
    private TableColumn<Topic, Button> newMessageButtonColumn;

    public void initialize() {
        initTable();

        ObservableList<Topic> observableTopicList;

        // Set all buttons initially on not visible
        viewMessagesButtonColumn.setVisible(false);
        newMessageButtonColumn.setVisible(false);

        if(SessionManager.getInstance().getRoles().contains("Operator")) {
            // Whitelist of visible buttons
            newMessageButtonColumn.setVisible(true);

            // Add all existing topics so the operator can send messages
            List<Topic> topics = List.of(
                    new Topic("Pop"),
                    new Topic("Metal"),
                    new Topic("Rock"),
                    new Topic("Electronic"),
                    new Topic("Grunge"),
                    new Topic("Hip-Hop"),
                    new Topic("Hard Rock")
            );

            observableTopicList = FXCollections.observableArrayList(topics);
        } else {
            // Whitelist of visible buttons
            viewMessagesButtonColumn.setVisible(true);

            // Add all subscribed topics of a non operator user
            try {
                List<Topic> topics = new ArrayList<>();
                List<String> subscribedTopics = RMIClient.getRmiClient()
                                                            .getRmiFactory()
                                                            .getMessagingService()
                                                            .getSubscribedTopics(SessionManager.getInstance().getSessionId());


                subscribedTopics.forEach(name -> topics.add(new Topic(name)));
                observableTopicList = FXCollections.observableArrayList(topics);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            } catch (SessionExpired e) {
                throw new RuntimeException(e);
            } catch (NoPermissionForOperation e) {
                throw new RuntimeException(e);
            }
        }

        topicTable.setItems(observableTopicList);
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
                                try {
                                    MessageListController.topicName = getTableView().getItems().get(getIndex()).getName();
                                    SceneManager.getInstance().switchView("message-list");
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });

                            setGraphic(viewMessagesButton);
                            setText(null);
                        }
                    }
                };
            }
        });

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
                                try {
                                    SendMessageController.topicName = getTableView().getItems().get(getIndex()).getName();
                                    SceneManager.getInstance().switchView("send-message");
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
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
