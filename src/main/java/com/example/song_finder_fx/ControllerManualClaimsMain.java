package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class ControllerManualClaimsMain {

    @FXML
    private Label lblClaimCount;

    public static Node nodeMC_List;

    @FXML
    public void initialize() throws SQLException {
        lblClaimCount.setText(DatabasePostgres.getManualClaimCount());
    }

    @FXML
    void onAddManualClaim(MouseEvent event) throws IOException {
        Node node;

        if (UIController.mainNodes[6] == null) {
            System.out.println("Loading Add Manual Claims UI");
            node = FXMLLoader.load(Objects.requireNonNull(UIController.class.getResource("layouts/manual_claims/manual-claims.fxml")));
            UIController.mainNodes[6] = node;
        } else {
            System.out.println("Accessing Add Manual Claims UI From UI Controller Reference");
            node = UIController.mainNodes[6];
        }

        Scene scene = ((Node) event.getSource()).getScene();
        VBox mainVBox = (VBox) scene.lookup("#mainVBox");
        mainVBox.getChildren().clear();
        mainVBox.getChildren().add(node);
    }

    @FXML
    void onIngestManualClaims(MouseEvent event) throws IOException {
        nodeMC_List = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/manual_claims/manual-claims-list.fxml")));
        Scene scene = ((Node) event.getSource()).getScene();
        VBox mainVBox = (VBox) scene.lookup("#mainVBox");
        mainVBox.getChildren().setAll(nodeMC_List);
    }

    @FXML
    void onArchivedClaims(MouseEvent event) {
        try {
            Node node = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/manual_claims/mc-archive-list.fxml")));
            Scene scene = ((Node) event.getSource()).getScene();
            VBox mainVBox = (VBox) scene.lookup("#mainVBox");
            mainVBox.getChildren().setAll(node);
        } catch (IOException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Loading Manual Claims View", e.toString());
        }
    }

}