package at.fhv.ec.javafxclient.view.controller;

import at.fhv.ec.javafxclient.communication.OrderingClient;
import at.fhv.ss22.ea.f.communication.dto.DetailedOrderDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.util.List;
import java.util.UUID;

public class OrderController {

    private OrderingClient orderingClient = OrderingClient.getInstance();

    @FXML
    private TableView<DetailedOrderDTO> ordersTable;

    @FXML
    private TableColumn<DetailedOrderDTO, String> productNameColumn;

    @FXML
    private TableColumn<DetailedOrderDTO, String> carrierTypeColumn;

    @FXML
    private TableColumn<DetailedOrderDTO, String> employeeColumn;

    @FXML
    private TableColumn<DetailedOrderDTO, Integer> amountColumn;

    @FXML
    private TableColumn<DetailedOrderDTO, UUID> actionColumn;

    @FXML
    public void initialize() {

        List<DetailedOrderDTO> orders = orderingClient.getActiveOrders();

        this.createTableCallbacks();

        ObservableList<DetailedOrderDTO> orderTableData = FXCollections.observableArrayList(orders);
        ordersTable.setItems(orderTableData);
    }

    private void createTableCallbacks() {
        actionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<DetailedOrderDTO, UUID> call(TableColumn<DetailedOrderDTO, UUID> param) {
                return new TableCell<>() {

                    @Override
                    protected void updateItem(UUID id, boolean empty) {
                        super.updateItem(id, empty);
                        if (empty || id == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            HBox wrapperBox = new HBox();
                            wrapperBox.setSpacing(10);
                            wrapperBox.setAlignment(Pos.CENTER);

                            Button approveButton = new Button("✔");
                            approveButton.getStyleClass().add("btn-success");
                            approveButton.setOnAction(event -> orderingClient.approveOrder(id));

                            Button denyButton = new Button("❌");
                            denyButton.getStyleClass().add("btn-alert");
                            denyButton.setOnAction(event -> orderingClient.denyOrder(id));

                            wrapperBox.getChildren().addAll(approveButton, denyButton);

                            setGraphic(wrapperBox);
                        }
                    }
                };
            }
        });
    }
}
