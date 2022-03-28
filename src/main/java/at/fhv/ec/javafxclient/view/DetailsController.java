package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ss22.ea.f.communication.api.ProductSearchService;
import at.fhv.ss22.ea.f.communication.dto.ProductDetailsDTO;
import at.fhv.ss22.ea.f.communication.dto.SongDTO;
import at.fhv.ss22.ea.f.communication.dto.SoundCarrierDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.PopupWindow;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.UUID;

public class DetailsController {
    public static UUID productId;
    private static ProductDetailsDTO productDetails;

    // Services
    ProductSearchService productSearchService;

    {
        try {
            productSearchService = RMIClient.getRmiClient().getRmiFactory().getProductSearchService();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public Label nameLabel;

    @FXML
    public Label artistNameLabel;

    @FXML
    public Label releaseYearLabel;

    @FXML
    public Label labelNameLabel;

    @FXML
    public Label genreLabel;

    @FXML
    public Label durationLabel;

    @FXML
    public TableView<SongDTO> songsTable;

    @FXML
    public Label vinylNameLabel;

    @FXML
    public Label cdNameLabel;

    @FXML
    public Label cassetteNameLabel;

    @FXML
    public Label vinylAmountLabel;

    @FXML
    public Label cdAmountLabel;

    @FXML
    public Label cassetteAmountLabel;

    @FXML
    public Label vinylPriceLabel;

    @FXML
    public Label cdPriceLabel;

    @FXML
    public Label cassettePriceLabel;

    @FXML
    public Spinner<Integer> vinylSpinner;

    @FXML
    public Spinner<Integer> cdSpinner;

    @FXML
    public Spinner<Integer> cassetteSpinner;

    @FXML
    public void initialize() {
        try {
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
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void addProductToCart(String soundCarrierName, int selectedAmount) {
        SoundCarrierDTO selectedSoundCarrier = productDetails.getSoundCarriers().stream()
                .filter(soundCarrierDTO -> soundCarrierDTO.getSoundCarrierName().equals(soundCarrierName))
                .findFirst().orElse(null);

        if(selectedSoundCarrier != null) {
            ShoppingCartController.shoppingCart.put(selectedSoundCarrier, selectedAmount);
            showPopup("Successful", "Successfully added sound carrier to shopping cart.\n" +
                    ShoppingCartController.shoppingCart.size() + " products in shopping cart.");
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

    @FXML
    protected void onVinylCartButtonClicked() {
        int selectedAmount = vinylSpinner.getValue();

        if(selectedAmount > 0) {
            addProductToCart("Vinyl", selectedAmount);
        } else {
            showPopup("Error", "You have to choose at least one.");
        }
    }

    @FXML
    protected void onCDCartButtonClicked() {
        int selectedAmount = cdSpinner.getValue();

        if(selectedAmount > 0) {
            addProductToCart("CD", selectedAmount);
        } else {
            showPopup("Error", "You have to choose at least one.");
        }
    }

    @FXML
    protected void onCassetteCartButtonClicked() {
        int selectedAmount = cassetteSpinner.getValue();

        if(selectedAmount > 0) {
            addProductToCart("Cassette", selectedAmount);
        } else {
            showPopup("Error", "You have to choose at least one.");
        }
    }

    @FXML
    protected void onBackButtonClicked() {
        try {
            SceneManager.getInstance().switchView("views/search-view-copy.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
