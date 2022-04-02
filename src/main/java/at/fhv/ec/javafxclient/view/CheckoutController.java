package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ec.javafxclient.view.forms.ShoppingCartEntry;
import at.fhv.ss22.ea.f.communication.dto.ShoppingCartProductDTO;
import at.fhv.ss22.ea.f.communication.exception.CarrierNotAvailableException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static at.fhv.ec.javafxclient.view.ShoppingCartController.shoppingCart;

public class CheckoutController {
    private static float totalPrice;
    private ToggleGroup paymentMethods;

    @FXML
    private TableView<ShoppingCartEntry> shoppingCartTable;

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

        totalPrice = 0;

        shoppingCart.forEach(item -> {
            totalPrice += item.getTotalProductPrice();
        });

        totalPriceLabel.setText(totalPrice + "€");

        // Add radio buttons to group
        paymentMethods = new ToggleGroup();
        cashRadioButton.setToggleGroup(paymentMethods);
        creditCardRadioButton.setToggleGroup(paymentMethods);
        invoiceRadioButton.setToggleGroup(paymentMethods);
    }

    @FXML
    protected void onPayButtonClicked() {
        RadioButton selectedPaymentMethodRadioButton = (RadioButton) paymentMethods.getSelectedToggle();

        if(selectedPaymentMethodRadioButton != null) {
            String selectedPaymentMethod = selectedPaymentMethodRadioButton.getText();
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

            // TODO: Ask for confirmation
            try {
                RMIClient.getRmiClient()
                        .getRmiFactory()
                        .getBuyingService()
                        .buyWithShoppingCart(shoppingCartProducts, selectedPaymentMethod);

                shoppingCart.clear();
                showPopup("Successful", "Bill is printed...", Alert.AlertType.CONFIRMATION);
                SceneManager.getInstance().switchView("views/product-search-view.fxml");
            } catch (CarrierNotAvailableException cne) {
                showPopup("Error", "The selected amount is not available.", Alert.AlertType.ERROR);
                cne.printStackTrace();
            } catch (IOException e) {
                showPopup("Error", "An error occurred.", Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        } else {
            showPopup("Error", "You have to select a payment method", Alert.AlertType.ERROR);
        }
    }

    @FXML
    protected void onBackButtonClicked() {
        try {
            SceneManager.getInstance().switchView("views/shopping-cart-view.fxml");
        } catch (IOException e) {
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
}
