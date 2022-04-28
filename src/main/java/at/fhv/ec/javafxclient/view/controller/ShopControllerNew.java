package at.fhv.ec.javafxclient.view.controller;

import at.fhv.ec.javafxclient.Application;
import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ec.javafxclient.SessionManager;
import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ec.javafxclient.view.animator.TextAnimator;
import at.fhv.ec.javafxclient.view.animator.TextOutput;
import at.fhv.ec.javafxclient.view.utils.ShoppingCartEntry;
import at.fhv.ss22.ea.f.communication.api.ProductSearchService;
import at.fhv.ss22.ea.f.communication.dto.*;
import at.fhv.ss22.ea.f.communication.exception.NoPermissionForOperation;
import at.fhv.ss22.ea.f.communication.exception.SessionExpired;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;
import java.util.UUID;

public class ShopControllerNew implements Initializable {

    private TextAnimator textAnimator;

    private ProductSearchService productSearchService;

    //////////////////////////////////////////////////////////////////////////////////////////

    @FXML
    private TableView<ProductOverviewDTO> productTable;
    @FXML
    private TableColumn<ProductOverviewDTO, String> albumColumn;
    @FXML
    private TableColumn<ProductOverviewDTO, Button> detailsButtonColumn;
    @FXML
    private TableColumn<ProductOverviewDTO, Button> buyButtonColumn;

    @FXML
    private VBox rootContainer;
    @FXML
    private TextField searchTextField;
    @FXML
    private Label feedbackLabel;
    @FXML
    private Button shoppingCartButton;

    //////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String[] texts = {"album", "artist", "genre", "release"};
        TextOutput textOutput = text -> searchTextField.setPromptText(text);
        textAnimator = new TextAnimator(1000, 75, "Search for ", texts, textOutput, true);
        Thread th = new Thread(textAnimator);
        th.start();

