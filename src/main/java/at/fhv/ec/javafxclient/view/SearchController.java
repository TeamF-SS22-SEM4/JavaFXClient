package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ss22.ea.f.communication.api.ProductSearchService;
import at.fhv.ec.javafxclient.view.components.ProductListCell;
import at.fhv.ss22.ea.f.communication.dto.ProductOverviewDTO;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.rmi.RemoteException;
import java.util.List;

public class SearchController {
    @FXML
    private TextField searchTextField;

    @FXML
    private ListView<ProductOverviewDTO> productList;

    public void initialize() {
        productList.setCellFactory(lv -> new ProductListCell());
    }

    @FXML
    protected void onSearchButtonClicked() {
        try {
            // TODO: Use something like dependency injection in springboot
            ProductSearchService productSearchService = RMIClient.getRmiClient().getRmiFactory().getProductSearchService();
            String searchTerm = searchTextField.getText();
            List<ProductOverviewDTO> products = productSearchService.fullTextSearch(searchTerm);
            productList.getItems().clear();
            productList.getItems().addAll(products);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
