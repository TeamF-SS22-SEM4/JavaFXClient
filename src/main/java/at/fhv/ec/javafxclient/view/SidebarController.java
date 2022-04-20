package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SidebarController implements Initializable {


    public ToggleGroup themeStyleToggleGroup;
    public ToggleGroup themeColorToggleGroup;
    public ToggleButton dark;
    public ToggleButton color1;

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
    private void onThemeButtonsClicked() {
        ToggleButton toggleButtonStyle = (ToggleButton) themeStyleToggleGroup.getSelectedToggle();
        String themeStyle = toggleButtonStyle.getId();

        ToggleButton toggleButtonColor = (ToggleButton) themeColorToggleGroup.getSelectedToggle();
        String themeColor = toggleButtonColor.getId();

        SceneManager.getInstance().switchTheme(themeStyle, themeColor);
    }

    @FXML
    private void onLogoutButtonClicked() throws IOException {
        SessionManager.getInstance().logout();
        SceneManager.getInstance().logout();
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
    }
}
