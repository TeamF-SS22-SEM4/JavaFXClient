package at.fhv.ec.javafxclient.view.controller;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.SessionManager;
import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ec.javafxclient.model.SaleItemEntry;
import at.fhv.ss22.ea.f.communication.api.RefundSaleService;
import at.fhv.ss22.ea.f.communication.api.SaleSearchService;
import at.fhv.ss22.ea.f.communication.dto.RefundedSaleItemDTO;
import at.fhv.ss22.ea.f.communication.dto.SaleDTO;
import at.fhv.ss22.ea.f.communication.exception.NoPermissionForOperation;
import at.fhv.ss22.ea.f.communication.exception.SessionExpired;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ExchangeController {
    private static List<SaleItemEntry> refundedSaleItems;

    @FXML
    private TextField searchTextField;

    @FXML
    private Label invoiceNumberLabel;

    @FXML
    private TableView<SaleItemEntry> saleItemsTable;

    @FXML
    private TableColumn<SaleItemEntry, String> productNameColumn;

    @FXML
    private TableColumn<SaleItemEntry, Float> pricePerCarrierColumn;

    @FXML
    private TableColumn<SaleItemEntry, Spinner<Integer>> refundCarrierColumn;

    @FXML
    private VBox refundBox;

    @FXML
    private Label totalPriceLabel;

    @FXML
    public void initialize() {

        invoiceNumberLabel.setVisible(false);
        saleItemsTable.setVisible(false);
        refundBox.setVisible(false);

        // Fomat table columns
        pricePerCarrierColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<SaleItemEntry, Float> call(TableColumn<SaleItemEntry, Float> param) {
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
        Callback<TableColumn<SaleItemEntry, Spinner<Integer>>, TableCell<SaleItemEntry, Spinner<Integer>>> spinnerCellFactory = new Callback<>() {
            @Override
            public TableCell<SaleItemEntry, Spinner<Integer>> call(final TableColumn<SaleItemEntry, Spinner<Integer>> param) {
                return new TableCell<>() {

                    private final Spinner<Integer> refundAmountSpinner = new Spinner<>();

                    @Override
                    public void updateItem(Spinner<Integer> item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            SaleItemEntry selectedItem = getTableView().getItems().get(getIndex());
                            int maximumRefundableAmount = selectedItem.getAmountOfCarriers() - selectedItem.getRefundedAmount();

                            if(maximumRefundableAmount != 0) {
                                refundAmountSpinner.setValueFactory(
                                        new SpinnerValueFactory.IntegerSpinnerValueFactory(
                                                0,
                                                maximumRefundableAmount,
                                                0
                                        )
                                );

                                refundAmountSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                                    SaleItemEntry refundedSaleItem = refundedSaleItems.get(getIndex());
                                    refundedSaleItem.setAmountToRefund(newValue);
                                });

                                setGraphic(refundAmountSpinner);
                            }
                        }
                    }
                };
            }
        };

        refundCarrierColumn.setCellFactory(spinnerCellFactory);
    }

    public void onHomeButtonClicked() {
        SceneManager.getInstance().switchView(SceneManager.VIEW_EXCHANGE);
    }

    @FXML
    protected void onSearchButtonClicked() {

        try {
            SaleSearchService saleSearchService = RMIClient.getRmiClient().getRmiFactory().getSaleSearchService();
            SaleDTO sale = saleSearchService.saleByInvoiceNumber(SessionManager.getInstance().getSessionId(), searchTextField.getText());

            refundedSaleItems = new ArrayList<>();
            sale.getSaleItems().forEach(saleItem -> {
                refundedSaleItems.add(new SaleItemEntry(
                        saleItem.getProductName(),
                        saleItem.getArtistName(),
                        saleItem.getSoundCarrierId(),
                        saleItem.getSoundCarrierName(),
                        saleItem.getAmountOfCarriers(),
                        saleItem.getPricePerCarrier(),
                        saleItem.getRefundedAmount()
                ));
            });

            invoiceNumberLabel.setText(sale.getInvoiceNumber());
            ObservableList<SaleItemEntry> saleItemsTableData = FXCollections.observableArrayList(refundedSaleItems);
            saleItemsTable.setItems(saleItemsTableData);
            saleItemsTable.getSortOrder().add(productNameColumn);
            saleItemsTable.sort();

            totalPriceLabel.setText(sale.getTotalPrice() + "€");

            refundBox.setVisible(true);
            saleItemsTable.setVisible(true);
            refundBox.setVisible(true);
        } catch (RemoteException e) {
            showPopup("Connection Error", "A connection error occured.", Alert.AlertType.ERROR);
        } catch (NoSuchElementException ne) {
            showPopup("Sale not found", "Sale " + searchTextField.getText() + " not found", Alert.AlertType.ERROR);
        } catch (SessionExpired | NoPermissionForOperation e) {
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
                refundSaleService.refundSale(SessionManager.getInstance().getSessionId(), invoiceNumberLabel.getText(), refundedSaleItemDTOs);
                onSearchButtonClicked();
                showPopup("Sale Items refunded", "Refund successful", Alert.AlertType.INFORMATION);
            } else {
                showPopup("Refund not possible", "You have to select at least one item to refund", Alert.AlertType.ERROR);
            }
        } catch (RemoteException | NoPermissionForOperation | SessionExpired e) {
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