package com.example.song_finder_fx;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import static java.awt.SystemColor.text;

public class UIController {
    public TextArea textArea;
    public Button btnDestination;
    File directory;
    File destination;
    @FXML
    public Button btnAudioDatabase;

    @FXML
    protected void onBrowseAudioButtonClick() {
        Main main = new Main();
        directory = main.browseLocation();
        String shortenedString = directory.getAbsolutePath().substring(0, Math.min(directory.getAbsolutePath().length(), 73));
        btnAudioDatabase.setText("Database: " + shortenedString + "...");
        if (directory != null) {
            Database.SearchSongsFromAudioLibrary(directory);
        } else {
            System.out.println("Directory not chosen!");
        }
    }

    public void onBrowseDestinationButtonClick() {
        Main main = new Main();
        destination = main.browseDestination();
        String shortenedString = destination.getAbsolutePath().substring(0, Math.min(destination.getAbsolutePath().length(), 73));
        btnDestination.setText("Destination: " + shortenedString + "...");
    }

    @FXML
    protected void onUpdateDatabaseButtonClick() throws SQLException, IOException, ClassNotFoundException {
        Main main = new Main();
        File file = main.browseFile();
        Database.updateBase(file);
    }

    public void onProceedButtonClick(ActionEvent event) throws SQLException, ClassNotFoundException, IOException {
        Task<Void> task = null;

        if (directory == null || destination == null) {
            showErrorDialog("Empty Location Entry", "Please browse for Audio Database and Destination Location", "Use the location section for this");
        } else {
            System.out.println(text.toString());
            task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Main main = new Main();
                    String text = textArea.getText();

                    String[] ISRCCodes = text.split("\\n");

                    for (String ISRCCode : ISRCCodes) {
                        if (ISRCCode.length() == 12) {
                            main.searchAudios(text, directory, destination);
                        } else {
                            showErrorDialog("Invalid ISRC Code", "Invalid or empty ISRC Code", ISRCCode);
                        }
                    }
                    return null;
                }
            };
        }
        new Thread(task).start();
    }

    private static void showErrorDialog(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }

}