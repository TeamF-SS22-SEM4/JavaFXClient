package at.fhv.ec.javafxclient.communication;

import at.fhv.ec.javafxclient.SessionManager;
import at.fhv.ec.javafxclient.model.CustomMessage;
import at.fhv.ss22.ea.f.communication.exception.NoPermissionForOperation;
import at.fhv.ss22.ea.f.communication.exception.SessionExpired;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;

import javax.jms.*;
import java.rmi.RemoteException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

public class JMSClient {

    private static JMSClient jmsClient;
    private final String PROTOCOL = "tcp";
    private final String PORT = "61616";

    private ActiveMQConnectionFactory connectionFactory;
    private Map<String, TopicConnection> connections;
    private Map<String, Session> sessions;
    private Map<String, MessageConsumer> consumers;
    private Map<String, ArrayList<CustomMessage>> messages;
    private Map<String, Message> jmsMessages;

    private int amountOfNewMessages;

    private JMSClient() {}

    public static JMSClient getJmsClient() {
        if(jmsClient == null) {
            jmsClient = new JMSClient();
            jmsClient.connections = new HashMap<>();
            jmsClient.sessions = new HashMap<>();
            jmsClient.consumers = new HashMap<>();
            jmsClient.messages = new HashMap<>();
            jmsClient.jmsMessages = new HashMap<>();
            jmsClient.amountOfNewMessages = 0;
        }

        return jmsClient;
    }

    public void connect(String host) throws JMSException {
        connectionFactory = new ActiveMQConnectionFactory(PROTOCOL + "://" + host + ":" + PORT);
    }

    public void startMessageListeners(List<String> topics, String employeeId) {
        topics.forEach(topic -> {
            try {
                TopicConnection connection = (TopicConnection) connectionFactory.createConnection();

                connection.setClientID(topic + "-" + employeeId);
                connection.start();

                TopicSession session = connection.createTopicSession(false, ActiveMQSession.INDIVIDUAL_ACKNOWLEDGE);

                Topic destination = session.createTopic(topic);

                MessageConsumer consumer = session.createDurableSubscriber(destination, topic + "-" + employeeId);

                // Listening for messages from publisher
                consumer.setMessageListener(message -> {
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        jmsMessages.put(message.getJMSMessageID(), message); // Save original message to acknowledge it later

                        String title = textMessage.getText().split("\n")[0];
                        String content = textMessage.getText().split("\n")[1];
                        addMessageToTopic(topic, new CustomMessage(message.getJMSMessageID(), title, content, new Date(message.getJMSTimestamp())));

                        LocalDateTime lastViewed = RMIClient.getRmiClient()
                                .getRmiFactory()
                                .getMessagingService()
                                .getLastViewed(SessionManager.getInstance().getSessionId());

                        LocalDateTime messageDateTime = LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(message.getJMSTimestamp()),
                                TimeZone.getDefault().toZoneId()
                        );

                        if(messageDateTime.isAfter(lastViewed)) {
                            SessionManager.getInstance().onNewMessageReceived();
                            amountOfNewMessages += 1;
                        }
                    } catch (JMSException | SessionExpired | RemoteException | NoPermissionForOperation e) {
                        throw new RuntimeException(e);
                    }
                });

                connections.put(topic, connection);
                sessions.put(topic, session);
                consumers.put(topic, consumer);
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void addMessageToTopic(String key, CustomMessage value) {
        ArrayList<CustomMessage> tempList;

        if (messages.containsKey(key)) {
            tempList = messages.get(key);
            if(tempList == null)
                tempList = new ArrayList<>();
            tempList.add(value);
        } else {
            tempList = new ArrayList<>();
            tempList.add(value);
        }

        messages.put(key,tempList);
    }

    public List<CustomMessage> getMessagesByTopic(String topicName) {
        List<CustomMessage> messageList = messages.get(topicName);
        return messageList == null ? Collections.emptyList() : messageList;
    }

    public int getAmountOfMessagesByTopic(String topicName) {
        return messages.get(topicName) == null ? 0 : messages.get(topicName).size();
    }

    public int getAmountOfNewMessages() {
        return this.amountOfNewMessages;
    }

    public void acknowledgeMessage(String topicName, CustomMessage customMessage) throws JMSException, NoSuchElementException {
        Message message = jmsMessages.get(customMessage.getJmsId());
        List<CustomMessage> topicMessages = messages.get(topicName);

        if(message == null) {
            throw new NoSuchElementException("Couldn't find message");
        }

        message.acknowledge();
        jmsMessages.remove(customMessage.getJmsId());
        topicMessages.remove(customMessage);
    }

    public void disconnect() {
        // Close all connections
        consumers.forEach((topic , consumer) -> {
            try {
                consumer.close();
            } catch (JMSException ignored) {

            }
        });

        sessions.forEach((topic , session) -> {
            try {
                session.close();
            } catch (JMSException ignored) {

            }
        });

        connections.forEach((topic , connection) -> {
            try {
                connection.close();
            } catch (JMSException ignored) {

            }
        });

        consumers.clear();
        sessions.clear();
        connections.clear();
        jmsMessages.clear();
        messages.clear();
        amountOfNewMessages = 0;
    }
}