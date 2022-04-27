package at.fhv.ec.javafxclient.view.controller;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.SessionManager;
import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ec.javafxclient.view.animator.TextOutput;
import at.fhv.ec.javafxclient.view.animator.TextAnimator;
import at.fhv.ss22.ea.f.communication.api.ProductSearchService;
import at.fhv.ss22.ea.f.communication.dto.ProductDetailsDTO;
import at.fhv.ss22.ea.f.communication.dto.ProductOverviewDTO;
import at.fhv.ss22.ea.f.communication.dto.SongDTO;
import at.fhv.ss22.ea.f.communication.exception.NoPermissionForOperation;
import at.fhv.ss22.ea.f.communication.exception.SessionExpired;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class ShopController implements Initializable{
    public HBox rootContainer;

    private ProductSearchService productSearchService;
    private static List<ProductOverviewDTO> products = new ArrayList<>();
    private static ProductDetailsDTO productDetails;
    public static UUID productId;

    @FXML
    private TableView<SongDTO> table;

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



    private void fillProductTable() {
        ObservableList<ProductOverviewDTO> productTableData = FXCollections.observableArrayList(products);
        productTable.setItems(productTableData);
        productTable.getSortOrder().add(nameColumn);
        productTable.sort();
    }


    private void createProductTable() {

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
                                    ProductDetailsController.productId = getTableView().getItems().get(getIndex()).getProductId();
                                    SceneManager.getInstance().switchView("productdetails");
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

                return new TableCell<>() {

                    @Override
                    public void updateItem(Button item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {


                            final Button detailsButton = new Button("Details");
                            detailsButton.getStyleClass().add("btn");
                            detailsButton.setOnAction(event -> {






                            });
                            setGraphic(detailsButton);
                            setText(null);
                        }
                    }
                };
            }



//            @Override
//            public TableCell<ProductOverviewDTO, Button> call(TableColumn<ProductOverviewDTO, Button> param) {
//
//                Button detailsButton = new Button("Details");
//                detailsButton.getStyleClass().add("btn");
//
//                detailsButton.setOnAction(e -> {
//
//
//                    Stage popup = new Stage();
//
//                    VBox rootElement = new VBox();
//                    rootElement.getStyleClass().add("modal");
//                    rootElement.setSpacing(20);
//
//
//                    TableColumn<SongDTO, String> titleColumn = new TableColumn<>("Title");
//                    titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
//
//                    ProductDetailsController.productId = getTableView().getItems().get(getIndex()).getProductId();
//
//
//                    try {
//
//                        productDetails = productSearchService.productById(SessionManager.getInstance().getSessionId(), productId);
//                    } catch (RemoteException | SessionExpired | NoPermissionForOperation ex) {
//                        throw new RuntimeException(ex);
//                    }
//
//
//                    ObservableList<SongDTO> songsTableData = FXCollections.observableArrayList(productDetails.getSongs());
//                    songsTable.setItems(songsTableData);
//
//
//                    songsTable = new TableView<>();
//                    songsTable.setItems(songsTableData);
//
//
//
//                    TableColumn<ProductDetailsDTO, String> nameColumn = new TableColumn<>("Name");
//                    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
//
//                    TableColumn<ProductDetailsDTO, String> artistColumn = new TableColumn<>("Artist");
//                    artistColumn.setCellValueFactory(new PropertyValueFactory<>("artistName"));
//
//                    ObservableList<ProductDetailsDTO> products = FXCollections.observableArrayList();
//                    products.add(new ProductDetailsDTO.Builder()
//                            .withName("Name1")
//                            .withArtistName("Artist1")
//                            .withId(new UUID(10,10))
//                            .build());
//                    products.add(new ProductDetailsDTO.Builder()
//                            .withName("Name2")
//                            .withArtistName("Artist2")
//                            .withId(new UUID(10,10))
//                            .build());
//                    products.add(new ProductDetailsDTO.Builder()
//                            .withName("Name3")
//                            .withArtistName("Artist3")
//                            .withId(new UUID(10,10))
//                            .build());
//
//                    table = new TableView<>();
//                    table.setItems(products);
//                    table.getColumns().addAll(nameColumn, artistColumn);
//                    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//
//
//
//                    Button button = new Button("test");
//                    button.getStyleClass().add("btn");
//                    button.setOnAction((test) -> {
//                        popup.close();
//                        ct.setEffect(null);
//                    });
//
//                    HBox hBox = new HBox(button);
//                    hBox.setAlignment(Pos.CENTER_RIGHT);
//
//
//
//                    rootElement.getChildren().addAll(table, songsTable, hBox);
//                    Scene scene = new Scene(rootElement, 600, 300);
//                    popup.setScene(scene);
//                    popup.setResizable(false);
//                    popup.initModality(Modality.NONE);
//                    popup.initStyle(StageStyle.UNDECORATED);
//                    popup.initOwner(Application.getWindow());
//
//                    popup.setX(Application.getWindow().getX() + Application.getWindow().getWidth()/2 - rootElement.getWidth()/2 + 225/2);
//                    popup.setY(Application.getWindow().getY() + Application.getWindow().getHeight()/2 - rootElement.getHeight()/2 + 100/2);
//
//                    System.out.println("Get X: " + Application.getWindow().getX());
//                    System.out.println("Get Y: " + Application.getWindow().getY());
//
//                    System.out.println("Height: " + Application.getWindow().getHeight());
//                    System.out.println("Width: " + Application.getWindow().getWidth());
//
//                    System.out.println("Height P: " + popup.getHeight());
//                    System.out.println("Width: P: " + popup.getWidth());
//
//
//                    System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
//
//
//
//                    for (int i = 0; i < Application.getStylesheets().size(); i++) {
//                        scene.getStylesheets().add(Application.getStylesheets().get(i));
//                    }
//
//                    ColorAdjust colorAdjust = new ColorAdjust();
//                    colorAdjust.setBrightness(-0.75);
//                    ct.setEffect(colorAdjust);
//
//                    scene.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
//                        if (KeyCode.ESCAPE == event.getCode()) {
//                            popup.close();
//                            ct.setEffect(null);                        }
//                    });
//
//                    popup.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
//                        if (! isNowFocused) {
//                            popup.close();
//                            ct.setEffect(null);
//                        }
//                    });
//
//
//                    popup.show();
//
//
//
//                });
//
//                return new TableCell<>() {
//
//                    @Override
//                    public void updateItem(Button item, boolean empty) {
//                        super.updateItem(item, empty);
//                        setGraphic(detailsButton);
//                    }
//                };
//            }
        });
    }


}