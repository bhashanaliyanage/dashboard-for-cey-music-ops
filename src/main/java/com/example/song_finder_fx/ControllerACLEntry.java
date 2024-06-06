package com.example.song_finder_fx;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ControllerACLEntry {

    @FXML
    private CheckBox checkBox;

    @FXML
    private HBox hboxEntry;

    @FXML
    private ImageView image;

    @FXML
    private Label lblClaimType;

    @FXML
    private Label lblComposer;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblLyricist;

    @FXML
    private Label lblSongName;

    @FXML
    private Label lblSongNo;

    @FXML
    private Label lblSongNo1;

    @FXML
    private VBox vboxSongDetails;

    @FXML
    void onSongClick(MouseEvent event) {
        checkBox.setSelected(!checkBox.isSelected());
    }

}
