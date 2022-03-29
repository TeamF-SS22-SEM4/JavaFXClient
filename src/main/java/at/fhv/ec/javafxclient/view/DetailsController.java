package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.communication.RMIClient;
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
import java.util.UUID;

public class DetailsController {
    public static UUID productId;
    private static ProductDetailsDTO productDetails;

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
    private Label vinylNameLabel;

    @FXML
    private Label cdNameLabel;

    @FXML
    private Label cassetteNameLabel;

    @FXML
    private Label vinylAmountLabel;

    @FXML
    private Label cdAmountLabel;

    @FXML
    private Label cassetteAmountLabel;

    @FXML
    private Label vinylPriceLabel;

    @FXML
    private Label cdPriceLabel;

    @FXML
    private Label cassettePriceLabel;

    @FXML
    private Spinner<Integer> vinylSpinner;

    @FXML
    private Spinner<Integer> cdSpinner;

    @FXML
    private Spinner<Integer> cassetteSpinner;

    @FXML
    private TableView<SoundCarrierDTO> soundCarrierTable;

    @FXML
    public void initialize() {
        try {
            productSearchService = RMIClient.getRmiClient().getRmiFactory().getProductSearchService();

            productDetails = productSearchService.productById(productId);

            nameLabel.setText(productDetails.getName());
            artistNameLabel.setText(productDetails.getArtistName());
            releaseYearLabel.setText(productDetails.getReleaseYear());
            labelNameLabel.setText(productDetails.getLabelName());
            genreLabel.setText(productDetails.getGenre());
            durationLabel.setText(productDetails.getDuration());

            ObservableList<SongDTO> songsTableData = FXCollections.observableArrayList(productDetails.getSongs());
            songsTable.setItems(songsTableData);

            productDetails.getSoundCarriers().forEach(soundCarrier -> {
                if(soundCarrier.getSoundCarrierName().equals("Vinyl")) {
                    vinylNameLabel.setText(soundCarrier.getSoundCarrierName());
                    vinylAmountLabel.setText(soundCarrier.getAmountAvailable() + " pieces");
                    vinylPriceLabel.setText(soundCarrier.getPricePerCarrier() + "€");
                    vinylSpinner.setValueFactory(
                            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, soundCarrier.getAmountAvailable(), 0)
                    );
                } else if (soundCarrier.getSoundCarrierName().equals("CD")) {
                    cdNameLabel.setText(soundCarrier.getSoundCarrierName());
                    cdAmountLabel.setText(soundCarrier.getAmountAvailable() + " pieces");
                    cdPriceLabel.setText(soundCarrier.getPricePerCarrier() + "€");
                    cdSpinner.setValueFactory(
                            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, soundCarrier.getAmountAvailable(), 0)
                    );
                } else {
                    cassetteNameLabel.setText(soundCarrier.getSoundCarrierName());
                    cassetteAmountLabel.setText(soundCarrier.getAmountAvailable() + " pieces");
                    cassettePriceLabel.setText(soundCarrier.getPricePerCarrier() + "€");
                    cassetteSpinner.setValueFactory(
                            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, soundCarrier.getAmountAvailable(), 0)
                    );
                }
            });

            createTable();
            fillTable();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onVinylCartButtonClicked() {
        int selectedAmount = vinylSpinner.getValue();

        if(selectedAmount > 0) {
            addProductToCart("Vinyl", selectedAmount);
            vinylSpinner.getValueFactory().setValue(0);

        } else {
            showPopup("Error", "You have to choose at least one.");
        }
    }

    @FXML
    protected void onCDCartButtonClicked() {
        int selectedAmount = cdSpinner.getValue();

        if(selectedAmount > 0) {
            addProductToCart("CD", selectedAmount);
            cdSpinner.getValueFactory().setValue(0);
        } else {
            showPopup("Error", "You have to choose at least one.");
        }
    }

    @FXML
    protected void onCassetteCartButtonClicked() {
        int selectedAmount = cassetteSpinner.getValue();

        if(selectedAmount > 0) {
            addProductToCart("Cassette", selectedAmount);
            cassetteSpinner.getValueFactory().setValue(0);
        } else {
            showPopup("Error", "You have to choose at least one.");
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

    @FXML
    protected void onShoppingCartButtonClicked() {
        try {
            SceneManager.getInstance().switchView("views/shopping-cart-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createTable() {
        // Initialize Table Columns
        TableColumn<SoundCarrierDTO, String> soundCarrierNameColumn = new TableColumn<>("Type");
        soundCarrierNameColumn.setCellValueFactory(new PropertyValueFactory("soundCarrierName"));

        TableColumn<SoundCarrierDTO, String> amountAvailableColumn = new TableColumn<>("In stock");
        amountAvailableColumn.setCellValueFactory(new PropertyValueFactory("amountAvailable"));

        TableColumn<SoundCarrierDTO, String> pricePerCarrierColumn = new TableColumn<>("Price per carrier");
        pricePerCarrierColumn.setCellValueFactory(new PropertyValueFactory("pricePerCarrier"));

        TableColumn spinnerColumn = new TableColumn("Selected Amount");
        TableColumn addToCartColumn = new TableColumn("Action");

        // TODO: find a more beautiful solution
        Callback<TableColumn<SoundCarrierDTO, String>, TableCell<SoundCarrierDTO, String>> spinnerCellFactory = new Callback<>() {
            @Override
            public TableCell call(final TableColumn<SoundCarrierDTO, String> param) {
                final TableCell<SoundCarrierDTO, String> cell = new TableCell<>() {

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
                                            getTableView().getItems().get(getIndex()).getAmountAvailable(),
                                            0
                                    )
                            );
                            setGraphic(selectAmountSpinner);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };

        Callback<TableColumn<SoundCarrierDTO, String>, TableCell<SoundCarrierDTO, String>> addToCartButtonCellFactory = new Callback<>() {
            @Override
            public TableCell call(final TableColumn<SoundCarrierDTO, String> param) {
                final TableCell<SoundCarrierDTO, String> cell = new TableCell<>() {

                    private final Button addToCartButton = new Button("Add to cart");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            addToCartButton.setOnAction(event -> {
                                System.out.println(spinnerColumn.cellValueFactoryProperty().getValue());
                                System.out.println(getTableView().getItems().get(getIndex()).getSoundCarrierName());
                            });
                            setGraphic(addToCartButton);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };

        spinnerColumn.setCellFactory(spinnerCellFactory);
        addToCartColumn.setCellFactory(addToCartButtonCellFactory);
        soundCarrierTable.getColumns().addAll(
                soundCarrierNameColumn, amountAvailableColumn, pricePerCarrierColumn,
                spinnerColumn, addToCartColumn
        );
    }

    private void fillTable() {
        ObservableList<SoundCarrierDTO> soundCarrierTableData = FXCollections.observableArrayList(productDetails.getSoundCarriers());
        soundCarrierTable.setItems(soundCarrierTableData);
    }

    private void addProductToCart(String soundCarrierName, int selectedAmount) {
        SoundCarrierDTO selectedSoundCarrier = productDetails.getSoundCarriers().stream()
                .filter(soundCarrierDTO -> soundCarrierDTO.getSoundCarrierName().equals(soundCarrierName))
                .findFirst().orElse(null);

        if(selectedSoundCarrier != null) {
            float totalPrice = selectedAmount * selectedSoundCarrier.getPricePerCarrier();
            ShoppingCartProductDTO shoppingCartProduct = ShoppingCartProductDTO.builder()
                    .withProductId(productDetails.getProductId())
                    .withProductName(productDetails.getName())
                    .withArtistName(productDetails.getArtistName())
                    .withSoundCarrierId(selectedSoundCarrier.getSoundCarrierId())
                    .withCarrierName(selectedSoundCarrier.getSoundCarrierName())
                    .withPricePerCarrier(selectedSoundCarrier.getPricePerCarrier())
                    .withSelectedAmount(selectedAmount)
                    .withTotalProductPrice(totalPrice)
                    .build();

            ShoppingCartController.shoppingCart.add(shoppingCartProduct);

            showPopup("Successful", "Successfully added " + selectedAmount + " " + soundCarrierName +
                    "(s) of " + productDetails.getName() + " to shopping cart.\n" +
                    ShoppingCartController.shoppingCart.size() + " product(s) in shopping cart.");
        } else {
            showPopup("Error", "There was a problem.");
        }
    }

    private void showPopup(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(message);
        ButtonType confirmButton = new ButtonType("Ok");
        alert.getButtonTypes().setAll(confirmButton);
        alert.show();
    }
}
