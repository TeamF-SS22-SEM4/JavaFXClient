package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ss22.ea.f.communication.api.AuthenticationService;
import at.fhv.ss22.ea.f.communication.dto.LoginResultDTO;
import at.fhv.ss22.ea.f.communication.exception.AuthenticationFailed;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.rmi.RemoteException;

public class LoginController {
    private AuthenticationService authenticationService;

    public static LoginResultDTO sessionInformation;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private Label statusLabel;

    @FXML
    protected void onLoginButtonClicked() {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        System.out.println("Username: " + username);
        System.out.println("Password: " + password);

        if((username != null) && (password != null)) {
            try {
                authenticationService = RMIClient.getRmiClient().getRmiFactory().getAuthenticationService();
                sessionInformation = authenticationService.login(username, password);

                System.out.println(sessionInformation.getSessionId());
                sessionInformation.getRoles().forEach(System.out::println);
            } catch (RemoteException e) {
                statusLabel.setText("Could not establish a connection to the server");
            } catch (AuthenticationFailed e) {
                statusLabel.setText("Invalid username or password");
            }
        }
    }
}