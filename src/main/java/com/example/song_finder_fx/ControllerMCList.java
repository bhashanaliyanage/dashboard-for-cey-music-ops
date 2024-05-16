package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.SceneController;
import com.example.song_finder_fx.Controller.TextFormatter;
import com.example.song_finder_fx.Model.ManualClaimTrack;
import javafx.application.Platform;
import javafx.concurrent.Task;
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

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
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
        ivArtworks.clear();

        lblClaimCount.setText("Loading...");

        Task<Void> taskGetManualClaims = new Task<>() {
            @Override
            protected Void call() throws SQLException {
                manualClaims = DatabasePostgres.getManualClaims();

                int count = 0;
                for (ManualClaimTrack claim : manualClaims) {
                    count++;
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
                        Label lblDate = (Label) node.lookup("#lblDate");
                        Label lblClaimType = (Label) node.lookup("#lblClaimType");
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
                        lblDate.setText(TextFormatter.getDaysAgo(claim.getDate()));
                        lblDate.setStyle(setColor(claim.getDate()));
                        lblClaimType.setText(claim.getClaimTypeString());

                        int finalCount = count;
                        Platform.runLater(() -> {
                            vbClaimsList.getChildren().add(node);
                            lblClaimCount.setText(String.valueOf(finalCount));
                        });
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }

                Platform.runLater(() -> {
                    try {
                        lblClaimCount.setText(DatabasePostgres.getManualClaimCount());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
                return null;
            }
        };

        Thread threadGetManualClaims = new Thread(taskGetManualClaims);
        threadGetManualClaims.start();



        /*Task<Void> taskLoadClaims = new Task<>() {
            @Override
            protected Void call() {

                return null;
            }
        };

        Thread thread = new Thread(taskLoadClaims);
        // thread.start();*/

    }

    private String setColor(LocalDate localDate) {
        // Get the current system date
        LocalDate currentDate = LocalDate.now();

        // Calculate the difference in days
        long daysDifference = ChronoUnit.DAYS.between(localDate, currentDate);

        // Determine the appropriate label based on the difference
        String label;
        if (daysDifference == 0) {
            label = "-fx-text-fill: #72a276";
        } else if (daysDifference == 1) {
            // #F28F3B
            label = "-fx-text-fill: #F28F3B";
        } else {
            // #A72608
            label = "-fx-text-fill: #A72608";
        }
        return label;
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
