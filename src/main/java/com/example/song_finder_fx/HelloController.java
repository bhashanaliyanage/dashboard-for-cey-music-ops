package com.example.song_finder_fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

public class HelloController {
    @FXML
    public Label fileNameLabel;
    public ProgressBar progressBar;
    public File file;

    @FXML
    protected void onBrowseButtonClick(ActionEvent event) throws SQLException, ClassNotFoundException {
        Main main = new Main();
        file = main.browseFile(event);
        String fileName = file.getName();
        fileNameLabel.setText(fileName);
        // progressBar.setProgress(100.0);

        // Creating Database
        main.CreateBase();
    }

    public void onProceedButtonClick(ActionEvent event) throws SQLException, IOException, ClassNotFoundException {
        Main main = new Main();
        main.ImportToBase(file);
    }
}