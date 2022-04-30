module at.fhv.ec.javafxclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires activemq.all;
    requires java.naming;
    requires java.datatransfer;
    requires java.desktop;
    requires RMI.Shared.Lib.v46;

    exports at.fhv.ec.javafxclient;
    opens at.fhv.ec.javafxclient to javafx.fxml;

    exports at.fhv.ec.javafxclient.view.controller;
    opens at.fhv.ec.javafxclient.view.controller to javafx.fxml;
}