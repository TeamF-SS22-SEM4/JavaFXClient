package at.fhv.ec.javafxclient.view.controller;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.view.utils.ShoppingCartEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCartController {
    public static List<ShoppingCartEntry> shoppingCart = new ArrayList<>();
    private static float totalPrice;

    @FXML
    private TableView<ShoppingCartEntry> shoppingCartTable;

    @FXML
    private TableColumn<ShoppingCartEntry, String> productNameColumn;

    @FXML
    private TableColumn<ShoppingCartEntry, Spinner<Integer>> selectedAmountColumn;

    @FXML
    private TableColumn<ShoppingCartEntry, Float> totalProductPriceColumn;

    @FXML
    private TableColumn<ShoppingCartEntry, Button> actionColumn;

    @FXML
    private Label totalPriceLabel;

    @FXML
    public void initialize() {
        createShoppingCartTable();

        ObservableList<ShoppingCartEntry> shoppingCartTableData = FXCollections.observableArrayList(shoppingCart);
        shoppingCartTable.setItems(shoppingCartTableData);
        shoppingCartTable.getSortOrder().add(productNameColumn);
        shoppingCartTable.sort();

        updateTotalPrice();
    }

    @FXML
    protected void onClearCartButtonClicked() {
        shoppingCartTable.getItems().clear();
        shoppingCart.clear();
        updateTotalPrice();
    }

    @FXML
    protected void onCheckoutButtonClicked() {
        if(shoppingCart.size() > 0) {
            try {
                SceneManager.getInstance().switchView("checkout");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showPopup(
                    "Error",
                    "The shopping cart is empty. Please add a product to the shopping cart.",
                    Alert.AlertType.ERROR
            );
        }
    }

    private void updateTotalPrice() {
        totalPrice = 0;

        shoppingCart.forEach(item -> {
            totalPrice += item.getTotalProductPrice();
        });

        totalPriceLabel.setText(totalPrice + "€");
    }

    private void createShoppingCartTable() {
        Callback<TableColumn<ShoppingCartEntry, Spinner<Integer>>, TableCell<ShoppingCartEntry, Spinner<Integer>>> spinnerCellFactory = new Callback<>() {
            @Override
            public TableCell<ShoppingCartEntry, Spinner<Integer>> call(final TableColumn<ShoppingCartEntry, Spinner<Integer>> param) {
                return new TableCell<>() {

                    private final Spinner<Integer> selectedAmountSpinner = new Spinner<>();

                    @Override
                    public void updateItem(Spinner<Integer> item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            selectedAmountSpinner.setValueFactory(
                                    new SpinnerValueFactory.IntegerSpinnerValueFactory(
                                            1,
                                            shoppingCart.get(getIndex()).getAmountAvailable(),
                                            shoppingCart.get(getIndex()).getSelectedAmount()
                                    )
                            );

                            selectedAmountSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                                // TODO: Update total per product in table
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

        actionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<ShoppingCartEntry, Button> call(TableColumn<ShoppingCartEntry, Button> param) {
                return new TableCell<>() {

                    final Button removeButton = new Button("Remove");

                    @Override
                    public void updateItem(Button item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            removeButton.setOnAction(event -> {
                                shoppingCartTable.getItems().remove(getIndex());
                                shoppingCart.remove(getIndex());
                                updateTotalPrice();
                            });
                            setGraphic(removeButton);
                        }
                    }
                };
            }
        });
    }

    private void showPopup(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(message);
        ButtonType confirmButton = new ButtonType("Ok");
        alert.getButtonTypes().setAll(confirmButton);
        alert.show();
    }

    @FXML
    public void onBackButtonClicked() throws IOException {
        SceneManager.getInstance().switchView("shop");
    }
}