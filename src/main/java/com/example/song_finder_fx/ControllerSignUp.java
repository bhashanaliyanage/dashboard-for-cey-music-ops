package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.SceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;

public class ControllerSignUp {

    @FXML
    private PasswordField passwordField;

    public static PasswordField passwordFieldStatic;

    @FXML
    private TextField txtUsername;

    public static TextField txtUsernameStatic;

    @FXML
    void initialize() {
        txtUsernameStatic = txtUsername;
        passwordFieldStatic = passwordField;
    }

    @FXML
    void onSetUpProfile(ActionEvent event) {
        if (!txtUsername.getText().isEmpty()) {
            try {
                if (Main.userSession.checkUsernameAvailability(txtUsername.getText())) {
                    txtUsername.setStyle("-fx-border-color: '#e9ebee';");

                    if (!passwordField.getText().isEmpty()) {
                        passwordField.setStyle("-fx-border-color: '#e9ebee';");

                        System.out.println("Proceed to setup profile page");

                        try {
                            Node node = SceneController.loadLayout("layouts/user/signup-profile-setup.fxml");
                            ControllerLogIn.vBoxLogInStatic.getChildren().clear();
                            ControllerLogIn.vBoxLogInStatic.getChildren().add(node);
                        } catch (IOException e) {
                            AlertBuilder.sendErrorAlert("Error", "Error Initializing UI", e.toString());
                        }
                    } else {
                        passwordField.setStyle("-fx-border-color: red;");
                    }
                } else {
                    txtUsername.setStyle("-fx-border-color: red;");
                }
            } catch (SQLException e) {
                AlertBuilder.sendErrorAlert("Database Error", "Error Checking Username Availability", "Username: " + txtUsername.getText() + "\n\nStack Trace: " + e);
            }
        } else {
            txtUsername.setStyle("-fx-border-color: red;");
        }
    }

}
