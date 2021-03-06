package at.fhv.ec.javafxclient.communication;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.SessionManager;
import at.fhv.ss22.ea.f.communication.dto.DetailedOrderDTO;
import at.fhv.ss22.ea.f.communication.dto.SoundCarrierOrderDTO;
import at.fhv.ss22.ea.f.communication.exception.NoPermissionForOperation;
import at.fhv.ss22.ea.f.communication.exception.SessionExpired;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;

import javax.jms.*;
import javax.naming.NamingException;
import java.io.IOException;
import java.lang.IllegalStateException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class OrderingClient {

    private static final String ORDER_TOPIC_NAME = "Orders";
    private static final String ORDER_CLIENT_CONNECTION_ID = "system_ordering_client";
    private static final String ORDER_CLIENT_NAME = "ordering_client";
    private static final String JMS_PORT = "61616";

    private ActiveMQConnectionFactory connectionFactory;
    private TopicConnection connection;
    private TopicSession session;
    private TopicSubscriber subscriber;
    private Topic topic;
    private List<ObjectMessage> messageList = new LinkedList<>();

    private OrderingClient() {}

    public void disconnect() {
        try {
            this.session.close();
            this.connection.stop();
            this.connection.close();
        } catch (JMSException | NullPointerException ignored) {}
    }

    public void connect(String host) {
        try {
            this.connectionFactory = new ActiveMQConnectionFactory("tcp://" + host + ":" + JMS_PORT);
            this.connectionFactory.setTrustAllPackages(true);
            connection = connectionFactory.createTopicConnection();
            connection.setClientID(ORDER_CLIENT_CONNECTION_ID);
            session = connection.createTopicSession(false, ActiveMQSession.INDIVIDUAL_ACKNOWLEDGE);
            topic = session.createTopic(ORDER_TOPIC_NAME);
            subscriber = session.createDurableSubscriber(topic, ORDER_CLIENT_NAME);
            connection.start();
            subscriber.setMessageListener(message -> {
                messageList.add((ObjectMessage) message);
            });
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public List<DetailedOrderDTO> getActiveOrders() {
        List<DetailedOrderDTO> orders = new LinkedList<>();

        for (ObjectMessage m : messageList) {
            try {
                orders.add( (DetailedOrderDTO) m.getObject());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
        return orders;
    }

    public void approveOrder(UUID orderId) {
        ObjectMessage message = this.messageList.stream().filter(msg -> {
            try {
                return ((DetailedOrderDTO) msg.getObject()).getOrderId().equals(orderId);
            } catch (JMSException e) {
                e.printStackTrace();
            }
            return false;
        }).findFirst().orElseThrow(IllegalStateException::new);

        try {
            this.messageList.remove(message);
            message.acknowledge();
            DetailedOrderDTO detailedOrderDTO = (DetailedOrderDTO) message.getObject();
            SoundCarrierOrderDTO orderDTO = SoundCarrierOrderDTO.builder()
                    .withOrderId(detailedOrderDTO.getOrderId())
                    .withAmount(detailedOrderDTO.getAmount())
                    .withCarrierId(detailedOrderDTO.getSoundCarrierId())
                    .build();
            EJBClient.getEjbClient().getOrderingService().approveOrder(SessionManager.getInstance().getSessionId(), orderDTO);
            //TODO replace, just first version of refreesh
            SceneManager.getInstance().switchView("order");
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (SessionExpired sessionExpired) {
            sessionExpired.printStackTrace();
        } catch (NoPermissionForOperation noPermissionForOperation) {
            noPermissionForOperation.printStackTrace();
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public void denyOrder(UUID orderId) {
        ObjectMessage message = this.messageList.stream().filter(msg -> {
            try {
                return ((DetailedOrderDTO) msg.getObject()).getOrderId().equals(orderId);
            } catch (JMSException e) {
                e.printStackTrace();
            }
            return false;
        }).findFirst().orElseThrow(IllegalStateException::new);

        try {
            this.messageList.remove(message);
            message.acknowledge();
            SceneManager.getInstance().switchView(SceneManager.VIEW_ORDERS);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    //Singleton implementation
    private static OrderingClient instance;
    static {
        instance = new OrderingClient();
    }
    public static OrderingClient getInstance() {
        return instance;
    }
}
