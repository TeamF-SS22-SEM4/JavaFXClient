package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.SessionManager;
import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ec.javafxclient.view.utils.ShoppingCartEntry;
import at.fhv.ss22.ea.f.communication.dto.CustomerDTO;
import at.fhv.ss22.ea.f.communication.dto.ShoppingCartProductDTO;
import at.fhv.ss22.ea.f.communication.exception.CarrierNotAvailableException;
import at.fhv.ss22.ea.f.communication.exception.NoPermissionForOperation;
import at.fhv.ss22.ea.f.communication.exception.SessionExpired;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static at.fhv.ec.javafxclient.view.ShoppingCartController.shoppingCart;

public class CheckoutController {
    public static CustomerDTO customer;
    private static float totalPrice;
    private ToggleGroup paymentMethods;

    @FXML
    private TableView<ShoppingCartEntry> shoppingCartTable;

    @FXML
    private TableColumn<ShoppingCartEntry, String> productNameColumn;

    @FXML
    private TableColumn<ShoppingCartEntry, Float> pricePerCarrierColumn;

    @FXML
    private TableColumn<ShoppingCartEntry, Float> totalProductPriceColumn;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private RadioButton cashRadioButton;

    @FXML
    private RadioButton creditCardRadioButton;

    @FXML
    private RadioButton invoiceRadioButton;

    @FXML
    private Label customerNameLabel;

    @FXML
    private Label customerAddressLabel;

    @FXML
    private Label customerMailAndPhoneLabel;

    @FXML
    public void initialize() {
        pricePerCarrierColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<ShoppingCartEntry, Float> call(TableColumn<ShoppingCartEntry, Float> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Float pricePerCarrier, boolean empty) {
                        super.updateItem(pricePerCarrier, empty);
                        if (empty || pricePerCarrier == null) {
                            setText("");
                        } else {
                            String pricePerCarrierStr = pricePerCarrier + "€";

                            setText(pricePerCarrierStr);
                        }
                    }
                };
            }
        });

        totalProductPriceColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<ShoppingCartEntry, Float> call(TableColumn<ShoppingCartEntry, Float> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Float totalProductPrice, boolean empty) {
                        super.updateItem(totalProductPrice, empty);
                        if (empty || totalProductPrice == null) {
                            setText("");
                        } else {
                            String ptotalProductPriceStr = totalProductPrice + "€";

                            setText(ptotalProductPriceStr);
                        }
                    }
                };
            }
        });

        ObservableList<ShoppingCartEntry> shoppingCartTableData = FXCollections.observableArrayList(shoppingCart);
        shoppingCartTable.setItems(shoppingCartTableData);
        shoppingCartTable.getSortOrder().add(productNameColumn);
        shoppingCartTable.sort();

        totalPrice = 0;

        shoppingCart.forEach(item -> {
            totalPrice += item.getTotalProductPrice();
        });

        totalPriceLabel.setText(totalPrice + "€");

        paymentMethods = new ToggleGroup();
        cashRadioButton.setToggleGroup(paymentMethods);
        creditCardRadioButton.setToggleGroup(paymentMethods);
        invoiceRadioButton.setToggleGroup(paymentMethods);

        // Add customer information
        if(customer != null) {
            customerNameLabel.setText(customer.getGivenName() + " " + customer.getFamilyName());
            customerAddressLabel.setText(customer.getStreet() + " " + customer.getHouseNumber() +
                    ", " + customer.getPostalCode() + " " + customer.getCity());
            customerMailAndPhoneLabel.setText("E-Mail: " + customer.getEmail() + "\nPhonenumber: " + customer.getPhoneNumber());

            customerAddressLabel.setVisible(true);
            customerMailAndPhoneLabel.setVisible(true);
        } else {
            customerNameLabel.setText("No customer selected");
            customerAddressLabel.setVisible(false);
            customerMailAndPhoneLabel.setVisible(false);
        }
    }

    @FXML
    protected void onPayButtonClicked() {
        RadioButton selectedPaymentMethodRadioButton = (RadioButton) paymentMethods.getSelectedToggle();

        if(selectedPaymentMethodRadioButton != null) {
            UUID customerId = customer == null ? null : customer.getCustomerId();
            purchase(selectedPaymentMethodRadioButton.getText(), customerId);
        } else {
            showPopup("Error", "You have to select a payment method", Alert.AlertType.ERROR);
        }
    }

    @FXML
    protected void onSelectCustomerButtonClicked() {
        try {
            SceneManager.getInstance().switchView("customer");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void purchase(String selectedPaymentMethod, UUID customerId) {
        List<ShoppingCartProductDTO> shoppingCartProducts = new ArrayList<>();

        shoppingCart.forEach(shoppingCartItem -> {
            shoppingCartProducts.add(
                    ShoppingCartProductDTO.builder()
                            .withProductId(shoppingCartItem.getProductId())
                            .withSoundCarrierId(shoppingCartItem.getSoundCarrierId())
                            .withProductName(shoppingCartItem.getProductName())
                            .withArtistName(shoppingCartItem.getArtistName())
                            .withSelectedAmount(shoppingCartItem.getSelectedAmount())
                            .withCarrierName(shoppingCartItem.getSoundCarrierName())
                            .withPricePerCarrier(shoppingCartItem.getPricePerCarrier())
                            .withTotalProductPrice(totalPrice)
                            .build()
            );
        });

        try {
            String invoiceNumber = RMIClient.getRmiClient()
                    .getRmiFactory()
                    .getBuyingService()
                    .buyWithShoppingCart(SessionManager.getInstance().getSessionId(), shoppingCartProducts, selectedPaymentMethod, customerId);

            shoppingCart.clear();
            showPopup("Successful", "Invoice No.: " + invoiceNumber + "\nBill is printed...", Alert.AlertType.CONFIRMATION);
            SceneManager.getInstance().switchView("shop");
        } catch (CarrierNotAvailableException cne) {
            showPopup("Error", "The selected amount is not available.", Alert.AlertType.ERROR);
            cne.printStackTrace();
        } catch (IOException e) {
            showPopup("Error", "An error occurred.", Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (SessionExpired | NoPermissionForOperation e) {
            e.printStackTrace();
        }
    }

    private void showPopup(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(message);
        ButtonType confirmButton = new ButtonType("Ok");
        alert.getButtonTypes().setAll(confirmButton);
        alert.show();
    }

    public void onBackButtonClicked(ActionEvent actionEvent) {
    }
}
