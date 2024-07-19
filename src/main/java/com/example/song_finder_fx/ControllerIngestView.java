package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.IngestController;
import com.example.song_finder_fx.Controller.SceneController;
import com.example.song_finder_fx.Model.Ingest;
import com.example.song_finder_fx.Model.IngestCSVData;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.sql.SQLException;

public class ControllerIngestView {

    @FXML
    public Button btnApproveIngest;

    @FXML
    public ImageView imgLoading;

    @FXML
    private TableView<IngestCSVData> tableIngest;

    @FXML
    private Label lblIngestName;

    @FXML
    void onApproveIngest() {
        Ingest ingest = ControllerUnApprovedIngestEntry.ingest;
        IngestController ingestController = new IngestController();

        boolean status = AlertBuilder.getSendConfirmationAlert("CeyMusic Dashboard", "Please double check before approval", "Confirm Approval?");
        System.out.println("status = " + status);

        if (status) {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    try {
                        Platform.runLater(() -> {
                            btnApproveIngest.setText("Approving Ingest");
                            imgLoading.setVisible(true);
                        });
                        ingestController.approveIngest(ingest);
                        Platform.runLater(() -> {
                            btnApproveIngest.setText("Approve Ingest");
                            imgLoading.setVisible(false);
                        });
                    } catch (SQLException e) {
                        Platform.runLater(() -> {
                            btnApproveIngest.setText("Approve Ingest");
                            imgLoading.setVisible(false);
                            AlertBuilder.sendErrorAlert("Error", "Approving Ingest", e.toString());
                            e.printStackTrace();
                        });
                    }
                    return null;
                }
            };
            Thread thread = new Thread(task);
            thread.start();
        }
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
        // Node node = SceneController.loadLayout("layouts/ingests/un-approved-ingests.fxml");
        UIController.mainVBoxStatic.getChildren().setAll(ControllerIngest.unApprovedIngestsUI);
    }

    @FXML
    void onSaveEdits(ActionEvent event) {

    }

}
