package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ec.javafxclient.view.forms.ShoppingCartForm;
import at.fhv.ss22.ea.f.communication.api.SaleSearchService;
import at.fhv.ss22.ea.f.communication.dto.ProductOverviewDTO;
import at.fhv.ss22.ea.f.communication.dto.SaleDTO;
import at.fhv.ss22.ea.f.communication.dto.SaleItemDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.NoSuchElementException;

public class SearchSaleController {
    private SaleSearchService saleSearchService;

    @FXML
    private TextField searchTextField;

    @FXML
    private Pane contentPaneTop;

    @FXML
    private Label invoiceNumberLabel;

    @FXML
    private TableView<SaleItemDTO> saleItemsTable;

    @FXML
    private TableColumn<SaleItemDTO, Float> pricePerCarrierColumn;

    @FXML
    private Pane contentPaneBottom;

    @FXML
    private Label totalPriceLabel;

    @FXML
    public void initialize() {
        // Hide elements
        contentPaneTop.setVisible(false);
        saleItemsTable.setVisible(false);
        contentPaneBottom.setVisible(false);

        // Fomat table columns
        pricePerCarrierColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<SaleItemDTO, Float> call(TableColumn<SaleItemDTO, Float> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Float pricePerCarrier, boolean empty) {
                        super.updateItem(pricePerCarrier, empty);
                        if (empty || pricePerCarrier == null) {
                            setText("");
                        } else {
                            String pricePerCarrierStr = pricePerCarrier + "€";

                            setText(pricePerCarrierStr);
                        }
                    }
                };
            }
        });
    }

    @FXML
    public void onEnter() {
        onSearchButtonClicked();
    }

    @FXML
    protected void onSearchButtonClicked() {
        try {
            saleSearchService = RMIClient.getRmiClient().getRmiFactory().getSaleSearchService();
            SaleDTO sale = saleSearchService.saleByInvoiceNumber(searchTextField.getText());

            invoiceNumberLabel.setText(sale.getInvoiceNumber());
            ObservableList<SaleItemDTO> saleItemsTableData = FXCollections.observableArrayList(sale.getSaleItems());
            saleItemsTable.setItems(saleItemsTableData);

            totalPriceLabel.setText(sale.getTotalPrice() + "€");

            contentPaneTop.setVisible(true);
            saleItemsTable.setVisible(true);
            contentPaneBottom.setVisible(true);
        } catch (RemoteException e) {
            showPopup("Connection Error", "A connection error occured.", Alert.AlertType.ERROR);
        } catch (NoSuchElementException ne) {
            showPopup("Sale not found", "Sale " + searchTextField.getText() + " not found", Alert.AlertType.ERROR);
        }
    }

    @FXML
    protected void onClearButtonClicked() {
        contentPaneTop.setVisible(false);
        saleItemsTable.setVisible(false);
        contentPaneBottom.setVisible(false);

        invoiceNumberLabel.setText("");
        saleItemsTable.getItems().clear();
        totalPriceLabel.setText("");
    }

    @FXML
    protected void onBackButtonClicked() {
        try {
            SceneManager.getInstance().switchView("views/product-search-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void showPopup(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(message);
        ButtonType confirmButton = new ButtonType("Ok");
        alert.getButtonTypes().setAll(confirmButton);
        alert.show();
    }
}