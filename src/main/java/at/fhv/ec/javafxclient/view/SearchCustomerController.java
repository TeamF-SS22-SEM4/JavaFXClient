package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ss22.ea.f.communication.api.CustomerService;
import at.fhv.ss22.ea.f.communication.dto.CustomerDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

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
    protected void onSearchButtonClicked() {
        String searchTerm = searchTextField.getText();
        try {
            customerService = RMIClient.getRmiClient().getRmiFactory().getCustomerSearchService();
            List<CustomerDTO> customers = customerService.search(searchTerm);

            ObservableList<CustomerDTO> customerTableData = FXCollections.observableArrayList(customers);
            customerTable.setItems(customerTableData);
            customerTable.getSortOrder().add(lastNameColumn);
            customerTable.sort();
        } catch (RemoteException e) {
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
