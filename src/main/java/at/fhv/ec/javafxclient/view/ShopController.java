package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.Main;
import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.SessionManager;
import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ec.javafxclient.view.animator.TextOutput;
import at.fhv.ec.javafxclient.view.animator.TextAnimator;
import at.fhv.ss22.ea.f.communication.api.ProductSearchService;
import at.fhv.ss22.ea.f.communication.dto.ProductOverviewDTO;
import at.fhv.ss22.ea.f.communication.exception.NoPermissionForOperation;
import at.fhv.ss22.ea.f.communication.exception.SessionExpired;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.HBox;

import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import java.util.ResourceBundle;

public class ShopController implements Initializable{

    private TextAnimator textAnimator;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        String[] texts = new String[4];
        texts[0] = "artist";
        texts[1] = "album";
        texts[2] = "genre";
        texts [3] ="release";

        TextOutput textOutput = text -> searchTextField.setPromptText(text);

        textAnimator = new TextAnimator(1000,75,"Search for ", texts, textOutput, true );

        Thread th = new Thread(textAnimator);
        th.start();

        try {
            productSearchService = RMIClient.getRmiClient().getRmiFactory().getProductSearchService();
            products = productSearchService.fullTextSearch(SessionManager.getInstance().getSessionId(), "");
            fillProductTable();
        } catch (RemoteException | SessionExpired | NoPermissionForOperation e) {
            e.printStackTrace();
        }

        createProductTable();

        fillProductTable();
    }















    public HBox ct;
    // TODO: Use something like dependency injection in springboot
    private ProductSearchService productSearchService;
    private static List<ProductOverviewDTO> products = new ArrayList<>();

    @FXML
    private TextField searchTextField;

    @FXML
    private TableView<ProductOverviewDTO> productTable;

    @FXML
    private TableColumn<ProductOverviewDTO, String> nameColumn;

    @FXML
    private TableColumn<ProductOverviewDTO, Button> detailsColumn;

    @FXML
    private TableColumn<ProductOverviewDTO, Button> addToCartColumn;


    @FXML
    private void onShoppingCartButtonClicked() throws IOException {
        SceneManager.getInstance().switchView("shoppingcart");
    }

    @FXML
    protected void onSearchButtonClicked() {
        try {
            String searchTerm = searchTextField.getText();
            products = productSearchService.fullTextSearch(SessionManager.getInstance().getSessionId(), searchTerm);
            searchTextField.setPromptText(searchTerm);
            searchTextField.selectAll();
            fillProductTable();

        } catch (RemoteException | NoPermissionForOperation | SessionExpired e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onHomeButtonClicked() {
        try {
            String searchTerm = "";
            products = productSearchService.fullTextSearch(SessionManager.getInstance().getSessionId(), searchTerm);
            searchTextField.setText(searchTerm);
            fillProductTable();

        } catch (RemoteException | NoPermissionForOperation | SessionExpired e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onClearButtonClicked() {
        productTable.getItems().clear();
        products.clear();
        searchTextField.clear();
    }



    private void createProductTable() {
        // Initialize Table Columns
        // Add Button to table to switch to DetailsView
        // TODO: find a more beautiful solution
        detailsColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<ProductOverviewDTO, Button> call(TableColumn<ProductOverviewDTO, Button> param) {

                final Button detailsButton = new Button("Details");
                detailsButton.getStyleClass().add("btn");

                return new TableCell<>() {

                    @Override
                    public void updateItem(Button item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            detailsButton.setOnAction(event -> {
                                try {
                                    ProductDetailsController.productId = getTableView().getItems().get(getIndex()).getProductId();
                                    SceneManager.getInstance().switchView("productdetails");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                            setGraphic(detailsButton);
                            setText(null);
                        }
                    }
                };
            }
        });

        addToCartColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<ProductOverviewDTO, Button> call(TableColumn<ProductOverviewDTO, Button> param) {

                final Button detailsButton = new Button("Add");
                detailsButton.getStyleClass().add("btn-green");
                detailsButton.setOnAction(e -> {

                    ColorAdjust colorAdjust = new ColorAdjust();

                    colorAdjust.setBrightness(-0.5);

                    Dialog d = new Dialog();
                    d.initStyle(StageStyle.UNDECORATED);
                    ct.setEffect(colorAdjust);
                    d.getDialogPane().getStylesheets().add(Main.returnStylesheet());
                    d.getDialogPane().getStyleClass().add("modal");
                    d.show();
                    ButtonType b = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                d.getDialogPane().getButtonTypes().add(b);

                });

                return new TableCell<>() {

                    @Override
                    public void updateItem(Button item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(detailsButton);
                    }
                };
            }
        });
    }

    private void fillProductTable() {
        ObservableList<ProductOverviewDTO> productTableData = FXCollections.observableArrayList(products);
        productTable.setItems(productTableData);
        productTable.getSortOrder().add(nameColumn);
        productTable.sort();
    }

}