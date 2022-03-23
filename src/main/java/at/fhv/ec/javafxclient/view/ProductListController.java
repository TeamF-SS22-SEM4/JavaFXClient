package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ss22.ea.f.communication.api.ProductSearchService;
import at.fhv.ss22.ea.f.communication.dto.ProductDetailsDTO;
import at.fhv.ss22.ea.f.communication.dto.ProductOverviewDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.rmi.RemoteException;
import java.util.Optional;

public class ProductListController {

    private ProductOverviewDTO product;

    @FXML
    Label nameLabel;

    @FXML
    Label artistNameLabel;

    @FXML
    Label releaseYearLabel;

    @FXML
    Button detailsButton;

    public void setProductOverviewDTO(ProductOverviewDTO productOverviewDTO) {
        product = productOverviewDTO;

        if(product == null) {
            nameLabel.setText(null);
            artistNameLabel.setText(null);
            releaseYearLabel.setText(null);
        } else {
            nameLabel.setText(productOverviewDTO.name());
            artistNameLabel.setText(
                    String.join(", ", productOverviewDTO.artistName())
            );
            releaseYearLabel.setText(productOverviewDTO.releaseYear());
        }
    }

    @FXML
    protected void onDetailsButtonClicked() {
        try {
            ProductSearchService productSearchService = RMIClient.getRmiClient().getRmiFactory().getProductSearchService();
            ProductDetailsDTO productDetails = productSearchService.productById(product.getProductId());

            System.out.println("----- Product Details -----");
            System.out.println(productDetails.getName());
            System.out.println(productDetails.getArtistName());
            System.out.println(productDetails.getReleaseYear());
            System.out.println(productDetails.getLabelName());
            System.out.println(productDetails.getGenre());
            System.out.println(productDetails.getDuration());
            System.out.println("Songs: " + productDetails.getSongs().size());
            System.out.println("Sound Carriers: " + productDetails.getSoundCarriers().size());
            System.out.println("----- Product Details -----");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
