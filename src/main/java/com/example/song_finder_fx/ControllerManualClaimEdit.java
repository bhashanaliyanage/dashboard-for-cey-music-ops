package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.*;
import com.example.song_finder_fx.Controller.TextFormatter;
import com.example.song_finder_fx.Model.ManualClaimTrack;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Objects;

public class ControllerManualClaimEdit {

    @FXML
    private Button btnSaveChanges;

    public Label lblLink;

    @FXML
    private Label lblClaimID;

    @FXML
    private TextField txtComposer;

    @FXML
    private TextField txtLyricist;

    @FXML
    private TextField txtSongName;

    @FXML
    private ImageView imgPreview;

    @FXML
    private TextField txtStartTime;

    @FXML
    private TextField txtEndTime;

    private boolean buttonEnabled = false;

    @FXML
    void initialize() {

    }

    @FXML
    void onEdit() {
        // ReadOnlyBooleanProperty property = btnSaveChanges.disabledProperty();

        if (!buttonEnabled) {
            btnSaveChanges.setDisable(false);
            buttonEnabled = true;
        }
    }

    @FXML
    void onSave() throws SQLException {
        String songID = lblClaimID.getText();
        String trackName = txtSongName.getText();
        String composer = txtComposer.getText();
        String lyricist = txtLyricist.getText();
        String trimStart = txtStartTime.getText();
        String trimEnd = txtEndTime.getText();

        boolean claimValidation = true;

        for (int i = 0; i < ControllerMCList.labelsSongNo.size(); i++) {
            if (Objects.equals(ControllerMCList.labelsSongNo.get(i).getText(), songID)) {

                ControllerMCList.labelsSongName.get(i).setText(trackName);
                ControllerMCList.labelsComposer.get(i).setText(composer);
                ControllerMCList.labelsLyricist.get(i).setText(lyricist);

                ControllerMCList.allManualClaims.get(i).setTrackName(trackName);
                ControllerMCList.allManualClaims.get(i).setComposer(composer);
                ControllerMCList.allManualClaims.get(i).setLyricist(lyricist);

                // Checking trim times
                if (trimStart != null && trimEnd != null && !trimStart.isEmpty() && !trimEnd.isEmpty()) {
                    // Validating trim times
                    boolean status = TextFormatter.validateTrimTimes(trimStart, trimEnd);
                    if (status) {
                        // Adding trim times to model
                        ControllerMCList.allManualClaims.get(i).setTrimStart(trimStart);
                        ControllerMCList.allManualClaims.get(i).setTrimEnd(trimEnd);
                        DatabasePostgres.editManualClaim(songID, trackName, composer, lyricist, trimStart, trimEnd);
                    } else {
                        claimValidation = false;
                        AlertBuilder.sendErrorAlert("Error", null, "Error Parsing Trim Time");
                    }
                } else {
                    DatabasePostgres.editManualClaim(songID, trackName, composer, lyricist, trimStart, trimEnd);
                }
            }
        }

        if (claimValidation) {
            UIController.blankSidePanel();
        }
    }

    @FXML
    public void onChangeImageClicked(MouseEvent event) throws IOException, SQLException {
        Scene scene = SceneController.getSceneFromEvent(event);
        File file = Main.browseForImage(scene.getWindow());
        if (file != null) {
            // Covert user input to a Java BufferedImage
            BufferedImage biArtwork = ImageIO.read(file);
            System.out.println("biArtwork = " + biArtwork.getColorModel());

            // Check image dimensions
            int imageWidth = biArtwork.getWidth();
            int imageHeight = biArtwork.getHeight();

            if (imageWidth > 1400 || imageHeight > 1400) {
                // Getting Claim ID
                String claimID = lblClaimID.getText();

                // Resize user input to preview size
                BufferedImage previewImage = ImageProcessor.resizeImage(210, 210, biArtwork);

                // Updating Database
                int status = DatabasePostgres.updateClaimArtwork(claimID, biArtwork, previewImage);

                if (status > 0) {
                    // Convert BufferedImage to JavaFX image and set it into user interface
                    Image image = SwingFXUtils.toFXImage(previewImage, null);
                    imgPreview.setImage(image);

                    for (int i = 0; i < ControllerMCList.labelsSongNo.size(); i++) {
                        if (Objects.equals(ControllerMCList.labelsSongNo.get(i).getText(), claimID)) {
                            ControllerMCList.ivArtworks.get(i).setImage(image);
                        }
                    }
                } else {
                    // TODO: 4/3/2024 Error Updating Database
                    NotificationBuilder.displayTrayError("Error Updating Artwork", "Database Malfunction");
                }
            } else {
                // TODO: 4/3/2024 Execute default functionality for smaller images
                NotificationBuilder.displayTrayError("Invalid Dimensions", "Image Dimensions are below 1400px");
            }

        }
    }

    public void onLinkClick() {
        String link = lblLink.getText();
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                URI uri = new URI(link);
                Desktop.getDesktop().browse(uri);
            }
        } catch (Exception e) {
            AlertBuilder.sendErrorAlert("Error", "Something went wrong when opening link", "Link: " + link + "\nException: " + e.getMessage());
        }
    }

    @FXML
    void onYoutubeRequested() {
        String claimID = lblClaimID.getText();

        try {
            int claimIDInt = Integer.parseInt(claimID);
            ManualClaimTrack track;

            track = DatabasePostgres.getManualClaim(claimIDInt);

            if (track != null) {
                String youtubeLink = getYT_LinkWithTrimStart(track);

                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    URI uri = new URI(youtubeLink);
                    Desktop.getDesktop().browse(uri);
                }
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error Fetching Manual Claim");
            alert.setContentText(e.toString());
            alert.showAndWait();
        } catch (URISyntaxException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error Loading Youtube Link");
            alert.setContentText(e.toString());
            alert.showAndWait();
        } catch (NumberFormatException ignore) {

        }

    }

    private static @NotNull String getYT_LinkWithTrimStart(ManualClaimTrack track) {
        String youtubeID = track.getYoutubeID();
        String trimStart = track.getTrimStart();

        // Split the time string into hours, minutes, and seconds
        if (trimStart != null) {
            String[] timeParts = trimStart.split(":");
            int hours = Integer.parseInt(timeParts[0]);
            int minutes = Integer.parseInt(timeParts[1]);
            int seconds = Integer.parseInt(timeParts[2]);

            // Assign to a new variable
            int trimStartInSeconds = hours * 3600 + minutes * 60 + seconds;

            return "https://youtube.com/watch?v=" + youtubeID + "&t=" + trimStartInSeconds;
        } else {
            return "https://youtube.com/watch?v=" + youtubeID;
        }
    }
}
