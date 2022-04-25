package at.fhv.ec.javafxclient.view.controller;

import at.fhv.ec.javafxclient.Application;
import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.SessionManager;
import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ec.javafxclient.view.animator.TextOutput;
import at.fhv.ec.javafxclient.view.animator.TextAnimator;
import at.fhv.ss22.ea.f.communication.api.ProductSearchService;
import at.fhv.ss22.ea.f.communication.dto.ProductDetailsDTO;
import at.fhv.ss22.ea.f.communication.dto.ProductOverviewDTO;
import at.fhv.ss22.ea.f.communication.dto.SongDTO;
import at.fhv.ss22.ea.f.communication.dto.SoundCarrierDTO;
import at.fhv.ss22.ea.f.communication.exception.NoPermissionForOperation;
import at.fhv.ss22.ea.f.communication.exception.SessionExpired;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;



import java.util.ResourceBundle;
import java.util.UUID;

public class ShopController implements Initializable{
    public HBox ct;
    private ProductSearchService productSearchService;
    private static List<ProductOverviewDTO> products = new ArrayList<>();

    private TableView<ProductDetailsDTO> table;

    private TextAnimator textAnimator;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        String[] texts = {"artist", "album", "genre", "release",};

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

    @FXML
    private void onShoppingCartButtonClicked() throws IOException {
        SceneManager.getInstance().switchView("shoppingcart");
    }

    @FXML
    protected void onSearchButtonClicked() {
        try {
            String searchTerm = searchTextField.getText();
            products = productSearchService.fullTextSearch(SessionManager.getInstance().getSessionId(), searchTerm);
            searchTextField.setText(searchTerm);
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
                    Stage popup = new Stage();
                    VBox vbox = new VBox();
                    vbox.getStyleClass().add("modal");

                    vbox.setSpacing(20);

                    TableColumn<ProductDetailsDTO, String> nameColumn = new TableColumn<>("Name");
                    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

                    TableColumn<ProductDetailsDTO, String> artistColumn = new TableColumn<>("Artist");
                    artistColumn.setCellValueFactory(new PropertyValueFactory<>("artistName"));



                    ObservableList<ProductDetailsDTO> products = FXCollections.observableArrayList();
                    products.add(new ProductDetailsDTO.Builder()
                            .withName("Name1")
                            .withArtistName("Artist1")
                            .withId(new UUID(10,10))
                            .build());
                    products.add(new ProductDetailsDTO.Builder()
                            .withName("Name2")
                            .withArtistName("Artist2")
                            .withId(new UUID(10,10))
                            .build());
                    products.add(new ProductDetailsDTO.Builder()
                            .withName("Name3")
                            .withArtistName("Artist3")
                            .withId(new UUID(10,10))
                            .build());



                    table = new TableView<>();
                    table.setItems(products);
                    table.getColumns().addAll(nameColumn, artistColumn);

                    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


                    Button button = new Button("test");
                    button.getStyleClass().add("btn");

                    button.setOnAction((test) -> {
                        popup.close();
                        ct.setEffect(null);
                    });


                    HBox hBox = new HBox(button);
                    hBox.setAlignment(Pos.CENTER_RIGHT);


                    vbox.getChildren().addAll(table, hBox);
                    Scene scene = new Scene(vbox, 600, 300);

                    popup.setScene(scene);


                    for (int i = 0; i < Application.getStylesheets().size(); i++) {
                        scene.getStylesheets().add(Application.getStylesheets().get(i));
                    }

                    ColorAdjust colorAdjust = new ColorAdjust();
                    colorAdjust.setBrightness(-0.75);

                    ct.setEffect(colorAdjust);




                    scene.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
                        if (KeyCode.ESCAPE == event.getCode()) {
                            popup.close();
                            ct.setEffect(null);                        }
                    });


                    popup.setResizable(false);
                    popup.initModality(Modality.NONE);
                    popup.initStyle(StageStyle.UNDECORATED);
                    popup.initOwner(Application.getWindow());
                    popup.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                        if (! isNowFocused) {
                            popup.close();
                            ct.setEffect(null);
                        }
                    });
                    popup.show();
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