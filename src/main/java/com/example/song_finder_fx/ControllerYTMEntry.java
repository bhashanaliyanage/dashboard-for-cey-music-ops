package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.TextFormatter;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.Objects;

import static com.example.song_finder_fx.Controller.ManualClaims.manualClaims;

public class ControllerYTMEntry {

    @FXML
    private Label lblYTLink;

@FXML
void onAddManualClaim() {
    Task<Void> task = new Task<>() {
        @Override
        protected Void call() {
            try {
                String URL = lblYTLink.getText();

                if (!isValidYoutubeUrl(URL)) {
                    AlertBuilder.sendErrorAlert("Error", "Invalid YouTube URL", null);
                    return null;
                }

                String ID2 = TextFormatter.extractYoutubeID(URL);
                String embedID = "https://www.youtube.com/embed/" + ID2;

                Platform.runLater(() -> {
                    try {
                        Node nodeMC = FXMLLoader.load(Objects.requireNonNull(ControllerManualClaims.class.getResource("layouts/manual_claims/manual-claims.fxml")));
                        Node node = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/manual_claims/manual-claims-track.fxml")));
                        UIController.mainVBoxStatic.getChildren().clear();
                        UIController.mainVBoxStatic.getChildren().add(nodeMC);
                        ControllerManualClaims.txtURL_Static.setText(URL);
                        ControllerManualClaims.ytPlayerStatic.getEngine().load(embedID);
                        // TODO: Show previous claims
                        ControllerManualClaims.comboClaimTypeStatic.requestFocus();
                        ControllerManualClaims.vboxTracksStatic.getChildren().clear();
                        ControllerManualClaims.vboxTracksStatic.getChildren().add(node);
                    } catch (IOException e) {
                        Platform.runLater(() -> {
                            AlertBuilder.sendErrorAlert("Error", "Something went wrong when loading manual claims", e.getMessage());
                            e.printStackTrace();
                        });
                    }
                });

                manualClaims.clear();
            } catch (Exception e) {
                Platform.runLater(() -> {
                    AlertBuilder.sendErrorAlert("Error", "Something went wrong when loading manual claims", e.getMessage());
                    e.printStackTrace();
                });
            }
            return null;
        }
    };

    Thread thread = new Thread(task);
    thread.setDaemon(true);
    thread.start();
}

    private boolean isValidYoutubeUrl(String url) {
        return url != null && !Objects.equals(url, "");
    }


}
