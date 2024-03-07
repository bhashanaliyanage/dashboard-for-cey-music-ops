package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.NotificationBuilder;
import com.example.song_finder_fx.Model.ManualClaimTrack;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

import static com.example.song_finder_fx.Controller.ManualClaims.manualClaims;

public class ControllerManualClaims {

    public WebView ytPlayer;
    public VBox vboxTracks;
    @FXML
    private TextField txtURL;

    @FXML
    public void initialize() {
    }

    @FXML
    void onCheckBtnClicked() throws IOException, SQLException {
        String URL = txtURL.getText();

        if (!Objects.equals(URL, "")) {
            System.out.println("URL = " + URL);

            if (URL.length() > 32) {
                String prefix = URL.substring(0, 32);

                if (prefix.equals("https://www.youtube.com/watch?v=")) {
                    String ID = URL.substring(32);

                    // If this ID is in the manual claims database, show an alert.
                    int previousClaims = DatabasePostgre.checkPreviousClaims(ID);
                    if (previousClaims > 0) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Previous Added Claims Found");
                        alert.setHeaderText("Previous Added Claims Found");
                        alert.setContentText(previousClaims + " Previous claims found for this video");

                        alert.showAndWait();
                    }

                    String embedID = "https://www.youtube.com/embed/" + ID;

                    System.out.println("embedID = " + embedID);

                    ytPlayer.getEngine().load(embedID);

                    Node node = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/manual_claims/manual-claims-track.fxml")));
                    vboxTracks.getChildren().setAll(node);
                } else {
                    System.out.println("No");
                    System.out.println("prefix = " + prefix);
                    System.out.println("URL = " + URL);
                }
            } else {
                System.out.println("Invalid URL");
            }
        } else {
            System.out.println("URL Empty");
        }
    }

    public void onAddManualClaim() throws SQLException, AWTException, IOException {
        for (ManualClaimTrack claim : manualClaims) {
            String songName = claim.getTrackName();
            String lyricist = claim.getLyricist();
            String composer = claim.getComposer();
            String id = claim.getYoutubeID();
            int status = DatabasePostgre.addManualClaim(songName, lyricist, composer, id);
            if (status < 1) {
                NotificationBuilder.displayTrayError("Error!", "Error Adding Manual Claim");
            } else {
                NotificationBuilder.displayTrayInfo("Manual Claim Added", "Your Claim for " + songName + " is successfully added");
                Node node = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/manual_claims/manual-claims-track.fxml")));
                vboxTracks.getChildren().setAll(node);
            }
        }
    }

    @FXML
    void onGoBack(MouseEvent event) throws IOException {
        Node node = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/manual_claims/manual-claims-main.fxml")));
        Scene scene = ((Node) event.getSource()).getScene();
        VBox mainVBox = (VBox) scene.lookup("#mainVBox");
        mainVBox.getChildren().setAll(node);
    }
}
