package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.NotificationBuilder;
import com.example.song_finder_fx.Controller.SceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;

public class ControllerSignupProfileSetup {

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtNickname;

    private String username;

    private String password;

    private String email;

    private String nickname;

    @FXML
    void initialize() {
        username = ControllerSignUp.txtUsernameStatic.getText();
        password = ControllerSignUp.passwordFieldStatic.getText();
    }

    @FXML
    void onCompleteSignUp(ActionEvent event) {
        email = txtEmail.getText();
        nickname = txtNickname.getText();

        if (!nickname.isEmpty()) {
            txtNickname.setStyle("-fx-border-color: '#e9ebee';");

            if (!email.isEmpty()) {
                if (emailValid(email)) {
                    txtEmail.setStyle("-fx-border-color: '#e9ebee';");
                    // Create User
                    System.out.println("Create User");
                    try {
                        Main.userSession.signup(username, password, email, nickname);

                        Node node = SceneController.loadLayout("layouts/user/signup-final.fxml");
                        ControllerLogIn.vBoxLogInStatic.getChildren().clear();
                        ControllerLogIn.vBoxLogInStatic.getChildren().add(node);

                        UIController.lblUserStatic.setText(nickname);
                        UIController.lblUserEmailAndUpdateStatic.setText(email);

                        UIController.loadUser();
                    } catch (SQLException e) {
                        AlertBuilder.sendErrorAlert("Error", "Error Creating User", "Username: " + username +
                                "\nEmail: " + email +
                                "\nNickname: " + nickname +
                                "\n\nStacktrace: " + e);
                    } catch (IOException e) {
                        AlertBuilder.sendErrorAlert("Error", "Error Initializing UI", e.toString());
                    }
                } else {
                    txtEmail.setStyle("-fx-border-color: red;");
                    try {
                        NotificationBuilder.displayTrayError("Invalid Email", "Please enter a valid email address");
                    } catch (AWTException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                txtEmail.setStyle("-fx-border-color: red;");
            }
        } else {
            txtNickname.setStyle("-fx-border-color: red;");
        }
    }

    private boolean emailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

}