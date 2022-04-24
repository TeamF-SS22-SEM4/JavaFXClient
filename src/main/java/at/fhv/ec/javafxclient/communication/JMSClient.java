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

        Connection connection = connectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        
        Destination destination = session.createTopic(topicName);

        MessageConsumer consumer = session.createConsumer(destination);

        Message message = consumer.receive(1000);

        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            System.out.println("Received: " + text);
        } else {
            System.out.println("No messages received");
        }

        consumer.close();
        session.close();
        connection.close();
    }
}
