package com.example.song_finder_fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class HelloController {
    public TextArea textArea;
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

    public void onProceedButtonClick() throws SQLException, ClassNotFoundException, IOException {
        Main main = new Main();
        String text = textArea.getText();

        if (directory == null || destination == null) {
            showErrorDialog("Empty Location Entry", "Please browse for Audio Database and Destination Location", "Use the location section for this");
        } else {
            if (text != null) {
                String[] ISRCCodes = text.split("\\n");

                for (String ISRCCode : ISRCCodes) {
                    if (ISRCCode.length() == 12) {
                        main.searchAudios(text, directory, destination);
                    } else {
                        showErrorDialog("Invalid ISRC Code", "Invalid or empty ISRC Code", ISRCCode);
                    }
                }
            } else {
                System.out.println("No ISRC codes given!");
            }
        }

    }

    private static void showErrorDialog(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }

    public void onBrowseDestinationButtonClick() {
        Main main = new Main();
        destination = main.browseDestination();
    }
}