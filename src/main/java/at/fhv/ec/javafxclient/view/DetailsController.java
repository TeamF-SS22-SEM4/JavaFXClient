package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ec.javafxclient.view.forms.ShoppingCartForm;
import at.fhv.ss22.ea.f.communication.api.ProductSearchService;
import at.fhv.ss22.ea.f.communication.dto.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DetailsController {
    public static UUID productId;
    private static ProductDetailsDTO productDetails;
    private static List<Integer> selectedAmounts;

    // Services
    ProductSearchService productSearchService;

    @FXML
    private Label nameLabel;

    @FXML
    private Label artistNameLabel;

    @FXML
    private Label releaseYearLabel;

    @FXML
    private Label labelNameLabel;

    @FXML
    private Label genreLabel;

    @FXML
    private Label durationLabel;

    @FXML
    private TableView<SongDTO> songsTable;

    @FXML
    private TableView<SoundCarrierDTO> soundCarrierTable;

    @FXML
    private TableColumn<SoundCarrierDTO, Integer> amountAvailableColumn;

    @FXML
    private TableColumn<SoundCarrierDTO, Float> pricePerCarrierColumn;

    @FXML
    private TableColumn<SoundCarrierDTO, String> spinnerColumn;

    @FXML
    private TableColumn<SoundCarrierDTO, Button> addToCartColumn;

    @FXML
    public void initialize() {
        selectedAmounts = new ArrayList<>();

        try {
            productSearchService = RMIClient.getRmiClient().getRmiFactory().getProductSearchService();
            productDetails = productSearchService.productById(productId);
        } catch (RemoteException e) {
            e.printStackTrace();
            showPopup("Error", "Error connecting to the server.", Alert.AlertType.ERROR);
        }

        nameLabel.setText(productDetails.getName());
        artistNameLabel.setText(productDetails.getArtistName());
        releaseYearLabel.setText(productDetails.getReleaseYear());
        labelNameLabel.setText(productDetails.getLabelName());
        genreLabel.setText(productDetails.getGenre());
        durationLabel.setText(productDetails.getDuration());

        ObservableList<SongDTO> songsTableData = FXCollections.observableArrayList(productDetails.getSongs());
        songsTable.setItems(songsTableData);

        createSoundCarrierTable();
        fillSoundCarrierTable();
    }

    @FXML
    protected void onShoppingCartButtonClicked() {
        try {
            SceneManager.getInstance().switchView("views/shopping-cart-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onBackButtonClicked() {
        try {
            SceneManager.getInstance().switchView("views/search-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createSoundCarrierTable() {
        // Initialize Table Columns
        amountAvailableColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<SoundCarrierDTO, Integer> call(TableColumn<SoundCarrierDTO, Integer> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Integer amountAvailable, boolean empty) {
                        super.updateItem(amountAvailable, empty);
                        if (empty || amountAvailable == null) {
                            setText("");
                        } else {
                            String amountAvailableStr = amountAvailable + " pieces";

                            setText(amountAvailableStr);
                        }
                    }
                };
            }
        });

        pricePerCarrierColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<SoundCarrierDTO, Float> call(TableColumn<SoundCarrierDTO, Float> param) {
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

        // TODO: use a more beautiful solution
        Callback<TableColumn<SoundCarrierDTO, String>, TableCell<SoundCarrierDTO, String>> spinnerCellFactory = new Callback<>() {
            @Override
            public TableCell<SoundCarrierDTO, String> call(final TableColumn<SoundCarrierDTO, String> param) {
                return new TableCell<>() {

                    private final Spinner<Integer> selectAmountSpinner = new Spinner<>();

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            selectAmountSpinner.setValueFactory(
                                    new SpinnerValueFactory.IntegerSpinnerValueFactory(
                                            0,
                                            soundCarrierTable.getItems().get(getIndex()).getAmountAvailable(),
                                            0
                                    )
                            );

                            selectAmountSpinner.valueProperty().addListener((observable, oldValue, newValue) ->
                                    selectedAmounts.add(getIndex(), newValue)
                            );

                            setGraphic(selectAmountSpinner);
                        }
                    }
                };
            }
        };

        // TODO: use a more beautiful solution
        Callback<TableColumn<SoundCarrierDTO, Button>, TableCell<SoundCarrierDTO, Button>> addToCartButtonCellFactory = new Callback<>() {
            @Override
            public TableCell<SoundCarrierDTO, Button> call(final TableColumn<SoundCarrierDTO, Button> param) {
                return new TableCell<>() {

                    private final Button addToCartButton = new Button("Add to cart");

                    @Override
                    public void updateItem(Button item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            addToCartButton.setOnAction(event -> {
                                // TODO: get spinner from same row
                                addProductToCart(soundCarrierTable.getItems().get(getIndex()).getSoundCarrierName(), getIndex());
                            });
                            setGraphic(addToCartButton);
                            setText(null);
                        }
                    }
                };
            }
        };

        spinnerColumn.setCellFactory(spinnerCellFactory);
        addToCartColumn.setCellFactory(addToCartButtonCellFactory);
    }

    private void fillSoundCarrierTable() {
        ObservableList<SoundCarrierDTO> soundCarrierTableData = FXCollections.observableArrayList(productDetails.getSoundCarriers());
        soundCarrierTable.setItems(soundCarrierTableData);
    }

    private void addProductToCart(String soundCarrierName, int index) {
        SoundCarrierDTO selectedSoundCarrier = productDetails.getSoundCarriers().stream()
                .filter(soundCarrierDTO -> soundCarrierDTO.getSoundCarrierName().equals(soundCarrierName))
                .findFirst().orElse(null);

        int selectedAmount = selectedAmounts.get(index);

        if(selectedSoundCarrier != null && selectedAmount > 0) {
            float totalPrice = selectedAmount * selectedSoundCarrier.getPricePerCarrier();
            ShoppingCartForm cartEntry = new ShoppingCartForm(
                    productDetails.getProductId(),
                    productDetails.getName(),
                    productDetails.getArtistName(),
                    selectedSoundCarrier.getSoundCarrierId(),
                    selectedSoundCarrier.getSoundCarrierName(),
                    selectedSoundCarrier.getPricePerCarrier(),
                    selectedAmount,
                    totalPrice,
                    selectedSoundCarrier.getAmountAvailable()
            );

            ShoppingCartController.shoppingCart.add(cartEntry);

            showPopup(
                    "Successful", "Successfully added " + selectedAmount + " " + soundCarrierName +
                    "(s) of " + productDetails.getName() + " to shopping cart.\n" +
                    ShoppingCartController.shoppingCart.size() + " product(s) are now in shopping cart.",
                    Alert.AlertType.CONFIRMATION
            );

            selectedAmounts.add(index, 0);
        } else {
            showPopup("Error", "You have to choose at least one.", Alert.AlertType.ERROR);
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
