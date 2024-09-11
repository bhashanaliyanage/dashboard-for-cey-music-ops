package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ControllerRRList {

    @FXML
    private Label lblDate;

    @FXML
    private Label lblImportedBy;

    @FXML
    private Label lblReportCount;

    @FXML
    private Label lblReportDate;

    @FXML
    private Label lblReportID;

    @FXML
    private Label lblReportName;

    @FXML
    private VBox vbReportsList;

    @FXML
    void initialize() {

    }

    @FXML
    void onAddNewReport(ActionEvent event) {
        try {
            FXMLLoader loaderMain = new FXMLLoader(ControllerSettings.class.getResource("layouts/revenue-report-processing.fxml"));
            Parent newContentMain = loaderMain.load();
            UIController.mainVBoxStatic.getChildren().setAll(newContentMain);
        } catch (IOException e) {
            AlertBuilder.sendErrorAlert("Error", "Something went wrong when loading the layout", e.getMessage());
        }
    }

}
