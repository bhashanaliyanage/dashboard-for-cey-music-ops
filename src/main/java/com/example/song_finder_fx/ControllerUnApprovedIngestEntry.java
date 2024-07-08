package com.example.song_finder_fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class ControllerUnApprovedIngestEntry {

    @FXML
    private Label lblAssetCount;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblImportedBy;

    @FXML
    private Label lblSongName;

    @FXML
    void onApprove(ActionEvent event) {
        System.out.println("ControllerUnApprovedIngestEntry.onApprove");
    }

    @FXML
    void onIngestClick(MouseEvent event) {
        System.out.println("ControllerUnApprovedIngestEntry.onIngestClick");
    }

}
