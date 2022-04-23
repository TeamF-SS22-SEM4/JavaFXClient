package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.SessionManager;
import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ss22.ea.f.communication.api.AuthenticationService;
import at.fhv.ss22.ea.f.communication.dto.LoginResultDTO;
import at.fhv.ss22.ea.f.communication.exception.AuthenticationFailed;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import java.io.IOException;
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
    private ChoiceBox<String> connectionType;

    private AuthenticationService authenticationService;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private Label infoText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connectionType.getItems().addAll(REMOTE, LOCAL);
        connectionType.setValue(REMOTE);
    }

    @FXML
    private void onLoginButtonClicked() {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        if (username.isEmpty() || password.isEmpty()) {

            infoText.setTextFill(Color.web("#ff0000"));
            infoText.setText("Empty username or password!");

        } else {

            infoText.setTextFill(Color.web("#ffffff"));
            infoText.setText("Loading ...");

            Platform.runLater(() -> {

                try {

                    if (connectionType.getValue().equals("local (port:1099)")) {
                        RMIClient.getRmiClient().connect(HOST_LOCAL, PORT_LOCAL);
                    } else {
                        RMIClient.getRmiClient().connect(HOST_REMOTE, PORT_REMOTE);
                    }

                    authenticationService = RMIClient.getRmiClient().getRmiFactory().getAuthenticationService();

                    // backdoor
                    // LoginResultDTO loginResultDTO = authenticationService.login("tf-test", "PssWrd");

                    // regular
                    LoginResultDTO loginResultDTO = authenticationService.login(username, password);

                    SessionManager.getInstance().login(loginResultDTO.getSessionId(), loginResultDTO.getRoles(), loginResultDTO.getTopicNames());
                    SceneManager.getInstance().switchView("shop");
                } catch (RemoteException | NotBoundException e) {
                    infoText.setTextFill(Color.web("#ff0000"));
                    infoText.setText("Connection to the server could not be established!");
                    e.printStackTrace();
                } catch (AuthenticationFailed e) {
                    infoText.setTextFill(Color.web("#ff0000"));
                    infoText.setText("Invalid username or password!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}