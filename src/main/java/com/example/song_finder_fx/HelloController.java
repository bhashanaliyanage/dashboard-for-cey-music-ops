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
    public File file;
    public TextArea textArea;
    public Label audioLocation;
    File directory;

    @FXML
    protected void onBrowseButtonClick(ActionEvent event) throws SQLException, ClassNotFoundException {
        Main main = new Main();
        directory = main.browseLocation();
        Database.SearchSongsFromAudioLibrary(directory);
    }

    public void onProceedButtonClick() throws SQLException, ClassNotFoundException {
        Main main = new Main();
        String text = textArea.getText();

        main.searchAudios(text, directory);
        /*System.out.println(Arrays.toString(isrcCodes));*/
    }
}