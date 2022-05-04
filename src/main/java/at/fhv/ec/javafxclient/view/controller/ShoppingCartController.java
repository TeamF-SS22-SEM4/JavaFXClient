package at.fhv.ec.javafxclient.view.controller;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.SessionManager;
import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ec.javafxclient.model.ShoppingCartEntry;
import at.fhv.ss22.ea.f.communication.dto.CustomerDTO;
import at.fhv.ss22.ea.f.communication.dto.ShoppingCartProductDTO;
import at.fhv.ss22.ea.f.communication.exception.CarrierNotAvailableException;
import at.fhv.ss22.ea.f.communication.exception.NoPermissionForOperation;
import at.fhv.ss22.ea.f.communication.exception.SessionExpired;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShoppingCartController {

    public static List<ShoppingCartEntry> shoppingCart = new ArrayList<>();
    public static CustomerDTO customer;
    private static float totalPrice;

    @FXML
    private ToggleGroup paymentToggleGroup;
    @FXML
    private Button payButton;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private Label feedbackLabel;
    @FXML
    private Button addCustomerButton;
    @FXML
    private Button removeCustomerButton;
    @FXML
    private Label customerNameLabel;
    @FXML
    private Label customerStreetLabel;
    @FXML
    private Label customerCityLabel;
    @FXML
    private TableView<ShoppingCartEntry> shoppingCartTable;
    @FXML
    private TableColumn<ShoppingCartEntry, String> productNameColumn;
    @FXML
    private TableColumn<ShoppingCartEntry, Spinner<Integer>> selectedAmountColumn;
    @FXML
    private TableColumn<ShoppingCartEntry, Float> pricePerCarrierColumn;
    @FXML
    private TableColumn<ShoppingCartEntry, Button> actionColumn;

    @FXML
    public void initialize() {
        ObservableList<ShoppingCartEntry> shoppingCartTableData = FXCollections.observableArrayList(shoppingCart);
        shoppingCartTable.setItems(shoppingCartTableData);
        shoppingCartTable.getSortOrder().add(productNameColumn);
        shoppingCartTable.sort();

        formatTable();
        updateTotalPrice();
        addCustomer();
        updatePayButton();
    }

    @FXML
    public void onBackButtonClicked() {
        SceneManager.getInstance().switchView(SceneManager.VIEW_SHOP);
    }

    @FXML
    public void onSelectCustomerButtonClicked() {
        SceneManager.getInstance().switchView(SceneManager.VIEW_CUSTOMER);
    }

    @FXML
    public void onRemoveCustomerButtonClicked() {
        customer = null;
        addCustomer();
    }

    @FXML
    protected void onPayButtonClicked() {
        feedbackLabel.setText("");
        feedbackLabel.getStyleClass().remove("alert");

        if(shoppingCart.size() > 0) {

            RadioButton selectedPaymentMethodRadioButton = (RadioButton) paymentToggleGroup.getSelectedToggle();

            if(selectedPaymentMethodRadioButton != null) {
                UUID customerId = customer == null ? null : customer.getCustomerId();
                purchase(selectedPaymentMethodRadioButton.getText(), customerId);
            } else {
                feedbackLabel.getStyleClass().add("alert");
                feedbackLabel.setText("Select a payment method!");
            }
        }
    }

    private void formatTable() {
        Callback<TableColumn<ShoppingCartEntry, Spinner<Integer>>, TableCell<ShoppingCartEntry, Spinner<Integer>>> spinnerCellFactory = new Callback<>() {
            @Override
            public TableCell<ShoppingCartEntry, Spinner<Integer>> call(final TableColumn<ShoppingCartEntry, Spinner<Integer>> param) {
                return new TableCell<>() {

                    @Override
                    public void updateItem(Spinner<Integer> item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Spinner<Integer> selectedAmountSpinner = new Spinner<>();
                            selectedAmountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, shoppingCart.get(getIndex()).getAmountAvailable(), shoppingCart.get(getIndex()).getSelectedAmount()));

                            selectedAmountSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                                ShoppingCartEntry selectedShoppingCartItem = shoppingCart.get(getIndex());
                                selectedShoppingCartItem.setSelectedAmount(newValue);
                                selectedShoppingCartItem.setTotalProductPrice(newValue * selectedShoppingCartItem.getPricePerCarrier());
                                updateTotalPrice();
                            });

                            setGraphic(selectedAmountSpinner);
                        }
                    }
                };
            }
        };
        selectedAmountColumn.setCellFactory(spinnerCellFactory);

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

        actionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<ShoppingCartEntry, Button> call(TableColumn<ShoppingCartEntry, Button> param) {
                return new TableCell<>() {

                    @Override
                    public void updateItem(Button item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Button removeButton = new Button("Remove");
                            removeButton.getStyleClass().add("btn-alert");
                            removeButton.setOnAction(event -> {
                                shoppingCartTable.getItems().remove(getIndex());
                                shoppingCart.remove(getIndex());
                                updateTotalPrice();
                                updatePayButton();
                            });
                            setGraphic(removeButton);
                        }
                    }
                };
            }
        });
    }

    private void updatePayButton() {
        if (shoppingCart.isEmpty()) {
            payButton.setVisible(false);
        } else {
            payButton.setVisible(true);
        }
    }

    private void purchase(String selectedPaymentMethod, UUID customerId) {
        feedbackLabel.setText("");
        feedbackLabel.getStyleClass().remove("alert");

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

            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle("Successful order");
            alert.setContentText("Invoice No.: " + invoiceNumber + "\n\nBill is printed...");
            ButtonType confirmButton = new ButtonType("Ok");
            alert.getButtonTypes().setAll(confirmButton);
            alert.show();

            SceneManager.getInstance().switchView(SceneManager.VIEW_SHOP);
        } catch (CarrierNotAvailableException cne) {
            feedbackLabel.getStyleClass().add("alert");
            feedbackLabel.setText("Selected amount is not available!");
        } catch (IOException e) {
            feedbackLabel.getStyleClass().add("alert");
            feedbackLabel.setText("An error occurred!");
        } catch (SessionExpired | NoPermissionForOperation e) {
            e.printStackTrace();
        }
    }

    private void addCustomer() {
        if(customer != null) {

            customerNameLabel.setVisible(true);
            customerNameLabel.setManaged(true);

            customerStreetLabel.setVisible(true);
            customerStreetLabel.setManaged(true);

            customerCityLabel.setVisible(true);
            customerCityLabel.setManaged(true);

            customerNameLabel.setText(customer.getGivenName() + " " + customer.getFamilyName());
            customerStreetLabel.setText(customer.getStreet() + " " + customer.getHouseNumber());
            customerCityLabel.setText(customer.getPostalCode() + " " + customer.getCity());

            removeCustomerButton.setVisible(true);
            removeCustomerButton.setManaged(true);
            addCustomerButton.setVisible(false);
            addCustomerButton.setManaged(false);

        } else {

            customerNameLabel.setVisible(false);
            customerNameLabel.setManaged(false);

            customerStreetLabel.setVisible(false);
            customerStreetLabel.setManaged(false);

            customerCityLabel.setVisible(false);
            customerCityLabel.setManaged(false);

            removeCustomerButton.setVisible(false);
            removeCustomerButton.setManaged(false);
            addCustomerButton.setVisible(true);
            addCustomerButton.setManaged(true);
        }
    }

    private void updateTotalPrice() {
        totalPrice = 0;
        shoppingCart.forEach(item -> totalPrice += item.getTotalProductPrice());
        totalPriceLabel.setText(totalPrice + "€");
    }

}