package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.*;
import com.example.song_finder_fx.Controller.TextFormatter;
import com.example.song_finder_fx.Model.ManualClaimTrack;
import com.example.song_finder_fx.Model.Songs;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
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
    private Button btnEditTrack;

    @FXML
    private TitledPane titledPane;

    private int trackID;

    @FXML
    public void initialize() throws SQLException {
        // List<String> songTitles = DatabaseMySQL.getAllSongs();
        List<String> songTitles = DatabasePostgres.getAllSongTitles();
        // List<String> artistValidation = DatabasePostgres.getAllArtists();
        List<String> artistValidation = DatabasePostgres.getAllValidatedArtists();
        TextFields.bindAutoCompletion(txtTrackTitle, songTitles);
        TextFields.bindAutoCompletion(txtComposer, artistValidation);
        TextFields.bindAutoCompletion(txtLyricist, artistValidation);

        txtTrackTitle.setOnAction(event -> {
            Songs songs;
            String songName = txtTrackTitle.getText();
            // System.out.println("songName = " + songName);
            try {
                songs = DatabasePostgres.searchContributorsSR(songName);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            txtComposer.setText(songs.getComposer());
            txtLyricist.setText(songs.getLyricist());

            spinnerStart.requestFocus();
        });
    }

    public void onAddTrack() throws IOException {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    Platform.runLater(() -> btnAddTrack.setText("Validating Claim..."));
                    Node nodeTrack = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/manual_claims/manual-claims-track.fxml")));

                    // Fetching user values
                    String trackName = txtTrackTitle.getText();
                    String lyricist = txtLyricist.getText();
                    String composer = txtComposer.getText();
                    String trimStart = spinnerStart.getText();
                    String trimEnd = spinnerEnd.getText();
                    String url = ControllerManualClaims.txtURL_Static.getText();
                    String selectedItem = ControllerManualClaims.comboClaimTypeStatic.getSelectionModel().getSelectedItem();
                    int claimType = getClaimType(selectedItem);

                    // Front-End validation
                    boolean ifAnyNull = checkData();

                    if (!ifAnyNull) {
                        // Fetching YouTube ID
                        String youtubeID = TextFormatter.extractYoutubeID(url);

                        // Getting Date
                        LocalDate date = LocalDate.now();

                        // Creating track model
                        ManualClaimTrack track = new ManualClaimTrack(0, trackName, lyricist, composer, youtubeID, date, claimType);
                        MCTrackController mcTrackController = new MCTrackController(track);

                        try {
                            mcTrackController.checkNew();
                        } catch (SQLException e) {
                            Platform.runLater(() -> AlertBuilder.sendErrorAlert("Error", "Something went wrong", e.toString()));
                            // ErrorDialog.showErrorDialog("Error!", "Error Adding Track Data", e.toString());
                        }

                        boolean claimValidation = true;

                        // Checking trim times
                        if (!trimStart.isEmpty() && !trimEnd.isEmpty()) {
                            // Validating trim times
                            boolean status = TextFormatter.validateTrimTimes(trimStart, trimEnd); // This will return false either if trim time is less than 50 seconds or if trim time is not in 00:00:00 format
                            if (status) {
                                // Adding trim times to model
                                track.addTrimTime(trimStart, trimEnd);
                            } else {
                                claimValidation = false;
                                Platform.runLater(() -> {
                                    AlertBuilder.sendErrorAlert("Error Adding Track", "Invalid Trim Time", "Trim time cannot be less than 50 seconds or not in 00:00:00 format");
                                    btnAddTrack.setText("Add Track");
                                });
                                // AlertBuilder.sendErrorAlert("Error Adding Track", "Invalid Trim Time", "Trim time cannot be less than 50 seconds or not in 00:00:00 format");
                            }
                        }

                        if (claimValidation) {
                            trackID = ManualClaims.manualClaims.size();

                            ManualClaims.manualClaims.add(track);

                            Platform.runLater(() -> {
                                titledPane.setText(trackName);
                                titledPane.setExpanded(false);
                                btnAddTrack.setDisable(true);
                                btnAddTrack.setText("Claim Added");
                                // btnEditTrack.setDisable(false);
                                ControllerManualClaims.vboxTracksStatic.getChildren().add(nodeTrack);
                            });

                        }
                    }
                } catch (Exception e) {
                    Platform.runLater(() -> AlertBuilder.sendErrorAlert("Error", "Something went wrong", e.toString()));
                }
                return null;
            }
        };

        new Thread(task).start();
    }

    @FXML
    void onEditTrack() {
        /*Node node = (Node) event.getSource();
        Scene scene = node.getScene();
        TextField txtURL = (TextField) scene.lookup("#txtURL");
        ComboBox<String> comboClaimType = (ComboBox<String>) scene.lookup("#comboClaimType");*/

        /*// Fetching user values
        String trackName = txtTrackTitle.getText();
        String lyricist = txtLyricist.getText();
        String composer = txtComposer.getText();
        String trimStart = spinnerStart.getText();
        String trimEnd = spinnerEnd.getText();
        String url = ControllerManualClaims.txtURL_Static.getText();
        String selectedItem = ControllerManualClaims.comboClaimTypeStatic.getSelectionModel().getSelectedItem();
        int claimType = getClaimType(selectedItem);

        // Front-End validation
        boolean ifAnyNull = checkData();

        if (!ifAnyNull) {
            // Fetching YouTube ID
            String youtubeID = TextFormatter.extractYoutubeID(url);

            // Getting Date
            LocalDate date = LocalDate.now();

            // Creating track model
            ManualClaimTrack track = new ManualClaimTrack(0, trackName, lyricist, composer, youtubeID, date, claimType);
            MCTrackController mcTrackController = new MCTrackController(track);

            try {
                mcTrackController.checkNew();
            } catch (SQLException e) {
                ErrorDialog.showErrorDialog("Error!", "Error Adding Track Data", e.toString());
            }

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
                // ManualClaims.manualClaims.add(track);

                titledPane.setText(trackName);
                titledPane.setExpanded(false);
                btnAddTrack.setDisable(true);
                btnEditTrack.setDisable(false);

                ManualClaimTrack trackOld = ManualClaims.manualClaims.set(trackID, track);

                try {
                    NotificationBuilder.displayTrayInfo("Manual Claim Edited", "Details of " + trackOld.getTrackName() + " are modified");
                } catch (AWTException e) {
                    e.printStackTrace();
                }

                // vboxTracks.getChildren().add(nodeTrack);
            }
        }*/
    }

    private int getClaimType(String selectedItem) {
        if (selectedItem == null) {
            return 1; // Default value when the selected item is null
        }

        return switch (selectedItem) {
            case "TV Programs" -> 2;
            case "Manual Claim" -> 3;
            case "Single SR" -> 4;
            case "Channel One" -> 7;
            case "Charana" -> 8;
            case "Chat Programmes" -> 9;
            case "Dawasak Da Handawaka" -> 10;
            case "Derana" -> 11;
            case "Derana 60+" -> 12;
            case "Derana Little Star" -> 13;
            case "Dream Star" -> 14;
            case "Hiru" -> 15;
            case "Hiru Star" -> 16;
            case "Imorich Tunes" -> 17;
            case "ITN" -> 18;
            case "Ma Nowana Mama" -> 19;
            case "Monara TV" -> 20;
            case "Roo Tunes" -> 21;
            case "Roopawahini" -> 22;
            case "Sirasa" -> 23;
            case "Siyatha" -> 24;
            case "Supreme TV" -> 25;
            case "Swarnawahini" -> 26;
            default -> 1;
        };
    }

    private boolean checkData() {
        /*boolean status = false;
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
                if (!trimEnd.isEmpty()) {
                    if (isNotValidTimeFormat(trimStart)) {
                        status = true;
                        spinnerEnd.setStyle("-fx-border-color: red;");
                    } else {
                        LocalTime startTime = LocalTime.parse(trimStart);
                        LocalTime endTime = LocalTime.parse(trimEnd);

                        if (endTime.isBefore(startTime)) {
                            status = true;
                            spinnerEnd.setStyle("-fx-border-color: red;");
                            spinnerStart.setStyle("-fx-border-color: red;");
                            NotificationBuilder.displayTrayError("Time Validation Error", "Please check trim start and end times");
                        } else {
                            spinnerEnd.setStyle("-fx-border-color: '#e9ebee';");
                            spinnerStart.setStyle("-fx-border-color: '#e9ebee';");
                        }
                    }
                }
            }
        }

        return status;*/
        boolean status = false;
        String trackName = txtTrackTitle.getText();
        String lyricist = txtLyricist.getText();
        String composer = txtComposer.getText();
        String trimStart = spinnerStart.getText();
        String trimEnd = spinnerEnd.getText();

        if (trackName.isEmpty()) {
            status = true;
            Platform.runLater(() -> txtTrackTitle.setStyle("-fx-border-color: red;"));
        } else {
            Platform.runLater(() -> txtTrackTitle.setStyle("-fx-border-color: '#e9ebee';"));
        }

        if (lyricist.isEmpty()) {
            status = true;
            Platform.runLater(() -> txtLyricist.setStyle("-fx-border-color: red;"));
        } else {
            Platform.runLater(() -> txtLyricist.setStyle("-fx-border-color: '#e9ebee';"));
        }

        if (composer.isEmpty()) {
            status = true;
            Platform.runLater(() -> txtComposer.setStyle("-fx-border-color: red;"));
        } else {
            Platform.runLater(() -> txtComposer.setStyle("-fx-border-color: '#e9ebee';"));
        }

        if (!trimStart.isEmpty()) {
            if (isNotValidTimeFormat(trimStart)) {
                status = true;
                Platform.runLater(() -> spinnerStart.setStyle("-fx-border-color: red;"));
            } else {
                if (!trimEnd.isEmpty()) {
                    if (isNotValidTimeFormat(trimStart)) {
                        status = true;
                        Platform.runLater(() -> spinnerEnd.setStyle("-fx-border-color: red;"));
                    } else {
                        LocalTime startTime = LocalTime.parse(trimStart);
                        LocalTime endTime = LocalTime.parse(trimEnd);

                        if (endTime.isBefore(startTime)) {
                            status = true;
                            Platform.runLater(() -> {
                                spinnerEnd.setStyle("-fx-border-color: red;");
                                spinnerStart.setStyle("-fx-border-color: red;");
                                NotificationBuilder.displayTrayError("Time Validation Error", "Please check trim start and end times");
                            });
                        } else {
                            Platform.runLater(() -> {
                                spinnerEnd.setStyle("-fx-border-color: '#e9ebee';");
                                spinnerStart.setStyle("-fx-border-color: '#e9ebee';");
                            });
                        }
                    }
                }
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

    @FXML
    void onGetStartTime() {
        WebEngine engine = ControllerManualClaims.ytPlayerStatic.getEngine();
        String currentTime = (String) engine.executeScript("document.getElementById('current-time').innerHTML");
        String formattedTime = TextFormatter.formatTime(currentTime);
        spinnerStart.setText(formattedTime);
    }

    @FXML
    void onGetEndTime() {
        WebEngine engine = ControllerManualClaims.ytPlayerStatic.getEngine();
        String currentTime = (String) engine.executeScript("document.getElementById('current-time').innerHTML");
        String formattedTime = TextFormatter.formatTime(currentTime);
        spinnerEnd.setText(formattedTime);
    }
}
