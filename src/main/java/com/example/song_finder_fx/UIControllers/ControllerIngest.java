package com.example.song_finder_fx.UIControllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class ControllerIngest {

    @FXML
    private Button btnOpen;

    @FXML
    private Label lblIngestID;

    @FXML
    private Label lblLocation;

    @FXML
    private Label lblPercentage;

    @FXML
    private Label lblProcess;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private VBox vboxTracks;

    @FXML
    void onGoBack(MouseEvent event) {

    }

}