        searchInProductTable("");
        checkIfShoppingCartIsFilled();
    }

    @FXML
    public void onHomeButtonClicked() {
        searchInProductTable("");
    }

    @FXML
    public void onSearchButtonClicked() {
        searchTextField.selectAll();
        searchInProductTable(searchTextField.getText());
    }

    @FXML
    public void onShoppingCartButtonClicked() {
        SceneManager.getInstance().switchView("shoppingcart");
    }

    private void searchInProductTable(String searchTerm) {
        try {
            productSearchService = RMIClient.getRmiClient().getRmiFactory().getProductSearchService();
            ObservableList<ProductOverviewDTO> productList = FXCollections.observableArrayList(productSearchService.fullTextSearch(SessionManager.getInstance().getSessionId(), searchTerm));
            productTable.setItems(productList);
            productTable.getSortOrder().add(albumColumn);
            productTable.sort();
            addButtonsToProductTable();
        } catch (RemoteException | SessionExpired | NoPermissionForOperation e) {
            e.printStackTrace();
        }
    }

    private void addButtonsToProductTable() {

        detailsButtonColumn.setCellFactory(new Callback<>() {
            public TableCell<ProductOverviewDTO, Button> call(TableColumn<ProductOverviewDTO, Button> param) {
                return new TableCell<>() {

                    public void updateItem(Button item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Button detailsButton = new Button("Details");
                            detailsButton.getStyleClass().add("btn");
                            detailsButton.setOnAction(action -> {

                                Stage modal = new Stage();
                                VBox modalRoot = new VBox();
                                HBox header = new HBox();
                                header.getStyleClass().add("modal-header");
                                HBox content = new HBox();
                                content.getStyleClass().add("modal-content");

                                TableView<SongDTO> songTable = new TableView<>();

                                Label heading = new Label("Details");
                                heading.getStyleClass().add("h2");

                                Label albumLabel = new Label("Album");
                                albumLabel.getStyleClass().add("p-1");
                                Label album = new Label();
                                album.getStyleClass().add("p-1-bold");

                                Label artistLabel = new Label("Artist");
                                artistLabel.getStyleClass().add("p-1");
                                Label artist = new Label();
                                artist.getStyleClass().add("p-1-bold");

                                Label genreLabel = new Label("Genre");
                                genreLabel.getStyleClass().add("p-1");
                                Label genre = new Label();
                                genre.getStyleClass().add("p-1-bold");

                                Label releaseLabel = new Label("Release");
                                releaseLabel.getStyleClass().add("p-1");
                                Label release = new Label();
                                release.getStyleClass().add("p-1-bold");

                                Label durationLabel = new Label("Duration");
                                durationLabel.getStyleClass().add("p-1");
                                Label duration = new Label();
                                duration.getStyleClass().add("p-1-bold");

                                Label labelLabel = new Label("Label");
                                labelLabel.getStyleClass().add("p-1");
                                Label label = new Label();
                                label.getStyleClass().add("p-1-bold");

                                Label storageLabel = new Label("Storage");
                                storageLabel.getStyleClass().add("p-1");
                                Label storage = new Label("FAKE-FAKE-FAKE");
                                storage.getStyleClass().add("p-1-bold");

                                try {
                                    productSearchService = RMIClient.getRmiClient().getRmiFactory().getProductSearchService();
                                    UUID productID = getTableView().getItems().get(getIndex()).getProductId();
                                    ProductDetailsDTO productDetails = productSearchService.productById(SessionManager.getInstance().getSessionId(), productID);

                                    TableColumn<SongDTO, String> titleColumn = new TableColumn<>("Title");
                                    titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
                                    titleColumn.setStyle("-fx-alignment: center-left;");

                                    TableColumn<SongDTO, String> durationColumn = new TableColumn<>("Duration");
                                    durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
                                    durationColumn.setStyle("-fx-alignment: center;");

                                    ObservableList<SongDTO> songList = FXCollections.observableArrayList(productDetails.getSongs());
                                    songTable.setItems(songList);
                                    songTable.getColumns().addAll(titleColumn, durationColumn);
                                    songTable.getSortOrder().add(titleColumn);
                                    songTable.sort();
                                    songTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

                                    heading.setText(productDetails.getName() + " - " + productDetails.getArtistName());
                                    album.setText(productDetails.getName());
                                    artist.setText(productDetails.getArtistName());
                                    genre.setText(productDetails.getGenre());
                                    release.setText(productDetails.getReleaseYear());
                                    duration.setText(productDetails.getDuration());
                                    label.setText(productDetails.getLabelName());

                                } catch (RemoteException | SessionExpired | NoPermissionForOperation e) {
                                    throw new RuntimeException(e);
                                }

                                VBox labels = new VBox(albumLabel, artistLabel, genreLabel, releaseLabel, durationLabel, labelLabel, storageLabel);
                                labels.setSpacing(19);
                                labels.setStyle("-fx-padding: 58 0 0 20;");

                                VBox infos = new VBox(album, artist, genre, release, duration, label, storage);
                                infos.setSpacing(19);
                                infos.setStyle("-fx-padding: 58 40 0 20;");

                                Button closeButton = new Button("close");
                                closeButton.getStyleClass().add("btn");
                                closeButton.setOnAction((test) -> {
                                    closeModal(modal);
                                });

                                HBox hBox1 = new HBox(heading);
                                hBox1.setAlignment(Pos.CENTER_LEFT);
                                hBox1.setPrefWidth(5000);

                                HBox hBox2 = new HBox(closeButton);
                                hBox2.setAlignment(Pos.CENTER_RIGHT);
                                hBox2.setPrefWidth(5000);

                                header.getChildren().addAll(hBox1, hBox2);

                                content.getChildren().addAll(labels, infos, songTable);
                                content.setHgrow(songTable, Priority.ALWAYS);

                                modalRoot.getChildren().addAll(header, content);
                                modalRoot.setVgrow(content, Priority.ALWAYS);

                                double width = 500 + Application.getWindow().getWidth()/10;
                                double height = 300 + Application.getWindow().getHeight()/5;
                                Scene scene = createModalScene(modal, modalRoot, width, height);
                                modal.setScene(scene);
                                setModalValues(modal, modalRoot);
                                showModal(modal);
                            });
                            setGraphic(detailsButton);
                        }
                    }
                };
            }
        });

        //////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////

        buyButtonColumn.setCellFactory(new Callback<>() {
            public TableCell<ProductOverviewDTO, Button> call(TableColumn<ProductOverviewDTO, Button> param) {
                return new TableCell<>() {

                    public void updateItem(Button item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Button buyButton = new Button("Buy");
                            buyButton.getStyleClass().add("btn-success");
                            buyButton.setOnAction(action -> {

                                Stage modal = new Stage();
                                VBox modalRoot = new VBox();
                                HBox header = new HBox();
                                header.getStyleClass().add("modal-header");
                                VBox content = new VBox();
                                content.getStyleClass().add("modal-content");

                                TableView<SoundCarrierDTO> priceTable = new TableView<>();

                                Label heading = new Label("Details");
                                heading.getStyleClass().add("h2");

                                try {
                                    productSearchService = RMIClient.getRmiClient().getRmiFactory().getProductSearchService();
                                    UUID productID = getTableView().getItems().get(getIndex()).getProductId();
                                    ProductDetailsDTO productDetails = productSearchService.productById(SessionManager.getInstance().getSessionId(), productID);

                                    TableColumn<SoundCarrierDTO, String> soundCarrierNameColumn = new TableColumn<>("Type");
                                    soundCarrierNameColumn.setCellValueFactory(new PropertyValueFactory<>("soundCarrierName"));

                                    TableColumn<SoundCarrierDTO, String> amountAvailableColumn = new TableColumn<>("Available");
                                    amountAvailableColumn.setCellValueFactory(new PropertyValueFactory<>("amountAvailable"));
                                    amountAvailableColumn.setStyle("-fx-alignment: center;");

                                    TableColumn<SoundCarrierDTO, Float> pricePerCarrierColumn = new TableColumn<>("Price");
                                    pricePerCarrierColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerCarrier"));
                                    pricePerCarrierColumn.setStyle("-fx-alignment: center;");

                                    TableColumn<SoundCarrierDTO, String> selectAmountColumn = new TableColumn<>();
                                    selectAmountColumn.setMinWidth(80);
                                    selectAmountColumn.setMaxWidth(80);
                                    selectAmountColumn.setStyle("-fx-alignment: center-right;");

                                    TableColumn<SoundCarrierDTO, Button> addToCartColumn = new TableColumn<>();
                                    addToCartColumn.setMinWidth(110);
                                    addToCartColumn.setMaxWidth(110);
                                    addToCartColumn.setStyle("-fx-alignment: center-right;");

                                    TableColumn<SoundCarrierDTO, Button> orderColumn = new TableColumn<>();
                                    orderColumn.setStyle("-fx-alignment: center-right;");
                                    orderColumn.setMinWidth(200);
                                    orderColumn.setMaxWidth(200);

                                    ObservableList<SoundCarrierDTO> priceList = FXCollections.observableArrayList(productDetails.getSoundCarriers());
                                    priceTable.setItems(priceList);
                                    priceTable.getColumns().addAll(soundCarrierNameColumn, amountAvailableColumn, pricePerCarrierColumn, selectAmountColumn, addToCartColumn, orderColumn);
                                    priceTable.getSortOrder().add(soundCarrierNameColumn);
                                    priceTable.sort();
                                    priceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

                                    heading.setText(productDetails.getName() + " - " + productDetails.getArtistName());

                                    orderColumn.setCellFactory(new Callback<>() {
                                           @Override
                                           public TableCell<SoundCarrierDTO, Button> call(TableColumn<SoundCarrierDTO, Button> param) {
                                               return new TableCell<>() {
                                                   private final Pane wrappingPane = new Pane();
                                                   private Button startButton = new Button("Order");
                                                   private HBox replacement = new HBox();
                                                   private Spinner amountInput = new Spinner();
                                                   private Button orderButton = new Button("->");
                                                   private Button quitButton = new Button("X");

                                                   @Override
                                                   protected void updateItem(Button item, boolean empty) {
                                                       super.updateItem(item, empty);
                                                       if (empty) {
                                                           setGraphic(null);
                                                       } else {
                                                           wrappingPane.getChildren().add(startButton);
                                                           quitButton.setOnAction(quitEvent -> {
                                                               wrappingPane.getChildren().remove(replacement);
                                                               wrappingPane.getChildren().add(startButton);
                                                           });
                                                           startButton.getStyleClass().add("btn");
                                                           quitButton.getStyleClass().add("btn");
                                                           replacement.getChildren().add(quitButton);
                                                           replacement.getChildren().add(amountInput);
                                                           replacement.getChildren().add(orderButton);
                                                           orderButton.getStyleClass().add("btn-success");
                                                           orderButton.setOnAction(orderEvent -> {
                                                               int amount = (Integer) amountInput.getValue();
                                                               if (amount > 0) {
                                                                   wrappingPane.getChildren().remove(replacement);
                                                                   wrappingPane.getChildren().add(startButton);

                                                                   SoundCarrierOrderDTO orderDTO = SoundCarrierOrderDTO.builder()
                                                                           .withOrderId(UUID.randomUUID())
                                                                           .withCarrierId(getTableView().getItems().get(getIndex()).getSoundCarrierId())
                                                                           .withAmount(amount)
                                                                           .build();

                                                                   boolean orderingSuccess = false;
                                                                   try {
                                                                       orderingSuccess = RMIClient.getRmiClient().getRmiFactory().getOrderingService()
                                                                               .placeOrder(SessionManager.getInstance().getSessionId(), orderDTO);

                                                                   } catch (RemoteException e) {
                                                                       //TODO error handling,
                                                                       e.printStackTrace();
                                                                   } catch (SessionExpired e) {
                                                                       e.printStackTrace();
                                                                   } catch (NoPermissionForOperation e) {
                                                                       e.printStackTrace();
                                                                   }
                                                                   displayOrderingSuccess(orderingSuccess);
                                                               }
                                                           });

                                                           startButton.setOnAction(event -> {
                                                               amountInput.setMaxWidth(100);
                                                               amountInput.setValueFactory(
                                                                       new SpinnerValueFactory.IntegerSpinnerValueFactory(
                                                                               0,
                                                                               100
                                                                       )
                                                               );
                                                               wrappingPane.getChildren().remove(startButton);
                                                               wrappingPane.getChildren().add(replacement);
                                                           });
                                                           setGraphic(wrappingPane);
                                                       }
                                                   }
                                               };
                                           }
                                       });

                                    pricePerCarrierColumn.setCellFactory(new Callback<>() {
                                        @Override
                                        public TableCell<SoundCarrierDTO, Float> call(TableColumn<SoundCarrierDTO, Float> param) {
                                            return new TableCell<>() {
                                                @Override
                                                protected void updateItem(Float pricePerCarrier, boolean empty) {
                                                    super.updateItem(pricePerCarrier, empty);
                                                    if (empty || pricePerCarrier == null) {
                                                        setText("");
                                                    } else {
                                                        String pricePerCarrierStr = pricePerCarrier + "€";
                                                        setText(pricePerCarrierStr);
                                                    }
                                                }
                                            };
                                        }
                                    });

                                    Callback<TableColumn<SoundCarrierDTO, String>, TableCell<SoundCarrierDTO, String>> spinnerCellFactory = new Callback<>() {
                                        @Override
                                        public TableCell<SoundCarrierDTO, String> call(final TableColumn<SoundCarrierDTO, String> param) {
                                            return new TableCell<>() {
                                                @Override
                                                public void updateItem(String item, boolean empty) {
                                                    super.updateItem(item, empty);
                                                    if (empty) {
                                                        setGraphic(null);
                                                    } else {
                                                        Spinner<Integer> selectAmountSpinner = new Spinner<>();
                                                        selectAmountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, getTableView().getItems().get(getIndex()).getAmountAvailable(), 0));
                                                        selectAmountSpinner.setId(getTableView().getItems().get(getIndex()).getSoundCarrierName());
                                                        setGraphic(selectAmountSpinner);
                                                    }
                                                }
                                            };
                                        }
                                    };
                                    selectAmountColumn.setCellFactory(spinnerCellFactory);

                                    addToCartColumn.setCellFactory(new Callback<>() {
                                        @Override
                                        public TableCell<SoundCarrierDTO, Button> call(TableColumn<SoundCarrierDTO, Button> param) {
                                            return new TableCell<>() {
                                                @Override
                                                public void updateItem(Button item, boolean empty) {
                                                    super.updateItem(item, empty);
                                                    if (empty) {
                                                        setGraphic(null);
                                                    } else {
                                                        Button addToCartButton = new Button("Add to cart");
                                                        addToCartButton.getStyleClass().add("btn-success");
                                                        addToCartButton.setOnAction(event -> {
                                                            Spinner<Integer> selectedAmountSpinner = (Spinner<Integer>) priceTable.lookup("#" + getTableView().getItems().get(getIndex()).getSoundCarrierName());
                                                            addProductToCart(getTableView().getItems().get(getIndex()).getSoundCarrierName(), selectedAmountSpinner.getValue(), productDetails);
                                                        });
                                                        setGraphic(addToCartButton);
                                                    }
                                                }
                                            };
                                        }
                                    });

                                } catch (RemoteException | NoPermissionForOperation | SessionExpired e) {
                                    throw new RuntimeException(e);
                                }

                                Button closeButton = new Button("close");
                                closeButton.getStyleClass().add("btn");
                                closeButton.setOnAction((test) -> {
                                    closeModal(modal);
                                });

                                HBox hBox1 = new HBox(heading);
                                hBox1.setAlignment(Pos.CENTER_LEFT);
                                hBox1.setPrefWidth(5000);

                                HBox hBox2 = new HBox(closeButton);
                                hBox2.setAlignment(Pos.CENTER_RIGHT);
                                hBox2.setPrefWidth(5000);

                                header.getChildren().addAll(hBox1, hBox2);

                                feedbackLabel = new Label();
                                feedbackLabel.getStyleClass().add("h2");
                                content.setSpacing(15);
                                content.getChildren().addAll(priceTable, feedbackLabel);
                                modalRoot.getChildren().addAll(header, content);

                                double width = 600 + Application.getWindow().getWidth()/5;
                                double height = 310;
                                Scene scene = createModalScene(modal, modalRoot, width, height);
                                modal.setScene(scene);
                                setModalValues(modal, modalRoot);
                                showModal(modal);
                            });
                            setGraphic(buyButton);
                        }
                    }
                };
            }
        });
    }

    private Stage setModalValues(Stage modal, Pane modalRoot) {
        modal.setResizable(false);
        modal.initModality(Modality.NONE);
        modal.initStyle(StageStyle.UNDECORATED);
        modal.initOwner(Application.getWindow());
        modal.setX(Application.getWindow().getX() + Application.getWindow().getWidth()/2 - modalRoot.getWidth()/2 + 225/2);
        modal.setY(Application.getWindow().getY() + Application.getWindow().getHeight()/2 - modalRoot.getHeight()/2);

        modal.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                closeModal(modal);
            }
        });

        return modal;
    }

    private Scene createModalScene(Stage modal, Pane modalRoot, Double width, Double height) {

        if (width > Application.getWindow().getWidth() - 20 - 255) {
            width = Application.getWindow().getWidth() - 20 - 255;
        }

        if (height > Application.getWindow().getHeight() - 40) {
            height = Application.getWindow().getHeight() - 40;
        }

        Scene scene = new Scene(modalRoot, width, height);

        for (int i = 0; i < Application.getStylesheets().size(); i++) {
            scene.getStylesheets().add(Application.getStylesheets().get(i));
        }

        scene.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.ESCAPE == event.getCode()) {
                closeModal(modal);
            }
        });

        return scene;
    }

    private void showModal(Stage modal) {
        modal.show();
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-0.75);
        rootContainer.setEffect(colorAdjust);
    }

    private void closeModal(Stage modal) {
        modal.close();
        rootContainer.setEffect(null);
    }

    private void checkIfShoppingCartIsFilled () {
        int items = ShoppingCartController.shoppingCart.size();

        if (items > 0) {
            shoppingCartButton.getStyleClass().add("btn-alert");
            shoppingCartButton.setText("Shopping Cart (" + items + ")");
        } else {
            shoppingCartButton.getStyleClass().remove("btn-alert");
            shoppingCartButton.setText("Shopping Cart");
        }
    }

    private void addProductToCart(String soundCarrierName, int selectedAmount, ProductDetailsDTO productDetails) {
        SoundCarrierDTO selectedSoundCarrier = productDetails.getSoundCarriers().stream()
                .filter(soundCarrierDTO -> soundCarrierDTO.getSoundCarrierName().equals(soundCarrierName))
                .findFirst().orElse(null);

        if(selectedSoundCarrier != null && selectedAmount > 0) {
            float totalPrice = selectedAmount * selectedSoundCarrier.getPricePerCarrier();
            ShoppingCartEntry cartEntry = new ShoppingCartEntry(
                    productDetails.getProductId(),
                    productDetails.getName(),
                    productDetails.getArtistName(),
                    selectedSoundCarrier.getSoundCarrierId(),
                    selectedSoundCarrier.getSoundCarrierName(),
                    selectedSoundCarrier.getPricePerCarrier(),
                    selectedAmount,
                    totalPrice,
                    selectedSoundCarrier.getAmountAvailable()
            );

            ShoppingCartController.shoppingCart.add(cartEntry);

            feedbackLabel.getStyleClass().remove("alert");
            feedbackLabel.setText("Success - Your article(s) are now in the shopping cart!");
        } else {
            feedbackLabel.getStyleClass().add("alert");
            feedbackLabel.setText("Fail - You have to choose at least one item!");
        }

        checkIfShoppingCartIsFilled();
    }

    public void displayOrderingSuccess(boolean success) {
        if (success) {
            feedbackLabel.getStyleClass().remove("alert");
            feedbackLabel.setText("Success - Placed Order");
        } else {
            feedbackLabel.getStyleClass().add("alert");
            feedbackLabel.setText("Failed - while placing order");
        }
    }
}