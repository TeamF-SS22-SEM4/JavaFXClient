package at.fhv.ec.javafxclient.communication;

import at.fhv.ss22.ea.f.communication.api.RMIFactory;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RMIClient {

    private static RMIClient rmiClient;
    private RMIFactory rmiFactory;
    private static final String PROTOCOL = "rmi://";
    private static final String STUB = "/RMIFactory";

    private RMIClient() {}

    public static RMIClient getRmiClient() {
        if(rmiClient == null) {
            rmiClient = new RMIClient();
        }
        return rmiClient;
    }

    public void connect(String HOST, String PORT) throws MalformedURLException, NotBoundException, RemoteException {
        rmiFactory = (RMIFactory) Naming.lookup(PROTOCOL + HOST + ":" + PORT + STUB);
    }

    public RMIFactory getRmiFactory() {
        return rmiFactory;
    }
}