module at.fhv.ec.javafxclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires activemq.all;
    requires java.naming;
    requires RMI.Shared.Lib.v36;

    opens at.fhv.ec.javafxclient to javafx.fxml;
    exports at.fhv.ec.javafxclient;
    exports at.fhv.ec.javafxclient.view;
    opens at.fhv.ec.javafxclient.view to javafx.fxml;
}