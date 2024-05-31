package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.SceneController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class ControllerSIdePanelUpdate {

    @FXML
    private Button btnUpdate;

    @FXML
    private Label lblInfo;

    @FXML
    private Label lblVersion;

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
        Scene scene = SceneController.getSceneFromEvent(event);
        Button btnUpdate = (Button) scene.lookup("#btnUpdate");
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> btnUpdate.setText("Downloading"));
                File updateFile = Main.versionInfo.getUpdate();
                Platform.runLater(() -> {
                    try {
                        Desktop.getDesktop().open(updateFile);
                    } catch (IOException e) {
                        AlertBuilder.sendErrorAlert("Error", "Error Opening Update Package", "Path: " + updateFile.getAbsolutePath() + "\n\n" + e);
                    }
                });
                Platform.runLater(() -> btnUpdate.setText("Installing..."));
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.start();
    }
}