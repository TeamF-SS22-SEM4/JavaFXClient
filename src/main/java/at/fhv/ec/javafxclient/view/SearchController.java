package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ec.javafxclient.view.forms.ShoppingCartForm;
import at.fhv.ss22.ea.f.communication.api.ProductSearchService;
import at.fhv.ss22.ea.f.communication.dto.ProductOverviewDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class SearchController {
    // TODO: Use something like dependency injection in springboot
    private ProductSearchService productSearchService;
    private static List<ProductOverviewDTO> products = new ArrayList<>();

    @FXML
    private TextField searchTextField;

    @FXML
    private TableView<ProductOverviewDTO> productTable;

    @FXML
    private TableColumn<ProductOverviewDTO, Button> actionColumn;

    @FXML
    public void initialize() {
        try {
            productSearchService = RMIClient.getRmiClient().getRmiFactory().getProductSearchService();
            products = productSearchService.fullTextSearch("");
            fillTable();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        createTable();

        fillTable();
    }

    @FXML
    protected void onSearchButtonClicked() {
        try {
            String searchTerm = searchTextField.getText();
            products = productSearchService.fullTextSearch(searchTerm);
            fillTable();

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onClearButtonClicked() {
        productTable.getItems().clear();
        products.clear();
        searchTextField.clear();
        try {
            String searchTerm = searchTextField.getText();
            products = productSearchService.fullTextSearch(searchTerm);
            fillTable();

        } catch (RemoteException e) {
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
        // Add Button to table to switch to DetailsView
        // TODO: find a more beautiful solution
        actionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<ProductOverviewDTO, Button> call(TableColumn<ProductOverviewDTO, Button> param) {
                return new TableCell<>() {

                    final Button detailsButton = new Button("Details");

                    @Override
                    public void updateItem(Button item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            detailsButton.setOnAction(event -> {
                                try {
                                    DetailsController.productId = getTableView().getItems().get(getIndex()).getProductId();
                                    SceneManager.getInstance().switchView("views/details-view.fxml");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                            setGraphic(detailsButton);
                            setText(null);
                        }
                    }
                };
            }
        });
    }

    private void fillTable() {
        ObservableList<ProductOverviewDTO> productListData = FXCollections.observableArrayList(products);
        productTable.setItems(productListData);
    }

    public void onEnter() {
        onSearchButtonClicked();
    }
}