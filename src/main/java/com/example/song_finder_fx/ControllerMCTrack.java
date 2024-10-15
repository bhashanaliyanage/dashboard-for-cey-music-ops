package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.*;
import com.example.song_finder_fx.Model.ManualClaimTrack;
import com.example.song_finder_fx.Model.Songs;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.web.WebEngine;
import org.controlsfx.control.textfield.TextFields;

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

            try {
                songs = DatabasePostgres.searchContributorsSR(songName);
                txtComposer.setText(songs.getComposer());
                txtLyricist.setText(songs.getLyricist());
            } catch (SQLException ignore) {
            }


            spinnerStart.requestFocus();
        });
    }

    public void onAddTrack() {
        if (titledPane.getText().equals("Song Name")) {
            // Perform initial validations
            if (!checkData()) {
                return;
            }

            // Create track model
            String trackName = txtTrackTitle.getText();
            String lyricist = txtLyricist.getText();
            String composer = txtComposer.getText();
            String url = ControllerManualClaims.txtURL_Static.getText();
            String selectedItem = ControllerManualClaims.comboClaimTypeStatic.getSelectionModel().getSelectedItem();
            int claimType = getClaimType(selectedItem);
            String youtubeID = TextFormatter.extractYoutubeID(url);
            LocalDate date = LocalDate.now();

            ManualClaimTrack track = new ManualClaimTrack(0, trackName, lyricist, composer, youtubeID, date, claimType);
            MCTrackController mcTrackController = new MCTrackController(track);

            // Validate artists
            boolean artistsValidated = mcTrackController.validateArtists();

            // If artists are not validated, ask for confirmation
            if (!artistsValidated) {
                boolean shouldProceed = AlertBuilder.getSendConfirmationAlert(
                        "Artist Validation",
                        "Artists Not Validated",
                        "Both artists are not registered in CeyMusic. Do you want to add the track anyway?"
                );
                if (!shouldProceed) {
                    return;
                }
            }

            // If we're here, either artists are validated or user confirmed to proceed
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    try {
                        Platform.runLater(() -> btnAddTrack.setText("Validating Claim..."));
                        Node nodeTrack = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/manual_claims/manual-claims-track.fxml")));

                        String trimStart = spinnerStart.getText();
                        String trimEnd = spinnerEnd.getText();

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
                                Platform.runLater(() -> {
                                    AlertBuilder.sendErrorAlert("Error Adding Track", "Invalid Trim Time", "Trim time cannot be less than 50 seconds or not in 00:00:00 format");
                                    btnAddTrack.setText("Add Track");
                                });
                            }
                        }

                        if (claimValidation) {
                            trackID = ManualClaims.manualClaims.size() + 1;

                            ManualClaims.manualClaims.add(track);

                            Platform.runLater(() -> {
                                titledPane.setText(String.format("%02d | %s", trackID, trackName));
                                titledPane.setExpanded(false);
                                btnAddTrack.setDisable(true);
                                btnAddTrack.setText("Claim Added");
                                disableFields(true);
                                btnEditTrack.setDisable(false);
                                ControllerManualClaims.vboxTracksStatic.getChildren().add(nodeTrack);
                            });
                        }
                    } catch (Exception e) {
                        Platform.runLater(() -> AlertBuilder.sendErrorAlert("Error", "Something went wrong", e.toString()));
                    }
                    return null;
                }
            };

            new Thread(task).start();
        } else {
            saveEdits();
        }
    }

    private void disableFields(boolean type) {
        txtTrackTitle.setDisable(type);
        spinnerStart.setDisable(type);
        spinnerEnd.setDisable(type);
        txtComposer.setDisable(type);
        txtLyricist.setDisable(type);
    }

    @FXML
    void onEditTrack() {
        if (btnEditTrack.getText().equals("Edit Track")) {
            disableFields(false);
            btnEditTrack.setText("Save");
        } else {
            saveEdits();
        }
    }

    private void saveEdits() {
        // Fetching user values
        String titledPaneText = titledPane.getText();
        String trackName = txtTrackTitle.getText();
        String lyricist = txtLyricist.getText();
        String composer = txtComposer.getText();
        String trimStart = spinnerStart.getText();
        String trimEnd = spinnerEnd.getText();

        if (checkData()) {
            int trackNumber = Integer.parseInt(titledPaneText.split("\\|")[0].trim()) - 1;

            // Update the track in the manualClaims list
            if (trackNumber >= 0 && trackNumber < ManualClaims.manualClaims.size()) {
                ManualClaimTrack track = ManualClaims.manualClaims.get(trackNumber);
                track.setTrackName(trackName);
                track.setLyricist(lyricist);
                track.setComposer(composer);
                track.setTrimStart(trimStart);
                track.setTrimEnd(trimEnd);

                // Update the titledPane text
                titledPane.setText(String.format("%02d | %s", trackNumber + 1, trackName));

                // On Completion
                disableFields(true);
                btnEditTrack.setText("Edit Track");
            } else {
                NotificationBuilder.displayTrayError("Error", "Unable to edit the track");
            }
        } else {
            NotificationBuilder.displayTrayError("Error", "Invalid Details");
        }
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

        return !status;
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
