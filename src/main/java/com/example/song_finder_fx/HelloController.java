package com.example.song_finder_fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class HelloController {
    public TextArea textArea;
    public Label audioLocation;
    File directory;
    File destination;

    @FXML
    protected void onBrowseAudioButtonClick() {
        Main main = new Main();
        directory = main.browseLocation();
        if (directory != null) {
            Database.SearchSongsFromAudioLibrary(directory);
        } else {
            System.out.println("Directory not chosen!");
        }
    }

    @FXML
    protected void onUpdateDatabaseButtonClick() throws SQLException, IOException, ClassNotFoundException {
        Main main = new Main();
        File file = main.browseFile();
        Database.updateBase(file);
    }

    @FXML
    protected void onTempProceedButtonClick() {
    }

    public void onProceedButtonClick() throws SQLException, ClassNotFoundException, IOException {
        Main main = new Main();
        String text = textArea.getText();

        if (text != null) {
            main.searchAudios(text, directory, destination);
        } else {
            System.out.println("No ISRC codes given!");
        }
    }

    public void onBrowseDestinationButtonClick(ActionEvent actionEvent) {
        Main main = new Main();
        destination = main.browseDestination();
    }
}