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
import org.w3c.dom.Text;

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
    void onEdit(MouseEvent event) throws IOException {
        System.out.println("ControllerMCLEntry.onEdit");
        Scene scene = SceneController.getSceneFromEvent(event);
        VBox sideVBox = SceneController.getSideVBox(scene);
        Node layoutNode = SceneController.loadLayout("layouts/manual_claims/manual-claims-track.fxml");
        sideVBox.getChildren().setAll(layoutNode);

        Label lblClaimID = SceneController.getLabelFromScene(scene, "lblClaimID");
        Label lblSongName = SceneController.getLabelFromScene(scene, "lblSongName");
        TextField txtSongName = SceneController.getTextFieldFromScene(scene, "txtSongName");
        TextField txtComposer = SceneController.getTextFieldFromScene(scene, "txtComposer");
        TextField txtLyricist = SceneController.getTextFieldFromScene(scene, "txtLyricist");
    }

}
