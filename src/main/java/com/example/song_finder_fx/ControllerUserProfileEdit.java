package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Session.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.Objects;

public class ControllerUserProfileEdit {

    @FXML
    private Label lblTrackName;

    @FXML
    private PasswordField pfNew;

    @FXML
    private PasswordField pfOld;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtNickname;

    @FXML
    private TextField txtUsername;

    private UserSession session;

    @FXML
    void initialize() {
        session = Main.userSession;
        txtNickname.setText(session.getNickName());
        txtUsername.setText(session.getUserName());
        txtEmail.setText(session.getEmail());
    }

    @FXML
    void onSave(ActionEvent event) {
        String nickName = txtNickname.getText();
        String username = txtUsername.getText();
        String email = txtEmail.getText();
        String currentPassword = pfOld.getText();
        String newPassword = pfNew.getText();

        try {
            if (!Objects.equals(nickName, session.getNickName())) {
                if (Main.userSession.changeNickName(nickName)) {
                    ControllerProfile.lblNicknameStatic.setText(nickName);
                    ControllerProfile.lblUsernameTopStatic.setText(nickName);
                }
            }

            /*if (!Objects.equals(username, session.getUserName())) {
                Main.userSession.changeUsername(username);
                ControllerProfile.lblUsernameStatic.setText(username);
            }*/
        } catch (Exception e) {
            AlertBuilder.sendErrorAlert("Error", "Error Updating Values", e.toString());
        }

        /*if (!nickName.isEmpty()) {
        }*/

    }

}
