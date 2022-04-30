package at.fhv.ec.javafxclient.view.controller;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.SessionManager;
import at.fhv.ec.javafxclient.communication.EJBClient;
import at.fhv.ss22.ea.f.communication.api.CustomerService;
import at.fhv.ss22.ea.f.communication.dto.CustomerDTO;
import at.fhv.ss22.ea.f.communication.exception.NoPermissionForOperation;
import at.fhv.ss22.ea.f.communication.exception.SessionExpired;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import javax.naming.NamingException;
import java.util.List;

public class CustomerController {
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


        String searchTerm = "b";
        try {
            customerService = EJBClient.getEjbClient().getCustomerService();
            List<CustomerDTO> customers = customerService.search(SessionManager.getInstance().getSessionId(), searchTerm);

            ObservableList<CustomerDTO> customerTableData = FXCollections.observableArrayList(customers);
            customerTable.setItems(customerTableData);
            customerTable.getSortOrder().add(lastNameColumn);
            customerTable.sort();
        } catch (NoPermissionForOperation e) {
            e.printStackTrace();
        } catch (SessionExpired e) {
            e.printStackTrace();
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }


        // Show add to sale button only when user comes from checkout view
        if(ShoppingCartController.shoppingCart.size() > 0) {
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
                                        SceneManager.getInstance().switchView(SceneManager.VIEW_CHECKOUT);
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
            customerService = EJBClient.getEjbClient().getCustomerService();
            List<CustomerDTO> customers = customerService.search(SessionManager.getInstance().getSessionId(), searchTerm);

            ObservableList<CustomerDTO> customerTableData = FXCollections.observableArrayList(customers);
            customerTable.setItems(customerTableData);
            customerTable.getSortOrder().add(lastNameColumn);
            customerTable.sort();
        } catch (NoPermissionForOperation | SessionExpired e) {
            e.printStackTrace();
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public void onHomeButtonClicked() {
        SceneManager.getInstance().switchView(SceneManager.VIEW_CUSTOMER);
    }
}
