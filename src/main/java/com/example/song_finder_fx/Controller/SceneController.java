package com.example.song_finder_fx.Controller;

import com.example.song_finder_fx.ControllerSettings;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Objects;

public class SceneController {
    public static Scene getSceneFromEvent(MouseEvent event) {
        Node node = (Node) event.getSource();
        return node.getScene();
    }

    public static VBox getSideVBox(Scene scene) {
        return  (VBox) scene.lookup("#sideVBox");
    }

    public static VBox getMainVBox(Scene scene) {
        return  (VBox) scene.lookup("#mainVBox");
    }

    public static Node loadLayout(String sceneLayoutFile) throws IOException {
        return FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource(sceneLayoutFile)));
    }

    public static Label getLabelFromScene(Scene scene, String fxid) {
        return (Label) scene.lookup("#" + fxid);
    }

    public static TextField getTextFieldFromScene(Scene scene, String fxid) {
        return (TextField) scene.lookup("#" + fxid);
    }

    public static VBox getMainVBoxFromEvent(ActionEvent event) {
        Scene scene = getSceneFromEvent(event);
        assert scene != null;
        return getMainVBox(scene);
    }

    public static Scene getSceneFromEvent(ActionEvent event) {
        Node node = (Node) event.getSource();
        return node.getScene();
    }

    public static Window getWindowFromMouseEvent(MouseEvent event) {
        Scene scene = ((Button) event.getSource()).getScene();
        Stage stage = (Stage) scene.getWindow();
        return stage;
    }
}
