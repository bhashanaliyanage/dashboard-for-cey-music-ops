package com.example.song_finder_fx.UIControllers;

import com.example.song_finder_fx.Controller.SceneController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;

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
    public void initialize() {
        // System.out.println("Manual Claims List Entry " + lblSongNo.getText());
    }

    @FXML
    void onEdit(MouseEvent event) throws IOException {
        System.out.println("ControllerMCLEntry.onEdit");
        Scene scene = SceneController.getSceneFromEvent(event);
        VBox sideVBox = SceneController.getSideVBox(scene);
        Node layoutNode = SceneController.loadLayout("layouts/manual_claims/sidepanel-claim-edit.fxml");
        sideVBox.getChildren().setAll(layoutNode);

        Label lblClaimID = SceneController.getLabelFromScene(scene, "lblClaimID");
        Label lblTrackName = SceneController.getLabelFromScene(scene, "lblTrackName");
        TextField txtSongName = SceneController.getTextFieldFromScene(scene, "txtSongName");
        TextField txtComposer = SceneController.getTextFieldFromScene(scene, "txtComposer");
        TextField txtLyricist = SceneController.getTextFieldFromScene(scene, "txtLyricist");

        lblClaimID.setText(lblSongNo.getText());
        lblTrackName.setText(lblSongName.getText());
        txtSongName.setText(lblSongName.getText());
        txtComposer.setText(lblComposer.getText());
        txtLyricist.setText(lblLyricist.getText());
    }

    @FXML
    void onSongClick() {
        boolean isSelected = checkBox.isSelected();
        checkBox.setSelected(!isSelected);
    }

}
