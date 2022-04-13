package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.SessionManager;
import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ss22.ea.f.communication.api.AuthenticationService;
import at.fhv.ss22.ea.f.communication.dto.LoginResultDTO;
import at.fhv.ss22.ea.f.communication.exception.AuthenticationFailed;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private AuthenticationService authenticationService;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Label infoText;

    @FXML
    private void onLoginButtonClicked() {

        if (username.getText().isEmpty() || password.getText().isEmpty()) {

            infoText.setTextFill(Color.web("#ff0000"));
            infoText.setText("Empty username or password!");

        } else {

            infoText.setTextFill(Color.web("#ffffff"));
            infoText.setText("Loading ...");

            try {

                authenticationService = RMIClient.getRmiClient().getRmiFactory().getAuthenticationService();
                LoginResultDTO loginResultDTO = authenticationService.login("tf-test", "PssWrd");
                SessionManager.getInstance().login(loginResultDTO.getSessionId(), loginResultDTO.getRoles());
                SceneManager.getInstance().switchView("shop");

            } catch (RemoteException e) {

                infoText.setTextFill(Color.web("#ff0000"));
                infoText.setText("Connection to the server could not be established!");

            } catch (AuthenticationFailed e) {

                infoText.setTextFill(Color.web("#ff0000"));
                infoText.setText("Invalid username or password!");

            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        username.setText("test");
        password.setText("test");

    }
}