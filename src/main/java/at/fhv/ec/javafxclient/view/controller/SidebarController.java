package at.fhv.ec.javafxclient.view.controller;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.SessionManager;
import at.fhv.ec.javafxclient.communication.JMSClient;
import at.fhv.ec.javafxclient.communication.OrderingClient;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import java.net.URL;
import java.util.ResourceBundle;

public class SidebarController implements Initializable {
    @FXML
    private Button messageButton;
    @FXML
    private Button topicsButton;

    @FXML
    private Button orderButton;

    public ToggleGroup themeStyleToggleGroup;
    public ToggleGroup themeColorToggleGroup;
    public ToggleButton dark;
    public Button logoutButton;

    @FXML
    private void onLogoClicked() {
        SceneManager.getInstance().switchView(SceneManager.VIEW_SHOP);
    }
    @FXML
    private void onShopButtonClicked() {
        SceneManager.getInstance().switchView(SceneManager.VIEW_SHOP);
    }

    @FXML
    private void onExchangeButtonClicked() {
        SceneManager.getInstance().switchView(SceneManager.VIEW_EXCHANGE);
    }

    @FXML
    private void onCustomerButtonClicked() {
        SceneManager.getInstance().switchView(SceneManager.VIEW_CUSTOMER);
    }

    @FXML
    private void onOrderButtonClicked() {
        SceneManager.getInstance().switchView(SceneManager.VIEW_ORDERS);
    }

    @FXML
    private void onMessageButtonClicked() {
        SceneManager.getInstance().switchView(SceneManager.VIEW_MESSAGES);
    }

    @FXML
    private void onTopicsButtonClicked() {
        SceneManager.getInstance().switchView(SceneManager.VIEW_TOPICS);
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
        JMSClient.getJmsClient().disconnect();
        OrderingClient.getInstance().disconnect();
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

        logoutButton.setText("Log out\n" + SessionManager.getInstance().getUsername());

        if(!SessionManager.getInstance().getRoles().contains("Operator")) {
            topicsButton.setVisible(false);
            orderButton.setVisible(false);
        }

        // Check if new messages were received
        if(SessionManager.getInstance().isNewMessagesReceived()) {
            messageButton.getStyleClass().add("btn-alert");
            int amountOfNewMessages = JMSClient.getJmsClient().getAmountOfNewMessages();
            messageButton.setText("Messages (" + amountOfNewMessages + ")");
        }
    }
}