package at.fhv.ec.javafxclient.view.controller;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.SessionManager;
import at.fhv.ec.javafxclient.communication.EJBClient;
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
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import javax.naming.NamingException;
import java.io.IOException;
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
    private Label priceLabel;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private Label feedbackLabel;
    @FXML
    private HBox bottomBox;
    @FXML
    private TableView<SaleItemEntry> saleItemsTable;
    @FXML
    private TableColumn<SaleItemEntry, String> productNameColumn;
    @FXML
    private TableColumn<SaleItemEntry, Float> pricePerCarrierColumn;
    @FXML
    private TableColumn<SaleItemEntry, Spinner<Integer>> refundCarrierColumn;

    @FXML
    public void initialize() {
        displayContent(false);
        formatTable();
    }

    @FXML
    public void onSearchButtonClicked() {
        displayContent(false);
        invoiceNumberLabel.getStyleClass().remove("alert");

        try {
            SaleSearchService saleSearchService = EJBClient.getEjbClient().getSaleSearchService();
            SaleDTO sale = saleSearchService.saleByInvoiceNumber(SessionManager.getInstance().getSessionId(), searchTextField.getText());
            refundedSaleItems = new ArrayList<>();
            sale.getSaleItems().forEach(saleItem -> refundedSaleItems.add(new SaleItemEntry(
                    saleItem.getProductName(),
                    saleItem.getArtistName(),
                    saleItem.getSoundCarrierId(),
                    saleItem.getSoundCarrierName(),
                    saleItem.getAmountOfCarriers(),
                    saleItem.getPricePerCarrier(),
                    saleItem.getRefundedAmount()
            )));

            invoiceNumberLabel.setText(sale.getInvoiceNumber());
            ObservableList<SaleItemEntry> saleItemsTableData = FXCollections.observableArrayList(refundedSaleItems);
            saleItemsTable.setItems(saleItemsTableData);
            saleItemsTable.getSortOrder().add(productNameColumn);
            saleItemsTable.sort();

            totalPriceLabel.setText(sale.getTotalPrice() + "???");
            displayContent(true);

            saleItemsTable.setVisible(true);
        } catch (NoSuchElementException ne) {

            invoiceNumberLabel.setText("No sale with invoice number " + searchTextField.getText() + " found!");
            invoiceNumberLabel.getStyleClass().add("alert");
            invoiceNumberLabel.setVisible(true);

        } catch (SessionExpired | NoPermissionForOperation e) {

            e.printStackTrace();
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onHomeButtonClicked() {
        SceneManager.getInstance().switchView(SceneManager.VIEW_EXCHANGE);
    }

    @FXML
    public void onRefundButtonClicked() {
        feedbackLabel.getStyleClass().remove("alert");

        try {
            RefundSaleService refundSaleService = EJBClient.getEjbClient().getRefundSaleService();
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
                feedbackLabel.setText("Refund successful!");
                formatTable();
            } else {
                feedbackLabel.getStyleClass().add("alert");
                feedbackLabel.setText("You have to select at least one item to refund!");
            }
        } catch (NoPermissionForOperation | SessionExpired e) {
            e.printStackTrace();
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    private void formatTable() {
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
                        String pricePerCarrierStr = pricePerCarrier + "???";
                        setText(pricePerCarrierStr);
                    }
                    }
                };
            }
        });

        Callback<TableColumn<SaleItemEntry, Spinner<Integer>>, TableCell<SaleItemEntry, Spinner<Integer>>> spinnerCellFactory = new Callback<>() {
            @Override
            public TableCell<SaleItemEntry, Spinner<Integer>> call(final TableColumn<SaleItemEntry, Spinner<Integer>> param) {
                return new TableCell<>() {

                    @Override
                    public void updateItem(Spinner<Integer> item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Spinner<Integer> refundAmountSpinner = new Spinner<>();
                            SaleItemEntry selectedItem = getTableView().getItems().get(getIndex());
                            int maximumRefundableAmount = selectedItem.getAmountOfCarriers() - selectedItem.getRefundedAmount();

                            if(maximumRefundableAmount != 0) {
                                refundAmountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, maximumRefundableAmount, 0));

                                refundAmountSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                                    SaleItemEntry refundedSaleItem = refundedSaleItems.get(getIndex());
                                    refundedSaleItem.setAmountToRefund(newValue);});

                                setGraphic(refundAmountSpinner);
                            }
                        }
                    }
                };
            }
        };
        refundCarrierColumn.setCellFactory(spinnerCellFactory);
    }

    private void displayContent(boolean show) {
        invoiceNumberLabel.setVisible(show);
        priceLabel.setVisible(show);
        totalPriceLabel.setVisible(show);
        saleItemsTable.setVisible(show);
        bottomBox.setVisible(show);
    }

}