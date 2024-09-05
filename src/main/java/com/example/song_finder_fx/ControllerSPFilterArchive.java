package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.SceneController;
import com.example.song_finder_fx.Controller.TextFormatter;
import com.example.song_finder_fx.Model.ArchivedMCUI;
import com.example.song_finder_fx.Model.ManualClaimTrack;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ControllerSPFilterArchive {

    @FXML
    public Button btnFilter;

    @FXML
    private DatePicker dpEnd;

    @FXML
    private DatePicker dpStart;

    public static List<ArchivedMCUI> archivedMCUIS = new ArrayList<>();

    @FXML
    void onFilter() {
        // Clearing current claims
        if (archivedMCUIS != null) {
            archivedMCUIS.clear();
        }

        // Get Dates
        LocalDate startDate = dpStart.getValue();
        LocalDate endDate = dpEnd.getValue();

        // Validate Dates
        if (startDate == null) {
            dpStart.setStyle("-fx-border-color: red;");
        } else if (endDate == null) {
            dpEnd.setStyle("-fx-border-color: red;");
        } else {
            // Check if the start date is grater than end date
            checkDatesGraterThan(startDate, endDate);

            ControllerMCArchiveList.vbClaimsListStatic.getChildren().clear();

            // Reset Style
            dpStart.setStyle("-fx-border-color: '#E9EBEE';");
            dpEnd.setStyle("-fx-border-color: '#E9EBEE';");

            // Filter Claims
            @SuppressWarnings("unchecked") final List<ManualClaimTrack>[] archivedManualClaims = new List[1];

            Task<Void> taskGetClaims = new Task<>() {
                @Override
                protected Void call() {
                    try {

                        int count = DatabasePostgres.getAMClaimCountFor(startDate, endDate);

                        Platform.runLater(() -> {
                            // Create Timeline for animation
                            Timeline timeline = new Timeline();

                            // Animate width change
                            KeyValue kvWidth = new KeyValue(btnFilter.prefWidthProperty(), 180);
                            KeyFrame kfWidth = new KeyFrame(Duration.millis(300), kvWidth);

                            // Fade out text
                            KeyValue kvTextFadeOut = new KeyValue(btnFilter.opacityProperty(), 0);
                            KeyFrame kfTextFadeOut = new KeyFrame(Duration.millis(150), kvTextFadeOut);

                            // Change text and fade in
                            KeyValue kvTextFadeIn = new KeyValue(btnFilter.opacityProperty(), 1);
                            KeyFrame kfTextChange = new KeyFrame(Duration.millis(151),
                                    e -> btnFilter.setText("Loading " + count + " Claims"));
                            KeyFrame kfTextFadeIn = new KeyFrame(Duration.millis(300), kvTextFadeIn);

                            // Add all keyframes to the timeline
                            timeline.getKeyFrames().addAll(kfWidth, kfTextFadeOut, kfTextChange, kfTextFadeIn);

                            // Play the animation
                            timeline.play();
                        });

                        archivedManualClaims[0] = DatabasePostgres.getArchivedManualClaims(startDate, endDate);

                    } catch (SQLException e) {
                        Platform.runLater(() -> AlertBuilder.sendErrorAlert("Error", "Error Filtering Manual Claims", e.toString()));
                    }

                    return null;
                }
            };

            Thread threadGetClaims = new Thread(taskGetClaims);
            threadGetClaims.start();

            taskGetClaims.setOnSucceeded(event1 -> {
                if (!archivedManualClaims[0].isEmpty()) {
                    for (ManualClaimTrack track : archivedManualClaims[0]) {
                        int claimID = track.getId();
                        // Image previewImage = track.getPreviewImage();
                        String title = track.getTrackName();
                        String composer = track.getComposer();
                        String lyricist = track.getLyricist();
                        LocalDate date = track.getDate();
                        String claimType = track.getClaimTypeString();

                        try {
                            Node node = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/manual_claims/archived-claims-list-entry.fxml")));

                            // Lookup entry items
                            Label lblSongNo = (Label) node.lookup("#lblSongNo");
                            Label lblSongName = (Label) node.lookup("#lblSongName");
                            Label lblComposer = (Label) node.lookup("#lblComposer");
                            Label lblLyricist = (Label) node.lookup("#lblLyricist");
                            Label lblDate = (Label) node.lookup("#lblDate");
                            Label lblClaimType = (Label) node.lookup("#lblClaimType");
                            CheckBox checkBox = (CheckBox) node.lookup("#checkBox");
                            HBox hboxEntry = (HBox) node.lookup("#hboxEntry");
                            // ImageView image = (ImageView) node.lookup("#image");

                            // Setting values
                            lblSongNo.setText(String.valueOf(claimID));
                            lblSongName.setText(title);
                            lblComposer.setText(composer);
                            lblLyricist.setText(lyricist);
                            lblDate.setText(TextFormatter.getDaysAgo(date));
                            lblClaimType.setText(claimType);
                            /*try {
                                image.setImage(previewImage);
                            } catch (Exception e) {
                                errorCount++;
                                errorSummary.append(String.format("""
                                        Error loading preview image for:
                                        Claim ID: %d
                                        Claim Name: %s
                                        Date: %s
                                        Type: %s

                                        """, claimID, title, date, claimType));
                            }*/

                            // Adding entry items for later access
                            ArchivedMCUI ui = new ArchivedMCUI(lblSongNo, lblClaimType, checkBox, hboxEntry);
                            ui.setClaim(track);
                            archivedMCUIS.add(ui);

                            Platform.runLater(() -> ControllerMCArchiveList.vbClaimsListStatic.getChildren().add(node));
                        } catch (IOException e) {
                            Platform.runLater(() -> AlertBuilder.sendErrorAlert("Error", "Error Initializing UI", e.toString()));
                            break;
                        }
                    }

                    Platform.runLater(() -> {
                        try {
                            resetSidePanel();
                        } catch (IOException e) {
                            System.out.println("Error resetting side panel: " + e);
                        }
                    });
                }
            });
        }
    }

    private void resetSidePanel() throws IOException {
        Node node2 = SceneController.loadLayout("layouts/sidepanel-blank.fxml");
        UIController.sideVBoxStatic.getChildren().setAll(node2);
    }

    private static void checkDatesGraterThan(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            AlertBuilder.sendInfoAlert("Error", "Check Dates", "Your start date is grater than end date");
        }
    }

}