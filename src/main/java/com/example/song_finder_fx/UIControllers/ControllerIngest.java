package com.example.song_finder_fx.UIControllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;

public class ControllerIngest {

    @FXML
    private Label lblLocation;

    @FXML
    void onGoBack(MouseEvent event) {

    }

    @FXML
    void onOpenBtnClicked(MouseEvent event) throws IOException {
        Path filePath = Path.of(lblLocation.getText());
        System.out.println("filePath = " + filePath);
        Desktop.getDesktop().open(filePath.toFile());
    }

}
