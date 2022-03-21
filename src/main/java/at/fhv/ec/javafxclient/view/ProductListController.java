package at.fhv.ec.javafxclient.view;

import at.fhv.ss22.ea.f.communication.dto.ProductOverviewDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ProductListController {

    @FXML
    Label nameLabel;

    @FXML
    Label artistNameLabel;

    @FXML
    Label releaseYearLabel;

    public void setProductOverviewDTO(ProductOverviewDTO productOverviewDTO) {
        if(productOverviewDTO == null) {
            nameLabel.setText(null);
            artistNameLabel.setText(null);
            releaseYearLabel.setText(null);
        } else {
            nameLabel.setText(productOverviewDTO.name());
            artistNameLabel.setText(productOverviewDTO.artistName());
            releaseYearLabel.setText(productOverviewDTO.releaseYear());
        }
    }
}
