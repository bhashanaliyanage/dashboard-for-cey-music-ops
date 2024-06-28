package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.ItemSwitcher;
import com.example.song_finder_fx.Controller.SceneController;
import com.example.song_finder_fx.Session.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ControllerProfile {

    @FXML
    private Button btnLogOut;

    @FXML
    private Label lblEmail;

    @FXML
    private Label lblNickname;

    public static Label lblNicknameStatic;

    @FXML
    private Label lblPassword;

    @FXML
    private Label lblUserPrivilegeLevel;

    @FXML
    private Label lblUsername;

    public static Label lblUsernameStatic;

    @FXML
    private Label lblUsernameTop;

    public static Label lblUsernameTopStatic;

    @FXML
    private ScrollPane scrlpneMain;

    @FXML
    private VBox vBoxLogIn;

    private UserSession userSession;

    @FXML
    void initialize() {
        userSession = Main.userSession;
        String nickName = userSession.getNickName();
        int privilegeLevel = userSession.getPrivilegeLevel();
        String userName = userSession.getUserName();
        String email = userSession.getEmail();

        lblUsernameTop.setText(nickName);
        lblNickname.setText(nickName);
        lblUserPrivilegeLevel.setText(ItemSwitcher.setPrivilegeLevel(privilegeLevel));
        lblUsername.setText(userName);
        lblEmail.setText(email);

        lblNicknameStatic = lblNickname;
        lblUsernameStatic = lblUsername;
        lblUsernameTopStatic = lblUsernameTop;
    }

    @FXML
    void onLogOut(ActionEvent event) {
        Main.userSession.logout();
        try {
            Node node = SceneController.loadLayout("layouts/user/login_signup.fxml");
            UIController.mainVBoxStatic.getChildren().setAll(node);
            UIController.disableUser();
        } catch (IOException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Initializing UI", e.toString());
        }
    }

    @FXML
    void onEdit(ActionEvent event) {
        try {
            Node node = SceneController.loadLayout("layouts/user/edit_profile.fxml");
            UIController.sideVBoxStatic.getChildren().setAll(node);
        } catch (IOException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Initializing UI", e.toString());
        }
    }

}
