package com.example.song_finder_fx.UIControllers;

import com.example.song_finder_fx.ControllerSettings;
import com.example.song_finder_fx.DatabasePostgre;
import com.example.song_finder_fx.Model.ManualClaimTrack;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ControllerMCList {

    @FXML
    private Label lblClaimCount;

    @FXML
    private VBox vbClaimsList;

    @FXML
    private ScrollPane scrlpneClaimsList;

    public static List<CheckBox> checkBoxes = new ArrayList<>();

    public static List<HBox> hBoxes = new ArrayList<>();

    public static List<Label> labelsSongNo = new ArrayList<>();

    public static List<Label> labelsSongName = new ArrayList<>();

    public static List<Label> labelsComposer = new ArrayList<>();

    public static List<Label> labelsLyricist = new ArrayList<>();

    @FXML
    public void initialize() throws SQLException, IOException {
        lblClaimCount.setText(DatabasePostgre.getManualClaimCount());

        List<ManualClaimTrack> manualClaims = DatabasePostgre.getManualClaims();

        for (ManualClaimTrack claim : manualClaims) {
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
    void onCheck() {
        for (CheckBox checkBox : checkBoxes) {
            System.out.println(checkBox.isSelected());
        }
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
                    DatabasePostgre.archiveSelectedClaim(songNo);
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
