package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.TextFormatter;
import com.example.song_finder_fx.Model.ArchivedMCUI;
import com.example.song_finder_fx.Model.ManualClaimTrack;
import com.itextpdf.kernel.pdf.PdfTextArray;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ControllerSPFilterArchive {

    @FXML
    private DatePicker dpEnd;

    @FXML
    private DatePicker dpStart;

    @FXML
    private Label lblTrackName;

    public static List<ArchivedMCUI> archivedMCUIS = new ArrayList<>();

    @FXML
    void onFilter(ActionEvent event) throws InterruptedException {
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
            final List<ManualClaimTrack>[] archivedManualClaims = new List[1];

            Task<Void> taskGetClaims = new Task<>() {
                @Override
                protected Void call() {
                    try {
                        archivedManualClaims[0] = DatabasePostgres.getArchivedManualClaims(startDate, endDate);

                        System.out.println("\nTotal: " + archivedManualClaims[0].size());
                        System.out.println("\n");
                    } catch (SQLException e) {
                        Platform.runLater(() -> {
                            AlertBuilder.sendErrorAlert("Error", "Error Filtering Manual Claims", e.toString());
                        });
                    }

                    return null;
                }
            };

            Thread threadGetClaims = new Thread(taskGetClaims);
            threadGetClaims.start();
            threadGetClaims.join();

            Task<Void> taskSHowClaims = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    if (!archivedManualClaims[0].isEmpty()) {
                        for (ManualClaimTrack track : archivedManualClaims[0]) {
                            int claimID = track.getId();
                            Image previewImage = track.getPreviewImage();
                            String title = track.getTrackName();
                            String composer = track.getComposer();
                            String lyricist = track.getLyricist();
                            LocalDate date = track.getDate();
                            String claimType = track.getClaimTypeString();

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
                            ImageView image = (ImageView) node.lookup("#image");

                            // Setting values
                            lblSongNo.setText(String.valueOf(claimID));
                            lblSongName.setText(title);
                            lblComposer.setText(composer);
                            lblLyricist.setText(lyricist);
                            lblDate.setText(TextFormatter.getDaysAgo(date));
                            lblClaimType.setText(claimType);
                            try {
                                image.setImage(previewImage);
                            } catch (Exception e) {
                                AlertBuilder.sendErrorAlert("Error",
                                        "Error Loading Preview Image",
                                        "Claim ID: " + claimID +
                                                "\nClaim Name: " + title +
                                                "\nDate: " + date +
                                                "\nType: " + claimType);
                            }

                            // Adding entry items for later access
                            ArchivedMCUI ui = new ArchivedMCUI(lblSongNo, lblClaimType, checkBox, hboxEntry);
                            ui.setClaim(track);
                            archivedMCUIS.add(ui);
                            /*labelsSongNo.add(lblSongNo);
                            labelsClaimType.add(lblClaimType);
                            checkBoxes.add(checkBox);
                            hBoxes.add(hboxEntry);*/

                            Platform.runLater(() -> ControllerMCArchiveList.vbClaimsListStatic.getChildren().add(node));
                        }
                    }

                    return null;
                }
            };

            Thread threadShowClaims = new Thread(taskSHowClaims);
            threadShowClaims.start();

        }

    }

    private static List<ManualClaimTrack> filterManualClaims(LocalDate startDate, LocalDate endDate) {
        final List<ManualClaimTrack>[] archivedManualClaims = new List[1];

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
                    archivedManualClaims[0] = DatabasePostgres.getArchivedManualClaims(startDate, endDate);

                    System.out.println("\nTotal: " + archivedManualClaims[0].size());
                    System.out.println("\n");

                    /*for (ManualClaimTrack track : archivedManualClaims[0]) {
                        System.out.println("Name: " + track.getTrackName());
                        // Platform.runLater(() -> System.out.println("Name: " + track.getTrackName()));
                    }*/
                } catch (SQLException e) {
                    Platform.runLater(() -> {
                        AlertBuilder.sendErrorAlert("Error", "Error Filtering Manual Claims", e.toString());
                    });
                }

                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.start();

        return archivedManualClaims[0];
    }

    private static void checkDatesGraterThan(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            AlertBuilder.sendInfoAlert("Error", "Check Dates", "Your start date is grater than end date");
        }
    }

}