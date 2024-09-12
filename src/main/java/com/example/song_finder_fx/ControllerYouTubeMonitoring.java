package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.*;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ControllerYouTubeMonitoring {

    @FXML
    private Label lblUploadCount;

    @FXML
    private VBox vbReportsList;

    @FXML
    void initialize() {
        Thread thread = new Thread(this::listChannels);
        thread.start();
    }

    @FXML
    void onAddNewReport(ActionEvent event) {

    }

    // For Testing Purposes
    /*public static void main(String[] args) {
        listChannels();
    }*/

    private void listChannels() {
        try {
            List<List<Map<String, String>>> list = YoutubeDownload.getTypeTvProgramLlist();

            int totalUploads = 0;
            Set<String> uniqueChannels = new HashSet<>();

            Platform.runLater(() -> vbReportsList.getChildren().clear());

            for (List<Map<String, String>> list1 : list) {
                for (Map<String, String> map : list1) {
                    // Fetching Details
                    String channelName = map.get("channelName");
                    uniqueChannels.add(channelName);
                    totalUploads++;

                    String title = map.get("Title");
                    String url = map.get("Url");
                    String thumbnail = map.get("Thumbnail");
                    String releaseDate = map.get("releaseDate");

                    System.out.println("title = " + title);
                    String cleanedTitle = cleanAndFormatTitle(title);
                    BufferedImage thumbnailImage = ImageProcessor.getDownloadedImage(thumbnail);
                    Image convertedImage = SwingFXUtils.toFXImage(thumbnailImage, null);

                    Node node = SceneController.loadLayout("layouts/youtube_monitoring/youtube_monitoring_entry.fxml");
                    ImageView imgThumb = (ImageView) node.lookup("#imgThumb");
                    Label lblVideoTitle = (Label) node.lookup("#lblVideoTitle");
                    Label lblCName = (Label) node.lookup("#lblCName");
                    Label lblDate = (Label) node.lookup("#lblDate");
                    Label lblViewCount = (Label) node.lookup("#lblViewCount");

                    Platform.runLater(() -> {
                        lblVideoTitle.setText(cleanedTitle);
                        lblCName.setText("Channel: Siyatha"); // TODO: Add Channel Name
                        lblDate.setText(releaseDate);
                        lblViewCount.setText("Views: N/A");
                        imgThumb.setImage(convertedImage);
                        vbReportsList.getChildren().add(node);
                    });
                }
            }

            int finalTotalUploads = totalUploads;
            String outText = finalTotalUploads + " Uploads from " + uniqueChannels.size() + " YouTube Channels are Available";
            Platform.runLater(() -> {
                lblUploadCount.setText(outText);
                System.out.println(outText);
            });
        } catch (IOException e) {
            Platform.runLater(() -> AlertBuilder.sendErrorAlert("Error", "Something went wrong while trying to load the YouTube Monitoring View.", e.toString()));
        } catch (URISyntaxException e) {
            Platform.runLater(() -> NotificationBuilder.displayTrayError("Error", "Something went wrong when fetching YouTube Thumbnail"));
        }
    }

    public static String cleanAndFormatTitle(String input) {
        if (input == null) {
            return null;
        }
        // Keep only English letters, numbers, and specific punctuation
        String cleanedTitle = input.replaceAll("[^a-zA-Z0-9\\s\\-|:]", "");

        // Trim leading and trailing spaces
        cleanedTitle = cleanedTitle.trim();

        // Replace multiple spaces with a single space
        cleanedTitle = cleanedTitle.replaceAll("\\s+", " ");

        return cleanedTitle;
    }

    private String removeUnsupportedCharacters(String input) {
        // Use StringBuilder for efficient string manipulation
        StringBuilder cleaned = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            // Check if the character is within the Basic Multilingual Plane (BMP)
            if (Character.isDefined(ch) && !Character.isISOControl(ch)) {
                cleaned.append(ch); // Append supported characters
            }
        }

        return cleaned.toString();
    }
}
