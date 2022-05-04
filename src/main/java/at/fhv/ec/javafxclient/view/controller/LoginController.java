package at.fhv.ec.javafxclient.view.controller;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.SessionManager;
import at.fhv.ec.javafxclient.communication.JMSClient;
import at.fhv.ec.javafxclient.communication.OrderingClient;
import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ss22.ea.f.communication.api.AuthenticationService;
import at.fhv.ss22.ea.f.communication.dto.LoginResultDTO;
import at.fhv.ss22.ea.f.communication.exception.AuthenticationFailed;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javax.jms.JMSException;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class LoginController {

    private AuthenticationService authenticationService;

    private static final String REMOTE_HOST = "10.0.40.170";
    private static final String REMOTE_PORT = "12345";
    private static final String REMOTE_INFORMATION_TEXT = "remote (port:12345)";
    private static final String LOCAL_HOST = "localhost";
    private static final String LOCAL_PORT = "1099";
    private static final String LOCAL_INFORMATION_TEXT = "local (port:1099)";

    @FXML
    private ChoiceBox<String> connectionTypeChoiceBox;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private Label infoLabel;

    @FXML
    public void initialize() {
        connectionTypeChoiceBox.getItems().addAll(REMOTE_INFORMATION_TEXT, LOCAL_INFORMATION_TEXT);
        connectionTypeChoiceBox.setValue(LOCAL_INFORMATION_TEXT);
    }

    @FXML
    public void onLoginButtonClicked() {
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
                    if (connectionTypeChoiceBox.getValue().equals(LOCAL_INFORMATION_TEXT)) {
                        RMIClient.getRmiClient().connect(LOCAL_HOST, LOCAL_PORT);
                        JMSClient.getJmsClient().connect(LOCAL_HOST);

                    } else {
                        RMIClient.getRmiClient().connect(REMOTE_HOST, REMOTE_PORT);
                        JMSClient.getJmsClient().connect(REMOTE_HOST);
                    }

                    authenticationService = RMIClient.getRmiClient().getRmiFactory().getAuthenticationService();
                    LoginResultDTO loginResultDTO = authenticationService.login(username, password);

                    SessionManager.getInstance().login(loginResultDTO.getUsername(), loginResultDTO.getSessionId(), loginResultDTO.getRoles());
                    JMSClient.getJmsClient().startMessageListeners(loginResultDTO.getTopicNames(), loginResultDTO.getEmployeeId());

                    if (loginResultDTO.getRoles().contains("Operator")) {
                        if (connectionTypeChoiceBox.getValue().equals(LOCAL_INFORMATION_TEXT)) {
                            OrderingClient.getInstance().connect(LOCAL_HOST);

                        } else {
                            OrderingClient.getInstance().connect(REMOTE_HOST);
                        }
                    }

                    SceneManager.getInstance().switchView(SceneManager.VIEW_SHOP);

                } catch (RemoteException | NotBoundException e) {

                    infoLabel.getStyleClass().add("alert");
                    infoLabel.setText("Connection to the server could not be established!");

                } catch (AuthenticationFailed e) {

                    infoLabel.getStyleClass().add("alert");
                    infoLabel.setText("Invalid username or password!");

                } catch (IOException ignored) {

                } catch (JMSException e) {

                    throw new RuntimeException(e);
                }
            });
        }
    }

    @FXML
    public void onLinkClicked() {
        try {
            Desktop.getDesktop().browse(new URL("https://www.windows-faq.de/2018/07/28/bildschirmanzeige-bei-windows-10-skalieren-schriftgroesse-veraendern/").toURI());
        } catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }
}