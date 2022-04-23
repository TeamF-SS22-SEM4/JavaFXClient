package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.SessionManager;
import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ec.javafxclient.view.utils.TopicListCell;
import at.fhv.ss22.ea.f.communication.exception.NoPermissionForOperation;
import at.fhv.ss22.ea.f.communication.exception.SessionExpired;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.rmi.RemoteException;
import java.util.List;

public class TopicListController {
    @FXML
    private ListView<String> topicListView;

    public void initialize() {
        topicListView.setCellFactory(lv -> new TopicListCell());

        if(SessionManager.getInstance().getRoles().contains("Operator")) {
            // Add all existing topics so the operator can send messages
            topicListView.getItems().add("Pop");
            topicListView.getItems().add("Metal");
            topicListView.getItems().add("Rock");
            topicListView.getItems().add("Electronic");
            topicListView.getItems().add("Grunge");
            topicListView.getItems().add("Hip-Hop");
            topicListView.getItems().add("Hard Rock");
        } else {
            // Add all subscribed topics of a non operator user
            try {
                List<String> subscribedTopics = RMIClient.getRmiClient()
                                                            .getRmiFactory()
                                                            .getMessagingService()
                                                            .getSubscribedTopics(SessionManager.getInstance().getSessionId());
                subscribedTopics.forEach(
                        topic -> topicListView.getItems().add(topic)
                );
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            } catch (SessionExpired e) {
                throw new RuntimeException(e);
            } catch (NoPermissionForOperation e) {
                throw new RuntimeException(e);
            }
        }
    }
}
