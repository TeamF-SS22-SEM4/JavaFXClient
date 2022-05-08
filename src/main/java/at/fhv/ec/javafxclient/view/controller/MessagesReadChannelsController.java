package at.fhv.ec.javafxclient.view.controller;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.SessionManager;
import at.fhv.ec.javafxclient.communication.EJBClient;
import at.fhv.ec.javafxclient.communication.JMSClient;
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

import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.List;

public class MessagesReadChannelsController {

    @FXML
    private TableView<Topic> topicTable;
    @FXML
    private TableColumn<Topic, String> nameColumn;
    @FXML
    private TableColumn<Topic, Button> viewMessagesButtonColumn;

    @FXML
    public void initialize() {
        formatTable();

        try {
            List<Topic> topics = new ArrayList<>();
            List<String> subscribedTopics = EJBClient
                                                .getEjbClient()
                                                .getMessagingService()
                                                .getSubscribedTopics(SessionManager.getInstance().getSessionId());

            subscribedTopics.forEach(name -> topics.add(new Topic(name, JMSClient.getJmsClient().getAmountOfMessagesByTopic(name))));

            ObservableList<Topic> observableTopicList = FXCollections.observableArrayList(topics);
            topicTable.setItems(observableTopicList);
            topicTable.getSortOrder().add(nameColumn);
            topicTable.sort();
        } catch (SessionExpired e) {
            throw new RuntimeException(e);
        } catch (NoPermissionForOperation e) {
            throw new RuntimeException(e);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    private void formatTable() {
        viewMessagesButtonColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Topic, Button> call(TableColumn<Topic, Button> param) {
                return new TableCell<>() {

                    @Override
                    public void updateItem(Button item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Button viewMessagesButton = new Button("View messages");
                            viewMessagesButton.getStyleClass().add("btn");

                            viewMessagesButton.setOnAction(event -> {
                                MessagesReadOverviewController.topicName = getTableView().getItems().get(getIndex()).getName();
                                SceneManager.getInstance().switchView(SceneManager.VIEW_MESSAGES_READ_OVERVIEW);
                            });

                            setGraphic(viewMessagesButton);
                        }
                    }
                };
            }
        });
    }
}
