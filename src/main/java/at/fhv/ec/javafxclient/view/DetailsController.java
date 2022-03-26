package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ss22.ea.f.communication.api.ProductSearchService;
import at.fhv.ss22.ea.f.communication.dto.ProductDetailsDTO;
import at.fhv.ss22.ea.f.communication.dto.ProductOverviewDTO;
import at.fhv.ss22.ea.f.communication.dto.SongDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.rmi.RemoteException;
import java.util.UUID;

public class DetailsController {
    private UUID productId;

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
    private TableView<SongDTO> songsTable;

    public void initData(UUID productId) {
        this.productId = productId;
    }

    @FXML
    public void initialize() {
        try {
            ProductDetailsDTO productDetails = productSearchService.productById(this.productId);

            nameLabel.setText(productDetails.getName());
            artistNameLabel.setText(productDetails.getArtistName());
            releaseYearLabel.setText(productDetails.getReleaseYear());
            labelNameLabel.setText(productDetails.getLabelName());
            genreLabel.setText(productDetails.getGenre());
            durationLabel.setText(productDetails.getDuration());

            /*
            getSongs returns null
            ObservableList<SongDTO> songsTableData = FXCollections.observableArrayList(productDetails.getSongs());
            songsTable.setItems(songsTableData);
             */
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
