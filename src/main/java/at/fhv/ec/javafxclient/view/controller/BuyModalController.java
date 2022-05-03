package at.fhv.ec.javafxclient.view.controller;

import at.fhv.ss22.ea.f.communication.dto.SongDTO;
import at.fhv.ss22.ea.f.communication.dto.SoundCarrierDTO;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class BuyModalController {


    public Label headingLabel;
    public Button backButton;
    public TableView buyTableView;

    public TableColumn<SoundCarrierDTO, String> soundCarrierNameColumn;
    public TableColumn<SoundCarrierDTO, String> locationColumn;
    public TableColumn<SoundCarrierDTO, String> amountAvailableColumn;
    public TableColumn<SoundCarrierDTO, Float> pricePerCarrierColumn;
    public TableColumn selectAmountColumn;
    public TableColumn addToCartColumn;
    public TableColumn orderColumn;
    public Label feedbackLabel;
}