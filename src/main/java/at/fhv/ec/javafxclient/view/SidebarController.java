package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class SidebarController {

    @FXML
    private Button messageButton;

    @FXML
    private void initialize() {
        if(!SessionManager.getInstance().getRoles().contains("Operator")) {
            messageButton.setVisible(false);
        }
    }
    @FXML
    private void onLogoClicked() throws IOException {
        SceneManager.getInstance().switchView("shop");
    }
    @FXML
    private void onShopButtonClicked() throws IOException {
        SceneManager.getInstance().switchView("shop");
    }

    @FXML
    private void onExchangeButtonClicked() throws IOException {
        SceneManager.getInstance().switchView("exchange");
    }

    @FXML
    private void onCustomerButtonClicked() throws IOException {
        SceneManager.getInstance().switchView("customer");
    }

    @FXML
    private void onMessageButtonClicked() throws IOException {
        SceneManager.getInstance().switchView("send-message");
    }

    @FXML
    private void onLogoutButtonClicked() throws IOException {
        SessionManager.getInstance().logout();
        SceneManager.getInstance().logout();
    }
}
