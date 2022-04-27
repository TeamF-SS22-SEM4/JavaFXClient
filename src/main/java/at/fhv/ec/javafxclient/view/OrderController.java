package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.communication.OrderingClient;
import at.fhv.ss22.ea.f.communication.dto.DetailedOrderDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
    private TableColumn<DetailedOrderDTO, UUID> denyColumn;

    @FXML
    private TableColumn<DetailedOrderDTO, UUID> approveColumn;

    @FXML
    public void initialize() {

        List<DetailedOrderDTO> orders = orderingClient.getActiveOrders();

        this.createTableCallbacks();

        ObservableList<DetailedOrderDTO> orderTableData = FXCollections.observableArrayList(orders);
        ordersTable.setItems(orderTableData);
    }

    private void createTableCallbacks() {
        approveColumn.setCellFactory(new Callback<TableColumn<DetailedOrderDTO, UUID>, TableCell<DetailedOrderDTO, UUID>>() {
            @Override
            public TableCell<DetailedOrderDTO, UUID> call(TableColumn<DetailedOrderDTO, UUID> param) {
                return new TableCell<>() {
                    private final Button approveButton = new Button("Approve");

                    @Override
                    protected void updateItem(UUID id, boolean empty) {
                        super.updateItem(id, empty);
                        if (empty || id ==null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            approveButton.getStyleClass().add("btn-success");
                            approveButton.setOnAction(event -> {
                                orderingClient.approveOrder(id);
                            });
                            setGraphic(approveButton);
                        }
                    }
                };
            }
        });

        denyColumn.setCellFactory(new Callback<TableColumn<DetailedOrderDTO, UUID>, TableCell<DetailedOrderDTO, UUID>>() {
            @Override
            public TableCell<DetailedOrderDTO, UUID> call(TableColumn<DetailedOrderDTO, UUID> param) {
                return new TableCell<>() {
                    private final Button denyButton = new Button("Deny");

                    @Override
                    protected void updateItem(UUID id, boolean empty) {
                        super.updateItem(id, empty);
                        if (empty || id ==null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            denyButton.getStyleClass().add("btn");
                            denyButton.setOnAction(event -> {
                                orderingClient.denyOrder(id);
                            });
                            setGraphic(denyButton);
                        }
                    }
                };
            }
        });

        employeeColumn.setCellFactory(new Callback<TableColumn<DetailedOrderDTO, String>, TableCell<DetailedOrderDTO, String>>() {
            @Override
            public TableCell<DetailedOrderDTO, String> call(TableColumn<DetailedOrderDTO, String> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(String employee, boolean empty) {
                        super.updateItem(employee, empty);
                        if (empty || employee == null) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            setText(employee);
                        }
                    }
                };
            }
        });

        amountColumn.setCellFactory(new Callback<TableColumn<DetailedOrderDTO, Integer>, TableCell<DetailedOrderDTO, Integer>>() {
            @Override
            public TableCell<DetailedOrderDTO, Integer> call(TableColumn<DetailedOrderDTO, Integer> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Integer amount, boolean empty) {
                        super.updateItem(amount, empty);
                        if (empty || amount == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(amount.toString());
                        }
                    }
                };
            }
        });

        productNameColumn.setCellFactory(new Callback<TableColumn<DetailedOrderDTO, String>, TableCell<DetailedOrderDTO, String>>() {
            @Override
            public TableCell<DetailedOrderDTO, String> call(TableColumn<DetailedOrderDTO, String> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(String productName, boolean empty) {
                        super.updateItem(productName, empty);
                        if (empty || productName == null) {
                            setText("");
                        } else {
                            setText(productName);
                        }
                    }
                };
            }
        });
        carrierTypeColumn.setCellFactory(new Callback<TableColumn<DetailedOrderDTO, String>, TableCell<DetailedOrderDTO, String>>() {
            @Override
            public TableCell<DetailedOrderDTO, String> call(TableColumn<DetailedOrderDTO, String> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(String type, boolean empty) {
                        super.updateItem(type, empty);
                        if (empty || type == null) {
                            setText("");
                        } else {
                            setText(type);
                        }
                    }
                };
            }
        });
    }

}
