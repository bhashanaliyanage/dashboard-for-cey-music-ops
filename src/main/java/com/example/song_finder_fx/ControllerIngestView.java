package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.IngestController;
import com.example.song_finder_fx.Controller.SceneController;
import com.example.song_finder_fx.Model.Ingest;
import com.example.song_finder_fx.Model.IngestCSVData;
import com.example.song_finder_fx.Model.ValidationResult;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class ControllerIngestView {

    @FXML
    public Button btnApproveIngest;

    @FXML
    public ImageView imgLoading;

    @FXML
    void onApproveIngest() {
        Ingest ingest = ControllerUnApprovedIngestEntry.ingest;
        IngestController ingestController = new IngestController();

        boolean status = AlertBuilder.getSendConfirmationAlert("CeyMusic Dashboard", "Please double check before approval", "Confirm Approval?");

        if (status) {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws SQLException {
                    // Validate ingest
                    Platform.runLater(() -> {
                        btnApproveIngest.setText("Validating Ingest");
                        imgLoading.setVisible(true);
                    });
                    ValidationResult validatedIngest = ingestController.validateIngest(ingest);

                    if (validatedIngest.isValid()) {
                        // Inset data to the table
                        Platform.runLater(() -> {
                            btnApproveIngest.setText("Approving Ingest");
                            imgLoading.setVisible(true);
                        });
                        ingestController.approveIngest(ingest);
                        Platform.runLater(() -> {
                            btnApproveIngest.setText("Approve Ingest");
                            imgLoading.setVisible(false);
                        });
                    } else {
                        Platform.runLater(() -> {
                            btnApproveIngest.setText("Approve Ingest");
                            imgLoading.setVisible(false);
                            showValidationErrors(validatedIngest.errorMessages());
                        });
                    }
                    /*try {
                    } catch (SQLException e) {
                        Platform.runLater(() -> {
                            btnApproveIngest.setText("Approve Ingest");
                            imgLoading.setVisible(false);
                            AlertBuilder.sendErrorAlert("Error", "Approving Ingest", e.toString());
                            e.printStackTrace();
                        });
                    }*/
                    return null;
                }
            };

            task.setOnFailed(e -> Platform.runLater(() -> {
                btnApproveIngest.setText("Approve Ingest");
                imgLoading.setVisible(false);
                AlertBuilder.sendErrorAlert("Error", "Approving Ingest", e.toString());
            }));

            Thread thread = new Thread(task);
            thread.start();
        }
    }

    private void showValidationErrors(List<String> errorMessages) {
        StringBuilder messageBuilder = new StringBuilder();
        int totalErrors = errorMessages.size();
        int displayedErrors = Math.min(3, totalErrors);

        for (int i = 0; i < displayedErrors; i++) {
            messageBuilder.append(errorMessages.get(i)).append("\n");
        }

        if (totalErrors > 3) {
            int remainingErrors = totalErrors - 3;
            messageBuilder.append("\n... and ").append(remainingErrors).append(" more error");
            if (remainingErrors > 1) {
                messageBuilder.append("s");
            }
            messageBuilder.append(" like these.");
        }

        String errorMessage = messageBuilder.toString();
        AlertBuilder.sendErrorAlert("Validation Error",
                "The ingest could not be validated. Found " + totalErrors + " error(s):",
                errorMessage);
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

    @FXML
    public void onHome(MouseEvent mouseEvent) {
        try {
            Node node = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/ingests-chooser.fxml")));
            UIController.mainVBoxStatic.getChildren().setAll(node);

            FXMLLoader sidepanelLoader = new FXMLLoader(getClass().getResource("layouts/sidepanel-blank.fxml"));
            Parent sidepanelNewContent = sidepanelLoader.load();
            UIController.sideVBoxStatic.getChildren().clear();
            UIController.sideVBoxStatic.getChildren().add(sidepanelNewContent);
        } catch (IOException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Initializing UI", e.toString());
        }
    }
}
