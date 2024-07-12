package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.SceneController;
import com.example.song_finder_fx.Model.IngestCSVData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class ControllerIngestView {

    @FXML
    private TableView<IngestCSVData> tableIngest;

    @FXML
    private Label lblIngestName;

    @FXML
    void onApproveIngest(ActionEvent event) {

    }

    @FXML
    void onAssignPayees(ActionEvent event) {
        try {
            Node node = SceneController.loadLayout("layouts/ingests/payee-updater-view.fxml");
            UIController.mainVBoxStatic.getChildren().setAll(node);
        } catch (IOException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Initializing UI", e.toString());
            e.printStackTrace();
        }
    }

    @FXML
    void onGoBack(MouseEvent event) {

    }

    @FXML
    void onSaveEdits(ActionEvent event) {

    }

}
