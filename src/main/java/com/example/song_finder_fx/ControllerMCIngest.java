package com.example.song_finder_fx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;

public class ControllerMCIngest {

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
