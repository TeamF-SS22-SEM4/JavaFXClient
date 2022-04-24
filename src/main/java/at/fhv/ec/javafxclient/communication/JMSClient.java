package at.fhv.ec.javafxclient.communication;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class JMSClient {
    private static JMSClient jmsClient;
    private final String PROTOCOL = "tcp";
    private final String HOST = "localhost";
    private final String PORT = "61616";

    private JMSClient() {}

    public static JMSClient getJmsClient() {
        if(jmsClient == null) {
            jmsClient = new JMSClient();
        }

        return jmsClient;
    }

    public void getMessagesByTopic(String topicName) throws JMSException {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(PROTOCOL + "://" + HOST + ":" + PORT);

        TopicConnection connection = (TopicConnection) connectionFactory.createConnection();
        connection.setClientID("DurableSubscriber");
        connection.start();

        TopicSession session = connection.createTopicSession(false, Session.CLIENT_ACKNOWLEDGE);
        
        Topic destination = session.createTopic(topicName);

        MessageConsumer consumer = session.createDurableSubscriber(destination, "Listener");

        // Listening for messages from publisher
        consumer.setMessageListener(System.out::println);

        /*
        consumer.close();
        session.close();
        connection.close();
        */
    }
}
