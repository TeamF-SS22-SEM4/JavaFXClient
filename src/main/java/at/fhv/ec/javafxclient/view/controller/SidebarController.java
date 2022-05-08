package at.fhv.ec.javafxclient.view.controller;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.SessionManager;
import at.fhv.ec.javafxclient.communication.JMSClient;
import at.fhv.ec.javafxclient.communication.OrderingClient;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import java.net.URL;
import java.util.ResourceBundle;

public class SidebarController {

    @FXML
    private Button messageButton;
    @FXML
    private Button readMessageButton;
    @FXML
    private Button writeMessageButton;
    @FXML
    private Button orderButton;
    @FXML
    private Button logoutButton;
    @FXML
    private ToggleGroup themeStyleToggleGroup;
    @FXML
    private ToggleGroup themeColorToggleGroup;


    @FXML
    public void initialize() {
        themeStyleToggleGroup.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null)
                oldVal.setSelected(true);
        });

        themeColorToggleGroup.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null)
                oldVal.setSelected(true);
        });

        logoutButton.setText("Log out\n" + SessionManager.getInstance().getUsername());

        if (!SessionManager.getInstance().getRoles().contains("Operator")) {
            readMessageButton.setVisible(false);
            writeMessageButton.setVisible(false);
            orderButton.setVisible(false);
        } else {
            messageButton.setVisible(false);
            messageButton.setManaged(false);
        }

        if(SessionManager.getInstance().isNewMessagesReceived()) {
            messageButton.getStyleClass().add("btn-notify");
            readMessageButton.getStyleClass().add("btn-notify");

            int amountOfNewMessages = JMSClient.getJmsClient().getAmountOfNewMessages();
            messageButton.setText("Messages (" + amountOfNewMessages + ")");
            readMessageButton.setText("Read (" + amountOfNewMessages + ")");
        }
    }

    @FXML
    public void onLogoClicked() {
        SceneManager.getInstance().switchView(SceneManager.VIEW_SHOP);
    }

    @FXML
    public void onShopButtonClicked() {
        SceneManager.getInstance().switchView(SceneManager.VIEW_SHOP);
    }

    @FXML
    public void onExchangeButtonClicked() {
        SceneManager.getInstance().switchView(SceneManager.VIEW_EXCHANGE);
    }

    @FXML
    public void onMessageButtonClicked() {
        SceneManager.getInstance().switchView(SceneManager.VIEW_MESSAGES_READ_CHANNELS);
    }

    @FXML
    public void onTopicsButtonClicked() {
        SceneManager.getInstance().switchView(SceneManager.VIEW_MESSAGES_WRITE_CHANNELS);
    }

    @FXML
    public void onOrderButtonClicked() {
        SceneManager.getInstance().switchView(SceneManager.VIEW_ORDERS);
    }

    @FXML
    public void onThemeButtonsClicked() {
        String theme = ((ToggleButton) themeStyleToggleGroup.getSelectedToggle()).getId();
        String color = ((ToggleButton) themeColorToggleGroup.getSelectedToggle()).getId();
        SceneManager.getInstance().switchTheme(theme, color);
    }

    @FXML
    public void onLogoutButtonClicked() {
        SessionManager.getInstance().logout();
        SceneManager.getInstance().logout();
        JMSClient.getJmsClient().disconnect();
        OrderingClient.getInstance().disconnect();
    }

}