package at.fhv.ec.javafxclient.view.components;

import at.fhv.ec.javafxclient.Application;
import at.fhv.ec.javafxclient.application.dto.ProductOverviewDTO;
import at.fhv.ec.javafxclient.view.ProductListController;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.io.UncheckedIOException;

public class ProductListCell extends ListCell<ProductOverviewDTO> {
    private final ProductListController productListController;

    public ProductListCell() {
        try {
            FXMLLoader loader = new FXMLLoader(Application.class.getResource("components/product-list-entry.fxml"));
            Pane productListView = loader.load();
            productListController = loader.getController();
            setGraphic(productListView);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        } catch (IOException e) {
            // IOException here is fatal:
            throw new UncheckedIOException(e);
        }
    }

    @Override
    protected void updateItem(ProductOverviewDTO item, boolean empty) {
        super.updateItem(item, empty);
        productListController.setProductOverviewDTO(item);
    }
}
