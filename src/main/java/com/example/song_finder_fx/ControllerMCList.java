package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.ImageProcessor;
import com.example.song_finder_fx.Controller.SceneController;
import com.example.song_finder_fx.Model.ManualClaimTrack;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ControllerMCList {

    @FXML
    private Label lblClaimCount;

    @FXML
    private VBox vbClaimsList;

    public static List<CheckBox> checkBoxes = new ArrayList<>();

    public static List<HBox> hBoxes = new ArrayList<>();

    public static List<Label> labelsSongNo = new ArrayList<>();

    public static List<Label> labelsSongName = new ArrayList<>();

    public static List<ImageView> ivArtworks = new ArrayList<>();

    public static List<Label> labelsComposer = new ArrayList<>();

    public static List<Label> labelsLyricist = new ArrayList<>();

    public static List<ManualClaimTrack> manualClaims = new ArrayList<>();

    public static List<ManualClaimTrack> finalManualClaims = new ArrayList<>();

    public static Map<Integer, ManualClaimTrack> claimMap = new HashMap<>();

    private final Executor imageDownloadExecutor = Executors.newFixedThreadPool(5);

    @FXML
    public void initialize() throws SQLException, IOException {
        checkBoxes.clear();
        hBoxes.clear();
        labelsSongNo.clear();
        labelsSongName.clear();
        labelsComposer.clear();
        labelsLyricist.clear();

        lblClaimCount.setText(DatabasePostgres.getManualClaimCount());

        manualClaims = DatabasePostgres.getManualClaims();

        /*List<CompletableFuture<Void>> downloadFutures = manualClaims.stream()
                .map(this::downloadImageAsync)
                .toList();

        CompletableFuture<Void> allDownloads = CompletableFuture.allOf(downloadFutures.toArray(new CompletableFuture[0]));

        allDownloads.thenRun(() -> {
            // All images downloaded, update UI
            Platform.runLater(this::updateUI);
        });*/

        for (ManualClaimTrack claim : manualClaims) {
            claimMap.put(claim.getId(), claim);

            Node node;
            try {
                node = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("layouts/manual_claims/manual-claims-list-entry.fxml")));
                Label lblSongNo = (Label) node.lookup("#lblSongNo");
                labelsSongNo.add(lblSongNo);
                Label lblSongName = (Label) node.lookup("#lblSongName");
                labelsSongName.add(lblSongName);
                Label lblComposer = (Label) node.lookup("#lblComposer");
                labelsComposer.add(lblComposer);
                Label lblLyricist = (Label) node.lookup("#lblLyricist");
                labelsLyricist.add(lblLyricist);
                CheckBox checkBox = (CheckBox) node.lookup("#checkBox");
                checkBoxes.add(checkBox);
                HBox hboxEntry = (HBox) node.lookup("#hboxEntry");
                hBoxes.add(hboxEntry);
                ImageView image = (ImageView) node.lookup("#image");
                ivArtworks.add(image);
                try {
                    image.setImage(claim.getPreviewImage());
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Error Loading Preview Image");
                    alert.setContentText(String.valueOf(e));
                    Platform.runLater(alert::showAndWait);
                }

                lblSongNo.setText(String.valueOf(claim.getId()));
                lblSongName.setText(claim.getTrackName());
                lblComposer.setText(claim.getComposer());
                lblLyricist.setText(claim.getLyricist());

                vbClaimsList.getChildren().add(node);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private void updateUI() {
        for (ManualClaimTrack claim : manualClaims) {
            claimMap.put(claim.getId(), claim);

            Node node;
            try {
                node = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../layouts/manual_claims/manual-claims-list-entry.fxml")));
                Label lblSongNo = (Label) node.lookup("#lblSongNo");
                labelsSongNo.add(lblSongNo);
                Label lblSongName = (Label) node.lookup("#lblSongName");
                labelsSongName.add(lblSongName);
                Label lblComposer = (Label) node.lookup("#lblComposer");
                labelsComposer.add(lblComposer);
                Label lblLyricist = (Label) node.lookup("#lblLyricist");
                labelsLyricist.add(lblLyricist);
                CheckBox checkBox = (CheckBox) node.lookup("#checkBox");
                checkBoxes.add(checkBox);
                HBox hboxEntry = (HBox) node.lookup("#hboxEntry");
                hBoxes.add(hboxEntry);
                ImageView image = (ImageView) node.lookup("#image");
                image.setImage(claim.getImage());

                lblSongNo.setText(String.valueOf(claim.getId()));
                lblSongName.setText(claim.getTrackName());
                lblComposer.setText(claim.getComposer());
                lblLyricist.setText(claim.getLyricist());

                vbClaimsList.getChildren().add(node);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private CompletableFuture<Void> downloadImageAsync(ManualClaimTrack claim) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String youtubeID = claim.getYoutubeID();
                String url = "https://i.ytimg.com/vi/" + youtubeID + "/maxresdefault.jpg";
                BufferedImage image = ImageProcessor.getDownloadedImage(url);
                image = ImageProcessor.cropImage(image);
                image = ImageProcessor.resizeImage(90, 90, image);
                claim.setImage(image); // Store the image in your claim object
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("An error occurred");
                alert.setContentText(String.valueOf(e));
                Platform.runLater(alert::showAndWait);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            return null;
        }, imageDownloadExecutor);
    }

    @FXML
    void onGoBack(MouseEvent event) throws IOException {
        Node node = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/manual_claims/manual-claims-main.fxml")));
        Scene scene = ((Node) event.getSource()).getScene();
        VBox mainVBox = (VBox) scene.lookup("#mainVBox");
        mainVBox.getChildren().setAll(node);
    }

    @FXML
    void onCheck(ActionEvent event) throws IOException {
        finalManualClaims.clear();
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                // ID, Name, Composer, Lyricist
                int id = manualClaims.get(i).getId();
                ManualClaimTrack claim = claimMap.get(id);
                System.out.println("claim.getBufferedImage() = " + manualClaims.get(i).getBufferedImage());
                finalManualClaims.add(claim);
                // finalSongNames.add(labelsSongName.get(i).getText());
            }
        }

        Node node = SceneController.loadLayout("layouts/manual_claims/manual-claims-identifiers.fxml");
        Scene scene = SceneController.getSceneFromEvent(event);
        VBox main = SceneController.getMainVBox(scene);
        main.getChildren().setAll(node);
    }

    @FXML
    void onSelectNone() {
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setSelected(false);
        }
    }

    @FXML
    void onSelectAll() {
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setSelected(true);
        }
    }

    @FXML
    void onArchiveSelected() {
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                String songNo = labelsSongNo.get(i).getText();
                try {
                    DatabasePostgres.archiveSelectedClaim(songNo);
                    hBoxes.get(i).setDisable(true);
                } catch (SQLException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("An error occurred");
                    alert.setContentText(String.valueOf(e));
                    Platform.runLater(alert::showAndWait);

                    // e.printStackTrace();
                }
            }
        }
    }

}
