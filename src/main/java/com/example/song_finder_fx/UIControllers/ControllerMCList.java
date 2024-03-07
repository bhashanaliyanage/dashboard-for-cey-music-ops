package com.example.song_finder_fx.UIControllers;

import com.example.song_finder_fx.ControllerSettings;
import com.example.song_finder_fx.DatabasePostgre;
import com.example.song_finder_fx.Model.ManualClaimTrack;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
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

    @FXML
    public void initialize() throws SQLException, IOException {
        lblClaimCount.setText(DatabasePostgre.getManualClaimCount());

        List<ManualClaimTrack> manualClaims = DatabasePostgre.getManualClaims();

        for (ManualClaimTrack claim : manualClaims) {
            Node node = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../layouts/manual_claims/manual-claims-list-entry.fxml")));
            Label lblSongNo = (Label) node.lookup("#lblSongNo");
            Label lblSongName = (Label) node.lookup("#lblSongName");
            Label lblComposer = (Label) node.lookup("#lblComposer");
            Label lblLyricist = (Label) node.lookup("#lblLyricist");

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
    void onArchiveSelected(ActionEvent event) {
        // TODO: 3/7/2024 Implement method to loop through CheckBoxes and HBoxes
    }

}
