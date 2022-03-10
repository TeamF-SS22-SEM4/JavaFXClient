module at.fhv.ec.javafxclient {
    requires javafx.controls;
    requires javafx.fxml;


    opens at.fhv.ec.javafxclient to javafx.fxml;
    exports at.fhv.ec.javafxclient;
}