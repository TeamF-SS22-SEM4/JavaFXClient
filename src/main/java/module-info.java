module at.fhv.ec.javafxclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires activemq.all;
    requires java.naming;
    requires RMI.Shared.Lib.v39;
    requires java.datatransfer;
    requires java.desktop;


    exports at.fhv.ec.javafxclient;
    opens at.fhv.ec.javafxclient to javafx.fxml;

    exports at.fhv.ec.javafxclient.view.controller;
    opens at.fhv.ec.javafxclient.view.controller to javafx.fxml;
}