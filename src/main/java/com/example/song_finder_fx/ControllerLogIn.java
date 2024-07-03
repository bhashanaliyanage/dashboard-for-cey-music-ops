package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.SceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;

public class ControllerLogIn {

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField txtUsername;

    @FXML
    private VBox vBoxLogIn;

    public static VBox vBoxLogInStatic;

    @FXML
    void initialize() {
        vBoxLogInStatic = vBoxLogIn;
    }

    @FXML
    void onLogIn(ActionEvent event) {
        String username = txtUsername.getText();
        String password = passwordField.getText();

        try {
            boolean loggedIn = Main.userSession.login(username, password);

            if (loggedIn) {
                // Get User Button
                Node node2 = (Node) event.getSource();
                Scene scene = node2.getScene();
                Label lblUsername = SceneController.getLabelFromScene(scene, "lblUser");
                Label lblEmail = SceneController.getLabelFromScene(scene, "lblUserEmailAndUpdate");

                // Load Logged In View
                Node node = SceneController.loadLayout("layouts/user/login_success.fxml");
                vBoxLogIn.getChildren().clear();
                vBoxLogIn.getChildren().add(node);

                // Set user details
                String nickname = Main.userSession.getNickName();
                String email = Main.userSession.getEmail();

                // Get user details
                lblUsername.setText(nickname);
                lblEmail.setText(email);

                // Set privileges
                UIController.loadUser();
            } else {
                txtUsername.setStyle("-fx-border-color: red;");
                passwordField.setStyle("-fx-border-color: red;");
            }
        } catch (SQLException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Login", e.toString());
        } catch (IOException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Initializing UI", e.toString());
        }
    }

    @FXML
    void onSignUp(ActionEvent event) {
        try {
            Node node = SceneController.loadLayout("layouts/user/signup.fxml");
            vBoxLogIn.getChildren().clear();
            vBoxLogIn.getChildren().add(node);
        } catch (IOException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Initializing UI", e.toString());
        }
    }

}
