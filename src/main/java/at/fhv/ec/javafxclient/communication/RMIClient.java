package at.fhv.ec.javafxclient.communication;

import at.fhv.ss22.ea.f.communication.api.RMIFactory;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RMIClient {
    // Instance as singleton
    private static RMIClient rmiClient;
    private RMIFactory rmiFactory;
    //private static int PORT = 1099; // LocalDevEnv
    private static int PORT = 12345; // Server
    private static String PROTOCOL = "rmi://";
    private static String HOST = "10.0.40.170"; // Server
    //private static String HOST = "localhost"; // LocalDevEnv
    private static String STUB = "/RMIFactory";

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
