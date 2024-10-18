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
import java.util.*;

public class ControllerYouTubeMonitoring {

    @FXML
    private Label lblUploadCount;

    public static Label lblUploadCountStatic;

    @FXML
    private VBox vbReportsList;

    public static VBox vbReportsListStatic;

    private final List<Node> allEntries = new ArrayList<>();

    public static List<Node> allEntriesStatic = new ArrayList<>();

    private static final int pageSize = 30;

    private static int currentPage = 0;

    @FXML
    private Label lblPaginationInfo;

    public static Label lblPaginationInfoStatic;

    public static Map<String, Integer> channelStats;

    @FXML
    void initialize() {
        vbReportsListStatic = vbReportsList;
        allEntriesStatic = allEntries;
        lblUploadCountStatic = lblUploadCount;
        lblPaginationInfoStatic = lblPaginationInfo;

        Thread thread = new Thread(() -> ControllerYouTubeMonitoring.listChannels("Loading"));
        thread.start();
    }

    @FXML
    void onAddNewReport() {
        try {
            Node node = SceneController.loadLayout("layouts/youtube_monitoring/ytm_sidepanel_add_channel.fxml");
            UIController.sideVBoxStatic.getChildren().clear();
            UIController.sideVBoxStatic.getChildren().add(node);
        } catch (IOException e) {
            AlertBuilder.sendErrorAlert("Error", "Something went wrong when loading UI", e.toString());
            System.out.println("Error Loading UI: " + e);
        }
    }

    public static void listChannels(String text) {
        try {
            Platform.runLater(() -> lblUploadCountStatic.setText(text));
            // Fetching Details
            List<List<Map<String, String>>> list = YoutubeDownload.getTypeTvProgramLlist();
            list.addAll(YoutubeDownload.getProgramListByChannel());

            channelStats = new HashMap<>();
            int totalUploads = 0;

            Set<String> uniqueChannels = new HashSet<>();

            Platform.runLater(() -> vbReportsListStatic.getChildren().clear());

            // Implementing Pagination
            for (List<Map<String, String>> list1 : list) {
                for (Map<String, String> map : list1) {
                    // Fetching Details
                    String channelName = map.get("channelName");
                    channelStats.merge(channelName, 1, Integer::sum);
                    totalUploads++;

                    uniqueChannels.add(channelName);

                    String title = map.get("Title");
                    String thumbnail = map.get("Thumbnail");
                    String releaseDate = map.get("releaseDate");
                    String ytLink = map.get("Url");
                    String viewCount = map.get("ViewCount");
                    String label = map.get("Label");

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
                        lblCName.setText("Channel/ Program: " + channelName);
                        lblDate.setText(releaseDate);
                        lblViewCount.setText("Views: " + viewCount);
                        lblYTLink.setText(ytLink);
                        imgThumb.setImage(convertedImage);
                        allEntriesStatic.add(node);
                        System.out.println(label);
                        // vbReportsList.getChildren().add(node);
                    });
                }
            }

            displayCurrentPage();

            int finalTotalUploads = totalUploads;
            String outText = finalTotalUploads + " Uploads from " + uniqueChannels.size() + " YouTube Channels are Available";
            Platform.runLater(() -> {
                lblUploadCountStatic.setText(outText);
                System.out.println(outText);

                loadSidePanel();

                channelStats.forEach((channel, count) -> {
                    String channelLabel = channel + ": " + count + " videos";
                    System.out.println(channelLabel);
                });
            });
        } catch (IOException e) {
            Platform.runLater(() -> AlertBuilder.sendErrorAlert("Error", "Something went wrong while trying to load the YouTube Monitoring View.", e.toString()));
        } catch (URISyntaxException e) {
            Platform.runLater(() -> NotificationBuilder.displayTrayError("Error", "Something went wrong when fetching YouTube Thumbnail"));
        }
    }

    private static void loadSidePanel() {

    }

    private static void displayCurrentPage() {
        int startIndex = currentPage * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allEntriesStatic.size());

        Platform.runLater(() -> vbReportsListStatic.getChildren().clear());
        for (int i = startIndex; i < endIndex; i++) {
            int finalI = i;
            Platform.runLater(() -> vbReportsListStatic.getChildren().add(allEntriesStatic.get(finalI)));
            // vbReportsList.getChildren().add(allEntries.get(i));
        }

        int displayStart = startIndex + 1;
        int totalUploads = allEntriesStatic.size();

        int currentPageNumber = currentPage + 1;
        int totalPages = (int) Math.ceil(totalUploads / (double) pageSize);
        String paginationInfo = String.format("Page %d of %d (%d-%d of %d claims)",
                currentPageNumber, totalPages,
                displayStart, endIndex, totalUploads);

        // TODO: Add Pagination Info Label
        Platform.runLater(() -> {
            System.out.println(paginationInfo);
            lblPaginationInfoStatic.setText(paginationInfo);
        });


    }

    @FXML
    void onBackPage() {
        previousPage();
    }

    private void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            displayCurrentPage();
        }
    }

    @FXML
    void onNextPage() {
        nextPage();
    }

    private void nextPage() {
        if ((currentPage + 1) * pageSize < allEntries.size()) {
            currentPage++;
            displayCurrentPage();
        }
    }

}
