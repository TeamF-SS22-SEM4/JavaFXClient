package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ss22.ea.f.communication.api.CustomerService;
import at.fhv.ss22.ea.f.communication.dto.CustomerDTO;
import at.fhv.ss22.ea.f.communication.dto.SoundCarrierDTO;
import at.fhv.ss22.ea.f.communication.exception.NoPermissionForOperation;
import at.fhv.ss22.ea.f.communication.exception.SessionExpired;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

public class SearchCustomerController {
    CustomerService customerService;

    @FXML
    private TextField searchTextField;

    @FXML
    private TableView<CustomerDTO> customerTable;

    @FXML
    private TableColumn<CustomerDTO, String> lastNameColumn;

    @FXML
    private TableColumn<CustomerDTO, Button> addToSaleColumn;

    @FXML
    public void initialize() {
        addToSaleColumn.setVisible(false);

        // Show add to sale button only when user comes from checkout view
        if(SceneManager.getInstance().getLastView().equals("checkout-view")) {
            addToSaleColumn.setCellFactory(new Callback<>() {
                @Override
                public TableCell<CustomerDTO, Button> call(TableColumn<CustomerDTO, Button> param) {
                    return new TableCell<>() {
                        private final Button addToSaleButton = new Button("Add to sale");

                        @Override
                        public void updateItem(Button item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                                setText(null);
                            } else {
                                addToSaleButton.setOnAction(event -> {
                                    CheckoutController.customer = getTableView().getItems().get(getIndex());
                                    try {
                                        SceneManager.getInstance().switchView("customer-search", "checkout-view");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                                setGraphic(addToSaleButton);
                                setText(null);
                            }
                        }
                    };
                }
            });

            addToSaleColumn.setVisible(true);
        }
    }

    @FXML
    protected void onSearchButtonClicked() {
        String searchTerm = searchTextField.getText();
        try {
            customerService = RMIClient.getRmiClient().getRmiFactory().getCustomerSearchService();
            List<CustomerDTO> customers = customerService.search(LoginController.sessionInformation.getSessionId(), searchTerm);

            ObservableList<CustomerDTO> customerTableData = FXCollections.observableArrayList(customers);
            customerTable.setItems(customerTableData);
            customerTable.getSortOrder().add(lastNameColumn);
            customerTable.sort();
        } catch (RemoteException | NoPermissionForOperation e) {
            e.printStackTrace();
        } catch (SessionExpired e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onEnter() {
        onSearchButtonClicked();
    }

    @FXML
    protected void onBackButtonClicked() {
        try {
            SceneManager.getInstance().back();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onClearButtonClicked() {
        customerTable.getItems().clear();
        searchTextField.clear();
    }
}
