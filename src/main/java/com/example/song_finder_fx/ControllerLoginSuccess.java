package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.SceneController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class ControllerLoginSuccess {
    @FXML
    private Label lblUsername;

    @FXML
    private Button btnProfile;

    @FXML
    public void initialize() {
        String nickname = Main.userSession.getNickName();
        lblUsername.setText(nickname);
    }

    @FXML
    void onGoToProfile(MouseEvent event) {
        try {
            Node node = SceneController.loadLayout("layouts/user/profile.fxml");
            UIController.mainVBoxStatic.getChildren().setAll(node);
        } catch (IOException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Initializing UI", e.toString());
        }
    }
}
