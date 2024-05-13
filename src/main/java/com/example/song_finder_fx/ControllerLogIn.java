package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.SceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;

public class ControllerLogIn {

    @FXML
    private TextField txtPassword;

    @FXML
    private TextField txtUsername;

    @FXML
    private VBox vBoxLogIn;

    @FXML
    void onLogIn(ActionEvent event) throws SQLException, IOException {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        boolean loggedIn = Main.userSession.login(username, password);

        if (loggedIn) {
            // Get User Button
            Node node2 = (Node) event.getSource();
            Scene scene = node2.getScene();
            Label lblUsername = SceneController.getLabelFromScene(scene, "lblUser");
            Label lblEmail = SceneController.getLabelFromScene(scene, "lblUserEmailAndUpdate");
            HBox hBoxRevenueAnalysis = SceneController.getHBoxFromScene(scene, "btnRevenueAnalysis");
            HBox hBoxArtistReports = SceneController.getHBoxFromScene(scene, "btnArtistReports");

            // Load Logged In View
            Node node = SceneController.loadLayout("layouts/user/login_success.fxml");
            vBoxLogIn.getChildren().setAll(node);

            // Set user details
            String nickname = Main.userSession.getNickName();
            String email = Main.userSession.getEmail();

            // Get user details
            lblUsername.setText(nickname);
            lblEmail.setText(email);

            // Set privileges
            int privilegeLevel = Main.userSession.getPrivilegeLevel();
            if (privilegeLevel == 3) {
                try {
                    hBoxRevenueAnalysis.setDisable(true);
                    hBoxArtistReports.setDisable(true);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else if (privilegeLevel == 2) {
                hBoxRevenueAnalysis.setDisable(false);
                hBoxArtistReports.setDisable(false);
            }
        } else {
            txtUsername.setStyle("-fx-border-color: red;");
            txtPassword.setStyle("-fx-border-color: red;");
        }
    }

    @FXML
    void onSignUp(ActionEvent event) {

    }

}
