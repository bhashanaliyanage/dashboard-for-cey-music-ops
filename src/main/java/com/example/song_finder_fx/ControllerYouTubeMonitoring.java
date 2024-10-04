package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.*;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
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
    void onAddNewReport() {

    }

    private void listChannels() {
        try {
            List<List<Map<String, String>>> list = YoutubeDownload.getTypeTvProgramLlist();
            list.addAll(YoutubeDownload.getProgramListByChannel());

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
                    // String url = map.get("Url");
                    String thumbnail = map.get("Thumbnail");
                    String releaseDate = map.get("releaseDate");
                    String ytLink = map.get("Url");

                    System.out.println("title = " + title);
                    BufferedImage thumbnailImage = ImageProcessor.getDownloadedImage(thumbnail);
                    Image convertedImage = SwingFXUtils.toFXImage(thumbnailImage, null);

                    Node node = SceneController.loadLayout("layouts/youtube_monitoring/youtube_monitoring_entry.fxml");
                    // ImageView imgTitle = (ImageView) node.lookup("#imgTitle");
                    ImageView imgThumb = (ImageView) node.lookup("#imgThumb");
                    Label lblVideoTitle = (Label) node.lookup("#lblVideoTitle");
                    Label lblCName = (Label) node.lookup("#lblCName");
                    Label lblDate = (Label) node.lookup("#lblDate");
                    Label lblViewCount = (Label) node.lookup("#lblViewCount");
                    Label lblYTLink = (Label) node.lookup("#lblYTLink");

                    Platform.runLater(() -> {
                        lblVideoTitle.setText(title);
                        // imgTitle.setImage(Test.createImageFromText(title, "Iskoola Potha"));
                        lblCName.setText("Channel/ Program: " + channelName); // TODO: Add Channel Name
                        lblDate.setText(releaseDate);
                        lblViewCount.setText("Views: N/A");
                        lblYTLink.setText(ytLink);
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

}
