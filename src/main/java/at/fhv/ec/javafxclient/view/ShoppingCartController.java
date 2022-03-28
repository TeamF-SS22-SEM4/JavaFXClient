package at.fhv.ec.javafxclient.view;

import at.fhv.ec.javafxclient.SceneManager;
import at.fhv.ss22.ea.f.communication.dto.ShoppingCartProductDTO;
import at.fhv.ss22.ea.f.communication.dto.SongDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCartController {
    public static List<ShoppingCartProductDTO> shoppingCart = new ArrayList<>();
    private static float totalPrice;

    @FXML
    public TableView<ShoppingCartProductDTO> shoppingCartTable;

    @FXML
    public Label totalPriceLabel;

    @FXML
    public void initialize() {
        ObservableList<ShoppingCartProductDTO> shoppingCartTableData = FXCollections.observableArrayList(shoppingCart);
        shoppingCartTable.setItems(shoppingCartTableData);

        totalPrice = 0;
        shoppingCart.forEach(item -> {
            totalPrice += item.getTotalProductPrice();
        });

        totalPriceLabel.setText(totalPrice + "€");
    }

    @FXML
    protected void onBackButtonClicked() {
        try {
            SceneManager.getInstance().switchView("views/search-view-copy.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
