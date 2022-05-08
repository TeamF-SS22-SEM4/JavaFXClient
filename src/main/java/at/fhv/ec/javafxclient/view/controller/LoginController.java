package at.fhv.ec.javafxclient.view.controller;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.SessionManager;
import at.fhv.ec.javafxclient.communication.EJBClient;
import at.fhv.ec.javafxclient.communication.JMSClient;
import at.fhv.ec.javafxclient.communication.OrderingClient;
import at.fhv.ss22.ea.f.communication.api.AuthenticationService;
import at.fhv.ss22.ea.f.communication.dto.LoginResultDTO;
import at.fhv.ss22.ea.f.communication.exception.AuthenticationFailed;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javax.jms.JMSException;
import javax.naming.NamingException;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController {

    private AuthenticationService authenticationService;

    private static final String REMOTE_HOST = "10.0.40.170";
    private static final String REMOTE_INFORMATION_TEXT = "remote";

    private static final String LOCAL_HOST = "localhost";
    private static final String LOCAL_INFORMATION_TEXT = "local";

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
                        EJBClient.getEjbClient().connect(LOCAL_HOST);
                        JMSClient.getJmsClient().connect(LOCAL_HOST);

                    } else {
                        EJBClient.getEjbClient().connect(REMOTE_HOST);
                        JMSClient.getJmsClient().connect(REMOTE_HOST);
                    }

                    authenticationService = EJBClient.getEjbClient().getAuthenticationService();
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


                } catch (AuthenticationFailed e) {

                    infoLabel.getStyleClass().add("alert");
                    infoLabel.setText("Invalid username or password!");

                } catch (IOException ignored) {

                } catch (JMSException e) {
                    throw new RuntimeException(e);
                } catch (NamingException e) {
                    infoLabel.getStyleClass().add("alert");
                    infoLabel.setText("Connection to the server could not be established!");
                    e.printStackTrace();
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