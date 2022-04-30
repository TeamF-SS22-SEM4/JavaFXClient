package at.fhv.ec.javafxclient.communication;

import at.fhv.ss22.ea.f.communication.api.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class EJBClient {
    private static EJBClient ejbClient;
    private static final String PROTOCOL = "http-remoting";
    private static final String PORT = "8080";
    private Context initialContext;

    private EJBClient() {}

    public static EJBClient getEjbClient() {
        if(ejbClient == null) {
            ejbClient = new EJBClient();
        }

        return ejbClient;
    }

    public void connect(String HOST) throws NamingException {
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        props.put(Context.PROVIDER_URL, PROTOCOL + "://" + HOST + ":" + PORT);
        initialContext = new InitialContext(props);
    }

    public AuthenticationService getAuthenticationService() throws NamingException {
        return (AuthenticationService) initialContext.lookup(
                "ejb:/MusicShopBackend-1.0-SNAPSHOT/AuthenticationServiceImpl!at.fhv.ss22.ea.f.communication.api.AuthenticationService"
        );
    }

    public BuyingService getBuyingService() throws NamingException {
        return (BuyingService) initialContext.lookup(
                "ejb:/MusicShopBackend-1.0-SNAPSHOT/BuyingServiceImpl!at.fhv.ss22.ea.f.communication.api.BuyingService"
        );
    }

    public CustomerService getCustomerService() throws NamingException {
        return (CustomerService) initialContext.lookup(
                "ejb:/MusicShopBackend-1.0-SNAPSHOT/CustomerSearchService!at.fhv.ss22.ea.f.communication.api.CustomerService"
        );
    }

    public MessagingService getMessagingService() throws NamingException {
        return (MessagingService) initialContext.lookup(
                "ejb:/MusicShopBackend-1.0-SNAPSHOT/MessagingServiceServant!at.fhv.ss22.ea.f.communication.api.MessagingService"
        );
    }

    public OrderingService getOrderingService() throws NamingException {
        return (OrderingService) initialContext.lookup(
                "ejb:/MusicShopBackend-1.0-SNAPSHOT/OrderingServiceImpl!at.fhv.ss22.ea.f.communication.api.OrderingService"
        );
    }

    public ProductSearchService getProductSearchService() throws NamingException {
        return (ProductSearchService) initialContext.lookup(
                "ejb:/MusicShopBackend-1.0-SNAPSHOT/ProductSearchServiceImpl!at.fhv.ss22.ea.f.communication.api.ProductSearchService"
        );
    }

    public RefundSaleService getRefundSaleService() throws NamingException {
        return (RefundSaleService) initialContext.lookup(
                "ejb:/MusicShopBackend-1.0-SNAPSHOT/RefundSaleServiceImpl!at.fhv.ss22.ea.f.communication.api.RefundSaleService"
        );
    }

    public SaleSearchService getSaleSearchService() throws NamingException {
        return (SaleSearchService) initialContext.lookup(
                "ejb:/MusicShopBackend-1.0-SNAPSHOT/SaleSearchServiceImpl!at.fhv.ss22.ea.f.communication.api.SaleSearchService"
        );
    }
}
