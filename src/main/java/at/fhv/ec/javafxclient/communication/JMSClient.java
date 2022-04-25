package at.fhv.ec.javafxclient.communication;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.List;

public class JMSClient {
    private static JMSClient jmsClient;
    private final String PROTOCOL = "tcp";
    private final String PORT = "61616";

    private String HOST;

    private JMSClient() {}

    public static JMSClient getJmsClient() {
        if(jmsClient == null) {
            jmsClient = new JMSClient();
        }

        return jmsClient;
    }

    public void connect(String host) throws JMSException {
        this.HOST = host;
    }

    public void startMessageListeners(List<String> topics, String employeeId) {
        // TODO: Avoid try and catch
        topics.forEach(topic -> {
            try {
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(PROTOCOL + "://" + HOST + ":" + PORT);
                TopicConnection connection = (TopicConnection) connectionFactory.createConnection();
                getMessagesByTopic(topic, connection, employeeId);
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void getMessagesByTopic(String topicName, TopicConnection connection, String employeeId) throws JMSException {
        connection.setClientID(topicName + "-" + employeeId);
        connection.start();

        TopicSession session = connection.createTopicSession(false, Session.CLIENT_ACKNOWLEDGE);

        Topic destination = session.createTopic(topicName);

        MessageConsumer consumer = session.createDurableSubscriber(destination, topicName + "-" + employeeId);

        // Listening for messages from publisher
        // TODO: Create something like a message storage
        consumer.setMessageListener(System.out::println);

        /*
        consumer.close();
        session.close();
        connection.close();
        */
    }
}
