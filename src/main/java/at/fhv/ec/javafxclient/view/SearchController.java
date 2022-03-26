package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ss22.ea.f.communication.api.ProductSearchService;
import at.fhv.ss22.ea.f.communication.dto.ProductOverviewDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

public class SearchController {
    SceneManager sceneManager;
    ProductSearchService productSearchService;
    {
        try {
            productSearchService = RMIClient.getRmiClient().getRmiFactory().getProductSearchService();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private TextField searchTextField;

    @FXML
    private TableView<ProductOverviewDTO> productTable;

    @FXML
    public void initialize() {
        productTable.setRowFactory(tv -> {
            TableRow<ProductOverviewDTO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2) {
                    try {
                        SceneManager.getInstance().switchToDetailsView(row.getItem().getProductId());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            return row;
        });
    }

    @FXML
    protected void onSearchButtonClicked() {
        try {
            // TODO: Use something like dependency injection in springboot
            String searchTerm = searchTextField.getText();
            List<ProductOverviewDTO> products = productSearchService.fullTextSearch(searchTerm);

            ObservableList<ProductOverviewDTO> productListData = FXCollections.observableArrayList(products);
            productTable.setItems(productListData);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}