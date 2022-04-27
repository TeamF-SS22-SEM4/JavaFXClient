package at.fhv.ec.javafxclient.communication;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.SessionManager;
import at.fhv.ss22.ea.f.communication.dto.DetailedOrderDTO;
import at.fhv.ss22.ea.f.communication.dto.SoundCarrierOrderDTO;
import at.fhv.ss22.ea.f.communication.exception.NoPermissionForOperation;
import at.fhv.ss22.ea.f.communication.exception.SessionExpired;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.springframework.beans.factory.support.ScopeNotActiveException;

import javax.jms.*;
import java.io.IOException;
import java.lang.IllegalStateException;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderingClient {
    private static final String ORDER_TOPIC_NAME = "Orders";
    private static final String ORDER_CLIENT_CONNECTION_ID = "system_ordering_client";
    private static final String ORDER_CLIENT_NAME = "ordering_client";

    private ActiveMQConnectionFactory connectionFactory;
    private TopicConnection connection;
    private TopicSession session;
    private TopicSubscriber subscriber;
    private Topic topic;
    private List<ObjectMessage> messageList = new LinkedList<>();

    private OrderingClient() {
        this.connect();
    }
    public void connect() {
        try {
            this.connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            this.connectionFactory.setTrustAllPackages(true);
            connection = connectionFactory.createTopicConnection();
            connection.setClientID(ORDER_CLIENT_CONNECTION_ID);
            session = connection.createTopicSession(false, ActiveMQSession.INDIVIDUAL_ACKNOWLEDGE);
            topic = session.createTopic(ORDER_TOPIC_NAME);
            subscriber = session.createDurableSubscriber(topic, ORDER_CLIENT_NAME);
            connection.start();
            subscriber.setMessageListener(message -> {
                messageList.add((ObjectMessage) message);
                System.out.println(message);
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
            RMIClient.getRmiClient().getRmiFactory().getOrderingService().approveOrder(SessionManager.getInstance().getSessionId(), orderDTO);
            //TODO replace, just first version of refreesh
            SceneManager.getInstance().switchView("order");
        } catch (JMSException | IOException e) {
            e.printStackTrace();
        } catch (SessionExpired sessionExpired) {
            sessionExpired.printStackTrace();
        } catch (NoPermissionForOperation noPermissionForOperation) {
            noPermissionForOperation.printStackTrace();
        }
        System.out.println("approved");
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
            //TODO replace, just first version of refreesh
            SceneManager.getInstance().switchView("order");
        } catch (JMSException e) {
            e.printStackTrace();
        }
        System.out.println("denied");

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
