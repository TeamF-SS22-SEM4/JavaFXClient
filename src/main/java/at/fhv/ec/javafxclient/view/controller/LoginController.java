package at.fhv.ec.javafxclient.view.controller;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.SessionManager;
import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ss22.ea.f.communication.api.AuthenticationService;
import at.fhv.ss22.ea.f.communication.dto.LoginResultDTO;
import at.fhv.ss22.ea.f.communication.exception.AuthenticationFailed;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private static final String HOST_REMOTE = "10.0.40.170";
    private static final String PORT_REMOTE = "12345";
    private static final String HOST_LOCAL = "localhost";
    private static final String PORT_LOCAL = "1099";

    private static final String REMOTE = "remote (port:12345)";
    private static final String LOCAL = "local (port:1099)";

    @FXML
    private ChoiceBox connectionType;

    private AuthenticationService authenticationService;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private Label infoLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connectionType.getItems().addAll(REMOTE, LOCAL);
        connectionType.setValue(REMOTE);

        usernameTextField.setText("tf-test");
        passwordTextField.setText("PssWrd");
    }

    @FXML
    private void onLoginButtonClicked() {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        infoLabel.getStyleClass().remove("alert");

        if (username.isEmpty() || password.isEmpty()) {

            infoLabel.getStyleClass().add("alert");
            infoLabel.setText("Empty username or password!");

        } else {

            infoLabel.setText("Loading ...");

            Platform.runLater(() -> {

                try {

                    if (connectionType.getValue().toString().equals(LOCAL)) {
                        RMIClient.getRmiClient().connect(HOST_LOCAL, PORT_LOCAL);
                    } else {
                        RMIClient.getRmiClient().connect(HOST_REMOTE, PORT_REMOTE);
                    }

                    authenticationService = RMIClient.getRmiClient().getRmiFactory().getAuthenticationService();
                    LoginResultDTO loginResultDTO = authenticationService.login(username, password);
                    SessionManager.getInstance().login(loginResultDTO.getSessionId(), loginResultDTO.getRoles());
                    SceneManager.getInstance().switchView("shop");

                } catch (RemoteException | NotBoundException e) {

                    infoLabel.getStyleClass().add("alert");
                    infoLabel.setText("Connection to the server could not be established!");

                } catch (AuthenticationFailed e) {

                    infoLabel.getStyleClass().add("alert");
                    infoLabel.setText("Invalid username or password!");

                } catch (IOException e) {

                    e.printStackTrace();

                }
            });

        }
    }

    public void onLinkClicked(ActionEvent actionEvent) {
            try {

                Desktop.getDesktop().browse(new URL("https://www.windows-faq.de/2018/07/28/bildschirmanzeige-bei-windows-10-skalieren-schriftgroesse-veraendern/").toURI());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
    }
}