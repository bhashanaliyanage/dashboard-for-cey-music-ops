package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.ImageProcessor;
import com.example.song_finder_fx.Controller.MCTrackController;
import com.example.song_finder_fx.Controller.ManualClaims;
import com.example.song_finder_fx.Controller.TextFormatter;
import com.example.song_finder_fx.Model.ManualClaimTrack;
import com.example.song_finder_fx.Model.Songs;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.TextFields;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class ControllerMCTrack {

    @FXML
    public TextField spinnerStart;

    @FXML
    public TextField spinnerEnd;

    @FXML
    private TextField txtTrackTitle;

    @FXML
    private TextField txtComposer;

    @FXML
    private TextField txtLyricist;

    @FXML
    private Button btnAddTrack;

    @FXML
    private TitledPane titledPane;

    @FXML
    public void initialize() throws SQLException {
        // List<String> songTitles = DatabaseMySQL.getAllSongs();
        List<String> songTitles = DatabasePostgres.getAllSongTitles();
        List<String> artistValidation = DatabasePostgres.getAllArtists();
        TextFields.bindAutoCompletion(txtTrackTitle, songTitles);
        TextFields.bindAutoCompletion(txtComposer, artistValidation);
        TextFields.bindAutoCompletion(txtLyricist, artistValidation);

        txtTrackTitle.setOnAction(event -> {
            Songs songs;
            String songName = txtTrackTitle.getText();
            // System.out.println("songName = " + songName);
            try {
                songs = DatabasePostgres.searchContributors(songName);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            txtComposer.setText(songs.getComposer());
            txtLyricist.setText(songs.getLyricist());

            spinnerStart.requestFocus();
        });
    }

    public void onAddTrack(ActionEvent event) throws IOException, URISyntaxException, SQLException, ClassNotFoundException {
        // Getting Parent Object References
        Node node = (Node) event.getSource();
        Scene scene = node.getScene();
        TextField txtURL = (TextField) scene.lookup("#txtURL");
        VBox vboxTracks = (VBox) scene.lookup("#vboxTracks");

        Node nodeTrack = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/manual_claims/manual-claims-track.fxml")));

        // Fetching user values
        String trackName = txtTrackTitle.getText();
        String lyricist = txtLyricist.getText();
        String composer = txtComposer.getText();
        String trimStart = spinnerStart.getText();
        String trimEnd = spinnerEnd.getText();
        String url = txtURL.getText();
        // System.out.println(url);

        // Front-End validation
        boolean ifAnyNull = checkData();

        if (!ifAnyNull) {
            // Fetching YouTube ID
            String youtubeID = TextFormatter.extractYoutubeID(url);

            // Fetching Thumbnail
            String thumbnailURL = "https://i.ytimg.com/vi/" + youtubeID + "/maxresdefault.jpg";
            BufferedImage image = ImageProcessor.getDownloadedImage(thumbnailURL);
            image = ImageProcessor.cropImage(image);

            // Getting Date
            LocalDate date = LocalDate.now();

            // Creating track model
            ManualClaimTrack track = new ManualClaimTrack(0, trackName, lyricist, composer, youtubeID, date);
            MCTrackController mcTrackController = new MCTrackController(track);
            mcTrackController.checkNew();

            // Setting Thumbnail and Preview Images to the model
            track.setPreviewImage(image);
            image = ImageProcessor.resizeImage(1400, 1400, image);
            track.setImage(image);

            boolean claimValidation = true;

            // Checking trim times
            if (!trimStart.isEmpty() && !trimEnd.isEmpty()) {
                // Validating trim times
                boolean status = TextFormatter.validateTrimTimes(trimStart, trimEnd);
                if (status) {
                    // Adding trim times to model
                    track.addTrimTime(trimStart, trimEnd);
                } else {
                    claimValidation = false;
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Error Parsing Time");
                    alert.showAndWait();
                }
            }

            if (claimValidation) {
                ManualClaims.manualClaims.add(track);
                titledPane.setText(trackName);
                titledPane.setExpanded(false);
                btnAddTrack.setDisable(true);

                vboxTracks.getChildren().add(nodeTrack);
            }
        }
    }

    private boolean checkData() {
        boolean status = false;
        String trackName = txtTrackTitle.getText();
        String lyricist = txtLyricist.getText();
        String composer = txtComposer.getText();
        String trimStart = spinnerStart.getText();
        String trimEnd = spinnerEnd.getText();

        if (trackName.isEmpty()) {
            status = true;
            txtTrackTitle.setStyle("-fx-border-color: red;");
        } else {
            txtTrackTitle.setStyle("-fx-border-color: '#e9ebee';");
        }

        if (lyricist.isEmpty()) {
            status = true;
            txtLyricist.setStyle("-fx-border-color: red;");
        } else {
            txtLyricist.setStyle("-fx-border-color: '#e9ebee';");
        }

        if (composer.isEmpty()) {
            status = true;
            txtComposer.setStyle("-fx-border-color: red;");
        } else {
            txtComposer.setStyle("-fx-border-color: '#e9ebee';");
        }

        if (!trimStart.isEmpty()) {
            if (isNotValidTimeFormat(trimStart)) {
                status = true;
                spinnerStart.setStyle("-fx-border-color: red;");
            } else {
                spinnerStart.setStyle("-fx-border-color: '#e9ebee';");
            }
        }

        if (!trimEnd.isEmpty()) {
            if (isNotValidTimeFormat(trimStart)) {
                status = true;
                spinnerEnd.setStyle("-fx-border-color: red;");
            } else {
                spinnerEnd.setStyle("-fx-border-color: '#e9ebee';");
            }
        }

        return status;
    }

    private boolean isNotValidTimeFormat(String timeText) {
        String[] parts = timeText.split(":");
        if (parts.length != 3) {
            return true; // Not in HH:MM:SS format
        }

        try {
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            int seconds = Integer.parseInt(parts[2]);

            // Validate ranges
            if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59 || seconds < 0 || seconds > 59) {
                return true; // Out of valid range
            }
        } catch (NumberFormatException e) {
            return true; // Not numeric values
        }

        return false; // Valid HH:MM:SS format
    }

    public void formatStartTime() {
        String time = spinnerStart.getText();
        String formattedTime = TextFormatter.formatTime(time);
        spinnerStart.setText(formattedTime);
        spinnerEnd.requestFocus();
    }

    public void formatEndTime() {
        String time = spinnerEnd.getText();
        String formattedTime = TextFormatter.formatTime(time);
        spinnerEnd.setText(formattedTime);
        txtLyricist.requestFocus();
    }

    public void onLyricistAction() {
        txtComposer.requestFocus();
    }
}
