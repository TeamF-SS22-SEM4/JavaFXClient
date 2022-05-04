package at.fhv.ec.javafxclient.view.controller;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.SessionManager;
import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ss22.ea.f.communication.api.CustomerService;
import at.fhv.ss22.ea.f.communication.dto.CustomerDTO;
import at.fhv.ss22.ea.f.communication.exception.NoPermissionForOperation;
import at.fhv.ss22.ea.f.communication.exception.SessionExpired;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.rmi.RemoteException;
import java.util.List;

public class CustomerController {

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
        String searchTerm = "b";
        searchCustomer(searchTerm);
        formatTable();
    }

    @FXML
    public void onSearchButtonClicked() {
        String searchTerm = searchTextField.getText();
        searchCustomer(searchTerm);
    }

    @FXML
    public void onHomeButtonClicked() {
        SceneManager.getInstance().switchView(SceneManager.VIEW_CUSTOMER);
    }

    private void searchCustomer(String searchTerm) {
        try {
            CustomerService customerService = RMIClient.getRmiClient().getRmiFactory().getCustomerSearchService();
            List<CustomerDTO> customers = customerService.search(SessionManager.getInstance().getSessionId(), searchTerm);

            ObservableList<CustomerDTO> customerTableData = FXCollections.observableArrayList(customers);
            customerTable.setItems(customerTableData);
            customerTable.getSortOrder().add(lastNameColumn);
            customerTable.sort();
        } catch (RemoteException | NoPermissionForOperation | SessionExpired e) {
            e.printStackTrace();
        }
    }

    private void formatTable() {
        addToSaleColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<CustomerDTO, Button> call(TableColumn<CustomerDTO, Button> param) {
                return new TableCell<>() {

                    @Override
                    public void updateItem(Button item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Button addToSaleButton = new Button("Add to sale");
                            addToSaleButton.getStyleClass().add("btn-success");
                            addToSaleButton.setOnAction(event -> {
                                ShoppingCartController.customer = getTableView().getItems().get(getIndex());
                                SceneManager.getInstance().switchView(SceneManager.VIEW_SHOPPING_CART);
                            });
                            setGraphic(addToSaleButton);
                        }
                    }
                };
            }
        });
    }

}