package com.example.song_finder_fx.UIControllers;

import com.example.song_finder_fx.Controller.SceneController;
import com.example.song_finder_fx.DatabasePostgre;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;

public class ControllerMCIdentifiers {

    @FXML
    private VBox vbClaimsList;

    @FXML
    public void initialize() throws IOException {
        vbClaimsList.getChildren().clear();
        for (int i = 0; i < ControllerMCList.checkBoxes.size(); i++) {
            if (ControllerMCList.checkBoxes.get(i).isSelected()) {
                Node entry = SceneController.loadLayout("layouts/manual_claims/mci-entry.fxml");
                vbClaimsList.getChildren().add(entry);
            }
        }
    }

    @FXML
    void onBack(MouseEvent event) {

    }

    @FXML
    void onGenerate(MouseEvent event) {

    }

    @FXML
    void toRoot(MouseEvent event) {

    }

}
