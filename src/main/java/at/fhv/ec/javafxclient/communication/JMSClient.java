package at.fhv.ec.javafxclient.communication;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.*;

public class JMSClient {
    private static JMSClient jmsClient;
    private final String PROTOCOL = "tcp";
    private final String PORT = "61616";

    private ActiveMQConnectionFactory connectionFactory;
    private Map<String, TopicConnection> connections;
    private Map<String, Session> sessions;
    private Map<String, MessageConsumer> consumers;
    private Map<String, ArrayList<String>> messages;

    private JMSClient() {}

    public static JMSClient getJmsClient() {
        if(jmsClient == null) {
            jmsClient = new JMSClient();
            jmsClient.connections = new HashMap<>();
            jmsClient.sessions = new HashMap<>();
            jmsClient.consumers = new HashMap<>();
            jmsClient.messages = new HashMap<>();
        }

        return jmsClient;
    }

    public void connect(String host) throws JMSException {
        connectionFactory = new ActiveMQConnectionFactory(PROTOCOL + "://" + host + ":" + PORT);
    }

    public void startMessageListeners(List<String> topics, String employeeId) {
        // TODO: find better solution
        topics.forEach(topic -> {
            try {
                TopicConnection connection = (TopicConnection) connectionFactory.createConnection();

                connection.setClientID(topic + "-" + employeeId);
                connection.start();

                TopicSession session = connection.createTopicSession(false, Session.CLIENT_ACKNOWLEDGE);

                Topic destination = session.createTopic(topic);

                MessageConsumer consumer = session.createDurableSubscriber(destination, topic + "-" + employeeId);

                // Listening for messages from publisher
                consumer.setMessageListener(message -> {
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        addMessageToTopic(topic, textMessage.getText());
                    } catch (JMSException e) {
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

    private void addMessageToTopic(String key, String value) {
        ArrayList<String> tempList;

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

    public void logout() {
        // TODO: Find better solution
        // Close all connections
        consumers.forEach((k , v) -> {
            try {
                v.close();
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        });

        sessions.forEach((k , v) -> {
            try {
                v.close();
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        });

        connections.forEach((k , v) -> {
            try {
                v.close();
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public List<String> getMessagesByTopic(String topicName) {
        return messages.get(topicName);
    }
}
