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

    private static final String HOST = "10.0.40.170"; // Server
    private static final int PORT = 12345;            // Server
//    private static final String HOST = "localhost";     // LocalDevEnv
//    private static final int PORT = 1099;               // LocalDevEnv

    private RMIClient() {
        try {
            rmiFactory = (RMIFactory) Naming.lookup(PROTOCOL + HOST + ":" + PORT + STUB);
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
    }

    public static RMIClient getRmiClient() {
        if(rmiClient == null) {
            rmiClient = new RMIClient();
        }
        return rmiClient;
    }

    public RMIFactory getRmiFactory() {
        return rmiFactory;
    }
}
