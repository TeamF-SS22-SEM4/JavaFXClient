package at.fhv.ec.javafxclient;

import at.fhv.ec.javafxclient.communication.RMIClient;
import at.fhv.ec.javafxclient.model.ShoppingCartEntry;
import at.fhv.ec.javafxclient.view.animator.TextAnimator;
import at.fhv.ec.javafxclient.view.animator.TextOutput;
import at.fhv.ec.javafxclient.view.controller.BuyModalController;
import at.fhv.ec.javafxclient.view.controller.DetailsModalController;
import at.fhv.ec.javafxclient.view.controller.ShoppingCartController;
import at.fhv.ss22.ea.f.communication.api.ProductSearchService;
import at.fhv.ss22.ea.f.communication.dto.*;
import at.fhv.ss22.ea.f.communication.exception.NoPermissionForOperation;
import at.fhv.ss22.ea.f.communication.exception.SessionExpired;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;
import java.util.UUID;

public class ShopController implements Initializable {

    private TextAnimator textAnimator;
    private ProductSearchService productSearchService;
    private BuyModalController buyController;
    private DetailsModalController detailsController;

    @FXML
    private TableView<ProductOverviewDTO> productTable;
    @FXML
    private TableColumn<ProductOverviewDTO, String> albumColumn;
    @FXML
    private TableColumn<ProductOverviewDTO, Float> priceColumn;
    @FXML
    private TableColumn<ProductOverviewDTO, Button> detailsButtonColumn;
    @FXML
    private TableColumn<ProductOverviewDTO, Button> buyButtonColumn;
    @FXML
    private VBox rootContainer;
    @FXML
    private TextField searchTextField;
    @FXML
    private Button shoppingCartButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String[] texts = {"album", "artist", "genre", "release"};
        TextOutput textOutput = text -> searchTextField.setPromptText(text);
        textAnimator = new TextAnimator(1000, 75, "Search for ", texts, textOutput, true);
        Thread th = new Thread(textAnimator);
        th.start();

