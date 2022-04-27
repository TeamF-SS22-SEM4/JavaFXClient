package at.fhv.ec.javafxclient.view.controller;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.SessionManager;
import at.fhv.ec.javafxclient.communication.JMSClient;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SidebarController implements Initializable {

    @FXML
    private Button topicsButton;

    public ToggleGroup themeStyleToggleGroup;
    public ToggleGroup themeColorToggleGroup;
    public ToggleButton dark;
    public Button logoutButton;
    public ImageView logoImageView;

    @FXML
    private void onLogoClicked() {
        SceneManager.getInstance().switchView("shop");
    }
    @FXML
    private void onShopButtonClicked() {
        SceneManager.getInstance().switchView("shop");
    }

    @FXML
    private void onExchangeButtonClicked() {
        SceneManager.getInstance().switchView("exchange");
    }

    @FXML
    private void onCustomerButtonClicked() {
        SceneManager.getInstance().switchView("customer");
    }

    @FXML
    private void onOrderButtonClicked() throws IOException {
        SceneManager.getInstance().switchView("order");
    }

    @FXML
    private void onMessageButtonClicked() {
        SceneManager.getInstance().switchView("subscribed-topics-list");
    }

    @FXML
    private void onTopicsButtonClicked() {
        SceneManager.getInstance().switchView("all-topics-list");
    }

    @FXML
    private void onThemeButtonsClicked() {
        ToggleButton toggleButtonStyle = (ToggleButton) themeStyleToggleGroup.getSelectedToggle();
        String themeStyle = toggleButtonStyle.getId();

        ToggleButton toggleButtonColor = (ToggleButton) themeColorToggleGroup.getSelectedToggle();
        String themeColor = toggleButtonColor.getId();

        SceneManager.getInstance().switchTheme(themeStyle, themeColor);
    }

    @FXML
    private void onLogoutButtonClicked() {
        SessionManager.getInstance().logout();
        SceneManager.getInstance().logout();
        JMSClient.getJmsClient().logout();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        themeStyleToggleGroup.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null)
                oldVal.setSelected(true);
        });

        themeColorToggleGroup.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null)
                oldVal.setSelected(true);
        });

        logoutButton.setText("Log out\njmo8620 FAKE");

        if(!SessionManager.getInstance().getRoles().contains("Operator")) {
            topicsButton.setVisible(false);
        }
    }
}