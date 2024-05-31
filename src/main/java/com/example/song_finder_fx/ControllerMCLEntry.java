package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.SceneController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;

public class ControllerMCLEntry {

    @FXML
    private Label lblComposer;

    @FXML
    private Label lblLyricist;

    @FXML
    private Label lblSongName;

    @FXML
    private Label lblSongNo;

    @FXML
    private CheckBox checkBox;

    @FXML
    private ImageView image;

    @FXML
    public void initialize() {
        // System.out.println("Manual Claims List Entry " + lblSongNo.getText());
    }

    @FXML
    void onEdit(MouseEvent event) throws IOException, SQLException {
        System.out.println("ControllerMCLEntry.onEdit");
        Scene scene = SceneController.getSceneFromEvent(event);
        VBox sideVBox = SceneController.getSideVBox(scene);
        Node layoutNode = SceneController.loadLayout("layouts/manual_claims/sidepanel-claim-edit.fxml");
        sideVBox.getChildren().clear();
        sideVBox.getChildren().add(layoutNode);

        Label lblClaimID = SceneController.getLabelFromScene(scene, "lblClaimID");
        Label lblTrackName = SceneController.getLabelFromScene(scene, "lblTrackName");
        Label lblLink = SceneController.getLabelFromScene(scene, "lblLink");
        TextField txtSongName = SceneController.getTextFieldFromScene(scene, "txtSongName");
        TextField txtComposer = SceneController.getTextFieldFromScene(scene, "txtComposer");
        TextField txtLyricist = SceneController.getTextFieldFromScene(scene, "txtLyricist");
        ImageView imgPreview = (ImageView) scene.lookup("#imgPreview");

        int claimID = Integer.parseInt(lblSongNo.getText());
        String youtubeID = DatabasePostgres.getClaimYouTubeID(claimID);
        String thumbnailURL = "https://i.ytimg.com/vi/" + youtubeID + "/maxresdefault.jpg";

        imgPreview.setImage(image.getImage());
        lblClaimID.setText(lblSongNo.getText());
        lblTrackName.setText(lblSongName.getText());
        txtSongName.setText(lblSongName.getText());
        txtComposer.setText(lblComposer.getText());
        txtLyricist.setText(lblLyricist.getText());
        lblLink.setText(thumbnailURL);
    }

    @FXML
    void onSongClick() {
        boolean isSelected = checkBox.isSelected();
        checkBox.setSelected(!isSelected);
    }

}