        searchProduct("");
        checkIfShoppingCartIsFilled();
    }

    @FXML
    public void onHomeButtonClicked() {
        searchProduct("");
    }

    @FXML
    public void onSearchButtonClicked() {
        searchTextField.selectAll();
        searchProduct(searchTextField.getText());
    }

    @FXML
    public void onShoppingCartButtonClicked() {
        SceneManager.getInstance().switchView(SceneManager.VIEW_SHOPPING_CART);
    }

    private void searchProduct(String searchTerm) {
        try {
            productSearchService = RMIClient.getRmiClient().getRmiFactory().getProductSearchService();
            ObservableList<ProductOverviewDTO> productList = FXCollections.observableArrayList(productSearchService.fullTextSearch(SessionManager.getInstance().getSessionId(), searchTerm));
            productTable.setItems(productList);
            productTable.getSortOrder().add(albumColumn);
            productTable.sort();
            formatTable();
        } catch (RemoteException | SessionExpired | NoPermissionForOperation e) {
            e.printStackTrace();
        }
    }

    private void formatTable() {
        priceColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<ProductOverviewDTO, Float> call(TableColumn<ProductOverviewDTO, Float> param) {
                return new TableCell<>() {

                    @Override
                    protected void updateItem(Float minPrice, boolean empty) {
                        super.updateItem(minPrice, empty);
                        if (empty || minPrice == null) {
                            setText("");
                        } else {
                            String minPriceStr = minPrice + "€";
                            setText(minPriceStr);
                        }
                    }
                };
            }
        });

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

                                try {
                                    Stage modalStage = new Stage();
                                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("views/elements/detailsModal.fxml"));
                                    Pane modalRoot = fxmlLoader.load();

                                    productSearchService = RMIClient.getRmiClient().getRmiFactory().getProductSearchService();
                                    UUID productID = getTableView().getItems().get(getIndex()).getProductId();
                                    ProductDetailsDTO productDetails = productSearchService.productById(SessionManager.getInstance().getSessionId(), productID);
                                    ObservableList<SongDTO> songList = FXCollections.observableArrayList(productDetails.getSongs());

                                    detailsController = fxmlLoader.getController();
                                    detailsController.detailsTableView.setItems(songList);
                                    detailsController.detailsTableView.getSortOrder().add(detailsController.titleColumn);
                                    detailsController.detailsTableView.sort();
                                    detailsController.headingLabel.setText(productDetails.getName() + " - " + productDetails.getArtistName());
                                    detailsController.albumLabel.setText(productDetails.getName());
                                    detailsController.artistLabel.setText(productDetails.getArtistName());
                                    detailsController.genreLabel.setText(productDetails.getGenre());
                                    detailsController.releaseLabel.setText(productDetails.getReleaseYear());
                                    detailsController.durationLabel.setText(productDetails.getDuration());
                                    detailsController.labelLabel.setText(productDetails.getLabelName());
                                    detailsController.backButton.setOnAction((action1234) -> closeModal(modalStage));

                                    double width = 650 + Application.getWindow().getWidth()/5;
                                    double height = 250 + Application.getWindow().getHeight()/2.5;

                                    Scene scene = createModalScene(modalStage, modalRoot, width, height);
                                    modalStage.setScene(scene);
                                    setModalValues(modalStage, modalRoot);
                                    showModal(modalStage);

                                } catch (IOException | NoPermissionForOperation | SessionExpired e) {
                                    throw new RuntimeException(e);
                                }
                            });
                            setGraphic(detailsButton);
                        }
                    }
                };
            }
        });

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

                                try {
                                    Stage modalStage = new Stage();
                                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("views/elements/buyModal.fxml"));
                                    Pane modalRoot = fxmlLoader.load();

                                    productSearchService = RMIClient.getRmiClient().getRmiFactory().getProductSearchService();
                                    UUID productID = getTableView().getItems().get(getIndex()).getProductId();
                                    ProductDetailsDTO productDetails = productSearchService.productById(SessionManager.getInstance().getSessionId(), productID);
                                    ObservableList<SoundCarrierDTO> priceList = FXCollections.observableArrayList(productDetails.getSoundCarriers());

                                    buyController = fxmlLoader.getController();
                                    buyController.buyTableView.setItems(priceList);
                                    buyController.buyTableView.getSortOrder().add(buyController.soundCarrierNameColumn);
                                    buyController.buyTableView.sort();
                                    buyController.headingLabel.setText(productDetails.getName() + " - " + productDetails.getArtistName());
                                    buyController.backButton.setOnAction((action1234) -> closeModal(modalStage));

                                    buyController.pricePerCarrierColumn.setCellFactory(new Callback<>() {
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
                                    buyController.selectAmountColumn.setCellFactory(spinnerCellFactory);

                                    Callback<TableColumn<SoundCarrierDTO, String>, TableCell<SoundCarrierDTO, String>> addToCartCellFactory = new Callback<>() {
                                        @Override
                                        public TableCell<SoundCarrierDTO, String> call(final TableColumn<SoundCarrierDTO, String> param) {
                                            return new TableCell<>() {

                                                @Override
                                                public void updateItem(String item, boolean empty) {
                                                    super.updateItem(item, empty);
                                                    if (empty) {
                                                        setGraphic(null);
                                                    } else {
                                                        Button addToCartButton = new Button("Add to cart");
                                                        addToCartButton.getStyleClass().add("btn-success");
                                                        addToCartButton.setOnAction(event -> {
                                                            Spinner<Integer> selectedAmountSpinner = (Spinner<Integer>) buyController.buyTableView.lookup("#" + getTableView().getItems().get(getIndex()).getSoundCarrierName());
                                                            addProductToCart(getTableView().getItems().get(getIndex()).getSoundCarrierName(), selectedAmountSpinner.getValue(), productDetails);
                                                            selectedAmountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, getTableView().getItems().get(getIndex()).getAmountAvailable(), 0));
                                                        });
                                                        setGraphic(addToCartButton);
                                                    }
                                                }
                                            };
                                        }
                                    };
                                    buyController.addToCartColumn.setCellFactory(addToCartCellFactory);

                                    Callback<TableColumn<SoundCarrierDTO, Button>, TableCell<SoundCarrierDTO, Button>> orderCellFactory = new Callback<>() {
                                        @Override
                                        public TableCell<SoundCarrierDTO, Button> call(final TableColumn<SoundCarrierDTO, Button> param) {
                                            return new TableCell<>() {

                                                @Override
                                                public void updateItem(Button item, boolean empty) {
                                                    super.updateItem(item, empty);
                                                    if (empty) {
                                                        setGraphic(null);
                                                    } else {
                                                        HBox wrappingBox = new HBox();
                                                        wrappingBox.setAlignment(Pos.CENTER);
                                                        wrappingBox.setSpacing(5);

                                                        Spinner amountSpinner = new Spinner();
                                                        Button startOrderButton = new Button("Order");
                                                        startOrderButton.getStyleClass().add("btn");
                                                        Button placeOrderButton = new Button("✔");
                                                        placeOrderButton.getStyleClass().add("btn-success");
                                                        Button quitOrderButton = new Button("❌");
                                                        quitOrderButton.getStyleClass().add("btn");

                                                        wrappingBox.getChildren().add(startOrderButton);

                                                        startOrderButton.setOnAction(startEvent -> {
                                                            amountSpinner.setMaxWidth(80);
                                                            amountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100));
                                                            wrappingBox.getChildren().remove(startOrderButton);
                                                            wrappingBox.getChildren().addAll(quitOrderButton, amountSpinner, placeOrderButton);
                                                        });

                                                        placeOrderButton.setOnAction(orderEvent -> {
                                                            int amount = (Integer) amountSpinner.getValue();
                                                            if (amount > 0) {
                                                                wrappingBox.getChildren().removeAll(quitOrderButton, amountSpinner, placeOrderButton);
                                                                wrappingBox.getChildren().add(startOrderButton);
                                                                SoundCarrierOrderDTO orderDTO = SoundCarrierOrderDTO.builder()
                                                                        .withOrderId(UUID.randomUUID())
                                                                        .withCarrierId(getTableView().getItems().get(getIndex()).getSoundCarrierId())
                                                                        .withAmount(amount)
                                                                        .build();

                                                                boolean orderingSuccess = false;
                                                                try {
                                                                    orderingSuccess = RMIClient.getRmiClient().getRmiFactory().getOrderingService()
                                                                            .placeOrder(SessionManager.getInstance().getSessionId(), orderDTO);

                                                                } catch (RemoteException | SessionExpired | NoPermissionForOperation e) {
                                                                    e.printStackTrace();
                                                                }
                                                                displayOrderingSuccess(orderingSuccess);
                                                            }
                                                        });

                                                        quitOrderButton.setOnAction(quitEvent -> {
                                                            wrappingBox.getChildren().removeAll(quitOrderButton, amountSpinner, placeOrderButton);
                                                            wrappingBox.getChildren().add(startOrderButton);
                                                        });
                                                        setGraphic(wrappingBox);
                                                    }
                                                }
                                            };
                                        }
                                    };
                                    buyController.orderColumn.setCellFactory(orderCellFactory);

                                    double width = 650 + Application.getWindow().getWidth()/5;
                                    double height = 310;

                                    Scene scene = createModalScene(modalStage, modalRoot, width, height);
                                    modalStage.setScene(scene);
                                    setModalValues(modalStage, modalRoot);
                                    showModal(modalStage);

                                } catch (IOException | NoPermissionForOperation | SessionExpired e) {
                                    throw new RuntimeException(e);
                                }
                            });
                            setGraphic(buyButton);
                        }
                    }
                };
            }
        });
    }

    private void checkIfShoppingCartIsFilled () {
        int items = ShoppingCartController.shoppingCart.size();

        if (items > 0) {
            shoppingCartButton.getStyleClass().add("btn-notify");
            shoppingCartButton.setText("Shopping Cart (" + items + ")");
        } else {
            shoppingCartButton.getStyleClass().remove("btn-notify");
            shoppingCartButton.setText("Shopping Cart");
        }
    }

    private void addProductToCart(String soundCarrierName, int selectedAmount, ProductDetailsDTO productDetails) {
        buyController.feedbackLabel.getStyleClass().remove("alert");

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

            if(!ShoppingCartController.shoppingCart.contains(cartEntry)) {
                ShoppingCartController.shoppingCart.add(cartEntry);
                buyController.feedbackLabel.setText("Success - Your article(s) are now in the shopping cart!");
            } else {
                buyController.feedbackLabel.getStyleClass().add("alert");
                buyController.feedbackLabel.setText("Failed - This product is already in the cart!");
            }
        } else {
            buyController.feedbackLabel.getStyleClass().add("alert");
            buyController.feedbackLabel.setText("Failed - You have to choose at least one item!");
        }

        checkIfShoppingCartIsFilled();
    }


    private Stage setModalValues(Stage modal, Pane modalRoot) {
        modal.setResizable(false);
        modal.initModality(Modality.NONE);
        modal.initStyle(StageStyle.UNDECORATED);
        modal.initOwner(Application.getWindow());
        modal.setX(Application.getWindow().getX() + Application.getWindow().getWidth()/2 - modalRoot.getWidth()/2 + 225/2);
        modal.setY(Application.getWindow().getY() + Application.getWindow().getHeight()/2 - modalRoot.getHeight()/2 + 20/2);

        modal.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                closeModal(modal);
            }
        });

        return modal;
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

    private Scene createModalScene(Stage modal, Pane modalRoot, Double width, Double height) {

        if (width > Application.getWindow().getWidth() - 20 - 255) {
            width = Application.getWindow().getWidth() - 20 - 255;      //sidebar offset
        }

        if (height > Application.getWindow().getHeight() - 20 - 80) {
            height = Application.getWindow().getHeight() - 20 - 80;     //taskbar offset
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

    private void displayOrderingSuccess(boolean success) {
        buyController.feedbackLabel.getStyleClass().remove("alert");

        if (success) {
            buyController.feedbackLabel.setText("Success - Placed Order!");
        } else {
            buyController.feedbackLabel.getStyleClass().add("alert");
            buyController.feedbackLabel.setText("Failed - while placing order!");
        }
    }
}