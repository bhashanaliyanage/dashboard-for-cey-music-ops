package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Model.Ingest;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Objects;

public class ControllerUnapprovedIngests {

    @FXML
    private Label lblIngestCount;

    @FXML
    private VBox vbIngestList;

    // public static VBox vbIngestListStatic;

    @FXML
    void initialize() {
        int total = ControllerIngest.unApprovedIngests.size();
        lblIngestCount.setText(String.valueOf(total));

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                for (Ingest ingest : ControllerIngest.unApprovedIngests) {
                    Node node = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("layouts/ingests/un-approved-ingests-entry.fxml")));

                    Label lblIngestID = (Label) node.lookup("#lblIngestID");
                    Label lblIngestName = (Label) node.lookup("#lblIngestName");
                    Label lblAssetCount = (Label) node.lookup("#lblAssetCount");
                    Label lblDate = (Label) node.lookup("#lblDate");
                    Label lblImportedBy = (Label) node.lookup("#lblImportedBy");

                    lblIngestID.setText(String.valueOf(ingest.getIngestID()));
                    lblIngestName.setText(ingest.getName());
                    lblAssetCount.setText(ingest.getAssetCount() + " Assets");
                    lblDate.setText(String.valueOf(ingest.getImportedDate()));
                    lblImportedBy.setText(ingest.getImportedUser());

                    Platform.runLater(() -> vbIngestList.getChildren().add(node));
                }

                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.start();
    }

    @FXML
    void onGoBack() {
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
