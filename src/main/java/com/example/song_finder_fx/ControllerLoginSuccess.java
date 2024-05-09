package com.example.song_finder_fx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

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
        btnProfile.setText("Still under development");
    }
}
