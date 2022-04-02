package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ec.javafxclient.view.forms.RefundedSaleItem;
import at.fhv.ec.javafxclient.view.forms.ShoppingCartEntry;
import at.fhv.ss22.ea.f.communication.api.RefundSaleService;
import at.fhv.ss22.ea.f.communication.api.SaleSearchService;
import at.fhv.ss22.ea.f.communication.dto.RefundedSaleItemDTO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class SearchSaleController {
    private static List<RefundedSaleItem> refundedSaleItems;

    @FXML
    private TextField searchTextField;

    @FXML
    private Pane contentPaneTop;

    @FXML
    private Label invoiceNumberLabel;

    @FXML
    private TableView<RefundedSaleItem> saleItemsTable;

    @FXML
    private TableColumn<RefundedSaleItem, Float> pricePerCarrierColumn;

    @FXML
    private TableColumn<RefundedSaleItem, Spinner<Integer>> refundCarrierColumn;

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
            public TableCell<RefundedSaleItem, Float> call(TableColumn<RefundedSaleItem, Float> param) {
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

        // TODO: use a more beautiful solution
        Callback<TableColumn<RefundedSaleItem, Spinner<Integer>>, TableCell<RefundedSaleItem, Spinner<Integer>>> spinnerCellFactory = new Callback<>() {
            @Override
            public TableCell<RefundedSaleItem, Spinner<Integer>> call(final TableColumn<RefundedSaleItem, Spinner<Integer>> param) {
                return new TableCell<>() {

                    private final Spinner<Integer> refundAmountSpinner = new Spinner<>();

                    @Override
                    public void updateItem(Spinner<Integer> item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            refundAmountSpinner.setValueFactory(
                                    new SpinnerValueFactory.IntegerSpinnerValueFactory(
                                            0,
                                            getTableView().getItems().get(getIndex()).getAmountOfCarriers(),
                                            0
                                    )
                            );

                            refundAmountSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                                RefundedSaleItem refundedSaleItem = refundedSaleItems.get(getIndex());
                                refundedSaleItem.setAmountToRefund(newValue);
                            });

                            setGraphic(refundAmountSpinner);
                        }
                    }
                };
            }
        };

        refundCarrierColumn.setCellFactory(spinnerCellFactory);
    }

    @FXML
    public void onEnter() {
        onSearchButtonClicked();
    }

    @FXML
    protected void onSearchButtonClicked() {
        try {
            SaleSearchService saleSearchService = RMIClient.getRmiClient().getRmiFactory().getSaleSearchService();
            SaleDTO sale = saleSearchService.saleByInvoiceNumber(searchTextField.getText());



            refundedSaleItems = new ArrayList<>();
            sale.getSaleItems().forEach(saleItem ->
                    refundedSaleItems.add(new RefundedSaleItem(
                            saleItem.getProductName(),
                            saleItem.getArtistName(),
                            saleItem.getSoundCarrierId(),
                            saleItem.getSoundCarrierName(),
                            saleItem.getAmountOfCarriers(),
                            saleItem.getPricePerCarrier(),
                            saleItem.isRefunded()
                    ))
            );

            invoiceNumberLabel.setText(sale.getInvoiceNumber());
            ObservableList<RefundedSaleItem> saleItemsTableData = FXCollections.observableArrayList(refundedSaleItems);
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

    @FXML
    protected void onRefundButtonClicked() {
        try {
            RefundSaleService refundSaleService = RMIClient.getRmiClient().getRmiFactory().getRefundedSaleService();
            List<RefundedSaleItemDTO> refundedSaleItemDTOs = new ArrayList<>();
            refundedSaleItems.forEach(refundedSaleItem -> {
                if(refundedSaleItem.getAmountToRefund() > 0) {
                    refundedSaleItemDTOs.add(
                            RefundedSaleItemDTO.builder()
                                    .withSoundCarrierId(refundedSaleItem.getSoundCarrierId())
                                    .withAmountToRefund(refundedSaleItem.getAmountToRefund())
                                    .build()
                    );
                }
            });

            if(refundedSaleItemDTOs.size() > 0) {
                refundSaleService.refundSale(invoiceNumberLabel.getText(), refundedSaleItemDTOs);
                onSearchButtonClicked();
                showPopup("Sale Items refunded", "Refund successful", Alert.AlertType.INFORMATION);
            } else {
                showPopup("Refund not possible", "You have to select at least one item to refund", Alert.AlertType.ERROR);
            }
        } catch (RemoteException e) {
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