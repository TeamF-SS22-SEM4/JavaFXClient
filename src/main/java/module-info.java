module at.fhv.ec.javafxclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires RMI.Shared.Lib.v34;
    requires activemq.all;
    requires java.naming;

    opens at.fhv.ec.javafxclient to javafx.fxml;
    exports at.fhv.ec.javafxclient;
    exports at.fhv.ec.javafxclient.view;
    opens at.fhv.ec.javafxclient.view to javafx.fxml;
}