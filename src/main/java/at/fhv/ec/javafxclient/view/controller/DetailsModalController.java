package at.fhv.ec.javafxclient.view.controller;

import at.fhv.ss22.ea.f.communication.dto.SongDTO;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class DetailsModalController {

    public TableView<SongDTO> detailsTableView;
    public TableColumn<SongDTO, String> titleColumn;
    public TableColumn<SongDTO, String> durationColumn;

    public Label headingLabel;
    public Button backButton;
    public Label albumLabel;
    public Label artistLabel;
    public Label genreLabel;
    public Label releaseLabel;
    public Label durationLabel;
    public Label labelLabel;

}