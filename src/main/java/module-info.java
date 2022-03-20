module at.fhv.ec.javafxclient {
    requires javafx.controls;
    requires javafx.fxml;


    opens at.fhv.ec.javafxclient to javafx.fxml;
    exports at.fhv.ec.javafxclient;
    exports at.fhv.ec.javafxclient.application.dto;
    opens at.fhv.ec.javafxclient.application.dto to javafx.fxml;
    exports at.fhv.ec.javafxclient.application.api;
    opens at.fhv.ec.javafxclient.application.api to javafx.fxml;
    exports at.fhv.ec.javafxclient.view;
    opens at.fhv.ec.javafxclient.view to javafx.fxml;
}