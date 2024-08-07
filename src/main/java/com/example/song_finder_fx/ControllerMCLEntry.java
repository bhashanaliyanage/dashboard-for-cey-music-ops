package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.SceneController;
import com.example.song_finder_fx.Model.ManualClaimTrack;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import javax.xml.crypto.Data;
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

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Label lblClaimID = SceneController.getLabelFromScene(scene, "lblClaimID");
                Label lblTrackName = SceneController.getLabelFromScene(scene, "lblTrackName");
                Label lblLink = SceneController.getLabelFromScene(scene, "lblLink");
                ImageView imgPreview = (ImageView) scene.lookup("#imgPreview");
                TextField txtSongName = SceneController.getTextFieldFromScene(scene, "txtSongName");
                TextField txtComposer = SceneController.getTextFieldFromScene(scene, "txtComposer");
                TextField txtLyricist = SceneController.getTextFieldFromScene(scene, "txtLyricist");
                TextField txtStartTime = SceneController.getTextFieldFromScene(scene, "txtStartTime");
                TextField txtEndTime = SceneController.getTextFieldFromScene(scene, "txtEndTime");

                int claimID = Integer.parseInt(lblSongNo.getText());
                ManualClaimTrack track = DatabasePostgres.getManualClaim(claimID);
                String youtubeID = track.getYoutubeID();
                String thumbnailURL = "https://i.ytimg.com/vi/" + youtubeID + "/maxresdefault.jpg";

                Platform.runLater(() -> {
                    imgPreview.setImage(image.getImage());
                    lblClaimID.setText(lblSongNo.getText());
                    lblTrackName.setText(lblSongName.getText());
                    txtSongName.setText(lblSongName.getText());
                    txtComposer.setText(lblComposer.getText());
                    txtLyricist.setText(lblLyricist.getText());
                    lblLink.setText(thumbnailURL);

                    System.out.println("Trim Start: " + track.getTrimStart());
                    System.out.println("Trim End: " + track.getTrimEnd());

                    txtStartTime.setText(track.getTrimStart());
                    txtEndTime.setText(track.getTrimEnd());
                });

                return null;
            }
        };

        task.setOnFailed(e -> {
            Throwable exception = task.getException();
            Platform.runLater(() -> AlertBuilder.sendErrorAlert("Error", "Error Loading Claim", exception.toString()));
        });

        Thread thread = new Thread(task);
        thread.start();
    }

    @FXML
    void onSongClick() {
        boolean isSelected = checkBox.isSelected();
        checkBox.setSelected(!isSelected);
    }

}
