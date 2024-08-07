package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.SceneController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class ControllerSIdePanelUpdate {

    @FXML
    private Button btnUpdate;

    @FXML
    private Label lblInfo;

    @FXML
    private Label lblVersion;

    private Task<File> task;

    @FXML
    void initialize() {
        String versionInfo = Main.versionInfo.getUpdateVersionInfo();
        String details = Main.versionInfo.getDetails();

        details = details.replace("\\n", "\n");

        System.out.println("Version Details: " + details);

        lblVersion.setText(versionInfo);
        lblInfo.setText(details);
    }

    @FXML
    public void onUpdateBtnClick(ActionEvent event) {
        // Current Code
        /*Scene scene = SceneController.getSceneFromEvent(event);
    Button btnUpdate = (Button) scene.lookup("#btnUpdate");
    Task<Void> task = new Task<>() {
        @Override
        protected Void call() throws Exception {
            Platform.runLater(() -> {
                btnUpdate.setText("Downloading");
                btnUpdate.setOnAction(actionEvent -> {});
            });
            File updateFile = Main.versionInfo.getUpdate(btnUpdate); // Your code is inside this method
            Platform.runLater(() -> {
                try {
                    if (updateFile != null) {
                        Platform.runLater(() -> btnUpdate.setText("Installing..."));
                        Desktop.getDesktop().open(updateFile);
                    } else {
                        Platform.runLater(() -> {
                            btnUpdate.setText("Error!");
                            btnUpdate.setStyle("-fx-border-color: red;");
                        });
                    }
                } catch (IOException e) {
                    AlertBuilder.sendErrorAlert("Error", "Error Opening Update Package", "Path: " + updateFile.getAbsolutePath() + "\n\n" + e);
                }
            });
            return null;
        }
    };

    Thread thread = new Thread(task);
    thread.start();*/

        // Claude.ai modification
        Scene scene = SceneController.getSceneFromEvent(event);
        Button btnUpdate = (Button) scene.lookup("#btnUpdate");
        Label lblUpdate = UIController.lblUserEmailAndUpdateStatic;
        btnUpdate.setText("Preparing");
        // ProgressBar progressBar = (ProgressBar) scene.lookup("#progressBar"); // Assume you've added this to your FXML

        if (task != null && task.isRunning()) {
            // If task is already running, clicking the button will prompt for cancellation
            showCancellationAlert(btnUpdate);
            return;
        }

        task = new Task<>() {
            @Override
            protected File call() throws Exception {
                updateProgress(0, 100);

                File updateFile = Main.versionInfo.getUpdate(btnUpdate, lblUpdate, task);

                if (updateFile != null && !isCancelled()) {
                    Platform.runLater(() -> btnUpdate.setText("Installing..."));
                }

                return updateFile;
            }
        };

        // btnUpdate.textProperty().bind(task.messageProperty());

        task.setOnSucceeded(e -> {
            File updateFile = task.getValue();
            if (updateFile != null) {
                try {
                    Desktop.getDesktop().open(updateFile);
                    Platform.exit();
                    System.exit(0);
                    // btnUpdate.setText("Restart to complete update");
                } catch (IOException ex) {
                    AlertBuilder.sendErrorAlert("Error", "Error Opening Update Package", "Path: " + updateFile.getAbsolutePath() + "\n\n" + ex);
                    btnUpdate.setText("Update Failed");
                }
            } else {
                btnUpdate.setText("Update Failed");
            }
            btnUpdate.setDisable(false);
        });

        task.setOnFailed(e -> {
            btnUpdate.setText("Update Failed");
            btnUpdate.setDisable(false);
            AlertBuilder.sendErrorAlert("Error", "Update Failed", task.getException().getMessage());
        });

        task.setOnCancelled(e -> btnUpdate.setText("Cancelling..."));

        // progressBar.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
    }

private void showCancellationAlert(Button btnUpdate) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Cancel Download");
    alert.setHeaderText("Do you want to cancel the download?");
    alert.setContentText("The download progress will be lost.");

    Optional<ButtonType> result = alert.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {
        task.cancel();

        // Set a timer to keep "Cancelling..." text for a short period
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, e -> btnUpdate.setText("Cancelling...")),
                new KeyFrame(Duration.seconds(2), e -> {
                    if (!task.isRunning()) {
                        btnUpdate.setText("Update");
                    }
                })
        );

        timeline.play();
    }
}
}