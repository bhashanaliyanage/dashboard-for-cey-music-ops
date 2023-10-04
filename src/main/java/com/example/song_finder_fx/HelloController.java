package com.example.song_finder_fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class HelloController {
    @FXML
    public Label fileNameLabel;
    public File file;
    public TextArea textArea;
    public Label audioLocation;
    File directory;
    File destination;

    @FXML
    protected void onBrowseAudioButtonClick(ActionEvent event) throws SQLException, ClassNotFoundException {
        Main main = new Main();
        directory = main.browseLocation();
        if (directory != null) {
            Database.SearchSongsFromAudioLibrary(directory);
        } else {
            System.out.println("Directory not chosen!");
        }
    }

    public void onProceedButtonClick() throws SQLException, ClassNotFoundException, IOException {
        Main main = new Main();
        String text = textArea.getText();

        if (text != null) {
            main.searchAudios(text, directory, destination);
        } else {
            System.out.println("No ISRC codes given!");
        }

        /*System.out.println(Arrays.toString(isrcCodes));*/
    }

    public void onBrowseDestinationButtonClick(ActionEvent actionEvent) {
        Main main = new Main();
        destination = main.browseDestination();
    }
}