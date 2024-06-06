package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Model.ArchivedMCUI;
import com.example.song_finder_fx.Model.ManualClaimTrack;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class ControllerMCArchiveList {

    @FXML
    private Label lblClaimCount;

    @FXML
    private VBox vbClaimsList;

    public static VBox vbClaimsListStatic;

    @FXML
    void initialize() {
        setClaimCount();

        vbClaimsListStatic = vbClaimsList;
    }

    private void setClaimCount() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws SQLException {
                int claimCount = DatabasePostgres.getArchivedManualClaimCount();

                Platform.runLater(() -> lblClaimCount.setText("Total: " + claimCount));

                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.start();
    }

    @FXML
    void onLoadClaims(ActionEvent event) {
        try {
            Node node = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/manual_claims/sidepanel-filter-archivelist.fxml")));
            UIController.sideVBoxStatic.getChildren().setAll(node);
        } catch (IOException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Loading SidePanel", e.toString());
            // e.printStackTrace();
        }
    }

    @FXML
    void onMC_HomeBC(MouseEvent event) {

    }

    @FXML
    void onSelectAll(ActionEvent event) {
        for (ArchivedMCUI ui : ControllerSPFilterArchive.archivedMCUIS) {
            ui.getCheckBox().setSelected(true);
        }
    }

    @FXML
    void onSelectNone(ActionEvent event) {
        for (ArchivedMCUI ui : ControllerSPFilterArchive.archivedMCUIS) {
            ui.getCheckBox().setSelected(false);
        }
    }

    @FXML
    void onUnArchiveSelected(ActionEvent event) {
        for (ArchivedMCUI ui : ControllerSPFilterArchive.archivedMCUIS) {
            if (ui.getCheckBox().isSelected()) {
                // UnArchive Claim
                String claimNumber = ui.getLblSongNo().getText();
                System.out.println("Selected Claim ID: " + claimNumber);

                ManualClaimTrack track = ui.getClaim();
                try {
                    int status = track.unArchive();
                    if (status > 0) {
                        ui.getHboxEntry().setDisable(true);
                        ui.getCheckBox().setSelected(false);
                    }
                } catch (SQLException e) {
                    AlertBuilder.sendErrorAlert("Error", "Error Archiving Claim", "Claim ID: " + track.getId() + "\n\n" + e);
                    break;
                }
            }
        }
    }

}
