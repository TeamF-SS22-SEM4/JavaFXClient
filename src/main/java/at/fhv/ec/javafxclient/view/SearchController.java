package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.application.api.ProductSearchService;
import at.fhv.ec.javafxclient.application.dto.ProductOverviewDTO;
import at.fhv.ec.javafxclient.application.impl.ProductSearchServiceImpl;
import at.fhv.ec.javafxclient.view.components.ProductListCell;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.List;

public class SearchController {
    @FXML
    private TextField searchTextField;

    @FXML
    private ListView<ProductOverviewDTO> productList;

    // TODO: Use something like dependency injection in springboot
    private final ProductSearchService productSearchService = new ProductSearchServiceImpl();

    public void initialize() {
        productList.setCellFactory(lv -> new ProductListCell());
    }

    @FXML
    protected void onSearchButtonClicked() {
        String searchTerm = searchTextField.getText();
        List<ProductOverviewDTO> products = productSearchService.fullTextSearch(searchTerm);

        productList.getItems().clear();
        productList.getItems().addAll(products);
    }
}
