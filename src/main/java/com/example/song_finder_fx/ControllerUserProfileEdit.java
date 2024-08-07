package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.NotificationBuilder;
import com.example.song_finder_fx.Controller.SceneController;
import com.example.song_finder_fx.Session.Hasher;
import com.example.song_finder_fx.Session.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.Objects;

public class ControllerUserProfileEdit {

    @FXML
    private Label lblTrackName;

    @FXML
    private PasswordField pfNew;

    @FXML
    private PasswordField pfNewReEnter;

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
        String newPasswordReEntered = pfNewReEnter.getText();

        try {
            // Nickname
            if (!Objects.equals(nickName, session.getNickName())) {
                if (Main.userSession.changeNickName(nickName)) {
                    ControllerProfile.lblNicknameStatic.setText(nickName);
                    ControllerProfile.lblUsernameTopStatic.setText(nickName);
                }
            }

            // Username
            if (!Objects.equals(username, session.getUserName())) {
                if (Main.userSession.changeUsername(username)) {
                    ControllerProfile.lblUsernameStatic.setText(username);
                }
            }

            // Email
            if (!Objects.equals(email, session.getEmail())) {
                if (Main.userSession.changeEmail(email)) {
                    ControllerProfile.lblEmailStatic.setText(email);
                }
            }

            // Password
            if (currentPasswordIsCorrect(currentPassword)) {
                pfOld.setStyle("-fx-border-color: '#e9ebee';");

                if (!newPassword.isEmpty() && !newPasswordReEntered.isEmpty()) {
                    if (Objects.equals(newPassword, newPasswordReEntered)) {
                        boolean status = DatabasePostgres.changePassword(Main.userSession.getUserName(), newPassword);
                        if (status) {
                            pfNew.setStyle("-fx-border-color: '#e9ebee';");
                            pfNewReEnter.setStyle("-fx-border-color: '#e9ebee';");
                            NotificationBuilder.displayTrayInfo("Password Changed", "Password changed for user: " + Main.userSession.getUserName());
                        } else {
                            NotificationBuilder.displayTrayError("Unable to change password", "Unable to change password for user: " + Main.userSession.getUserName());
                        }

                    } else {
                        pfNew.setStyle("-fx-border-color: red;");
                        pfNewReEnter.setStyle("-fx-border-color: red;");
                        NotificationBuilder.displayTrayError("Passwords not matched", "Please re-check the new password you entered");
                    }
                } else {
                    pfNew.setStyle("-fx-border-color: red;");
                    pfNewReEnter.setStyle("-fx-border-color: red;");
                    NotificationBuilder.displayTrayError("Passwords not matched", "One or more password fields are empty");
                }

            } else {
                pfOld.setStyle("-fx-border-color: red;");
            }

            // Clear SidePanel
            Node node2 = SceneController.loadLayout("layouts/sidepanel-blank.fxml");
            UIController.sideVBoxStatic.getChildren().clear();
            UIController.sideVBoxStatic.getChildren().add(node2);

        } catch (Exception e) {
            AlertBuilder.sendErrorAlert("Error", "Error Updating Values", e.toString());
        }
    }

    private boolean currentPasswordIsCorrect(String currentPassword) throws SQLException {
        Hasher hasher = new Hasher(Main.userSession.getUserName(), currentPassword);
        return hasher.validate();
    }

}
