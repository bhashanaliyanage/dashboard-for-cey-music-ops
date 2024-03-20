package com.example.song_finder_fx.UIControllers;

import com.example.song_finder_fx.Controller.SceneController;
import com.example.song_finder_fx.ControllerSettings;
import com.example.song_finder_fx.DatabasePostgres;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class ControllerMCList {

    @FXML
    private Label lblClaimCount;

    @FXML
    private VBox vbClaimsList;

    public static List<CheckBox> checkBoxes = new ArrayList<>();

    public static List<HBox> hBoxes = new ArrayList<>();

    public static List<Label> labelsSongNo = new ArrayList<>();

    public static List<Label> labelsSongName = new ArrayList<>();

    public static List<Label> labelsComposer = new ArrayList<>();

    public static List<Label> labelsLyricist = new ArrayList<>();

    public static List<ManualClaimTrack> manualClaims = new ArrayList<>();

    public static List<ManualClaimTrack> finalManualClaims = new ArrayList<>();

    public static Map<Integer, ManualClaimTrack> claimMap = new HashMap<>();

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


        /*for (ManualClaimTrack claim : manualClaims) {
            claimMap.put(claim.getId(), claim);
        }*/

        for (ManualClaimTrack claim : manualClaims) {
            claimMap.put(claim.getId(), claim);

            Node node = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../layouts/manual_claims/manual-claims-list-entry.fxml")));

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

            lblSongNo.setText(String.valueOf(claim.getId()));
            lblSongName.setText(claim.getTrackName());
            lblComposer.setText(claim.getComposer());
            lblLyricist.setText(claim.getLyricist());

            vbClaimsList.getChildren().add(node);
        }
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
                finalManualClaims.add(claim);
            }
        }

        Node node = SceneController.loadLayout("layouts/manual_claims/manual-claims-identifiers.fxml");
        Scene scene = SceneController.getSceneFromEvent(event);
        VBox main = SceneController.getMainVBox(scene);
        main.getChildren().setAll(node);

        // CSV Process
        /*Path tempDir = Files.createTempDirectory("ingest");
        Path csvFile = tempDir.resolve("ingest.csv");
        CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFile.toFile()));
        String[] header = {"//Field name:", "Album title", "Album version", "UPC", "Catalog number",
                "Primary artists", "Featuring artists", "Release date", "Main genre",
                "Main subgenre", "Alternate genre", "Alternate subgenre", "Label",
                "CLine year", "CLine name", "PLine year", "PLine name", "Parental advisory",
                "Recording year", "Recording location", "Album format", "Number of volumes",
                "Territories", "Excluded territories", "Language (Metadata)", "Catalog tier",
                "Track title", "Track version", "ISRC", "Track primary artists",
                "Track featuring artists", "Volume number", "Track main genre",
                "Track main subgenre", "Track alternate genre", "Track alternate subgenre",
                "Track language (Metadata)", "Audio language", "Lyrics", "Available separately",
                "Track parental advisory", "Preview start", "Preview length",
                "Track recording year", "Track recording location", "Contributing artists",
                "Composers", "Lyricists", "Remixers", "Performers", "Producers",
                "Writers", "Publishers", "Track sequence", "Track catalog tier",
                "Original file name", "Original release date", "Movement title",
                "Classical key", "Classical work", "Always send display title",
                "Movement number", "Classical catalog"};

        csvWriter.writeNext(header);

        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                System.out.println("labelsSongName = " + labelsSongName.get(i).getText());
            }
        }

        csvWriter.close();

        System.out.println("csvFile = " + csvFile);*/
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