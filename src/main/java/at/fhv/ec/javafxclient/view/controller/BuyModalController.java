package at.fhv.ec.javafxclient.view.controller;

import at.fhv.ss22.ea.f.communication.dto.SoundCarrierDTO;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class BuyModalController {

    public TableView<SoundCarrierDTO> buyTableView;
    public TableColumn<SoundCarrierDTO, String> soundCarrierNameColumn;
    public TableColumn<SoundCarrierDTO, String> locationColumn;
    public TableColumn<SoundCarrierDTO, String> amountAvailableColumn;
    public TableColumn<SoundCarrierDTO, Float> pricePerCarrierColumn;
    public TableColumn<SoundCarrierDTO, String> selectAmountColumn;
    public TableColumn<SoundCarrierDTO, String> addToCartColumn;
    public TableColumn<SoundCarrierDTO, Button> orderColumn;

    public Label headingLabel;
    public Button backButton;
    public Label feedbackLabel;

}