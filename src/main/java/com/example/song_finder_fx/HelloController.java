package com.example.song_finder_fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

import java.io.File;
import java.sql.SQLException;

public class HelloController {
    @FXML
    public Label fileNameLabel;
    public ProgressBar progressBar;
    public File file;
    public TextArea textArea;

    @FXML
    protected void onBrowseButtonClick(ActionEvent event) throws SQLException, ClassNotFoundException {
        Main main = new Main();
        file = main.browseFile(event);
        String fileName = file.getName();
        fileNameLabel.setText(fileName);
        // progressBar.setProgress(100.0);

        // Creating Database
        Database.CreateBase();
    }

    public void onProceedButtonClick() throws SQLException, ClassNotFoundException {
        Main main = new Main();
        String text = textArea.getText();

        main.searchAudios(text);
        /*System.out.println(Arrays.toString(isrcCodes));*/
    }
}