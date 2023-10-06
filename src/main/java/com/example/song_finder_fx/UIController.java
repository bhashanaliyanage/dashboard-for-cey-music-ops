package com.example.song_finder_fx;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class UIController {
    public TextArea textArea;
    public Button btnDestination;
    public Button btnProceed;
    public ImageView ProgressView;
    public HBox searchAndCollect;
    public VBox textAreaVbox;
    public VBox mainVBox;
    File directory;
    File destination;
    public Button btnAudioDatabase;

    @FXML
    protected void onSearchDetailsButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("search-details.fxml"));
            Parent newContent = loader.load();

            mainVBox.getChildren().clear();
            mainVBox.getChildren().add(newContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /*if (toggle == 1) {
            searchAndCollect.setStyle("-fx-background-color: #eeefee; -fx-border-color: '#c0c1c2';");
            ProgressView.setVisible(true);
            btnProceed.setText("Processing");
            ProgressView.setVisible(true);
            btnAudioDatabase.setDisable(true);
            btnDestination.setDisable(true);
            textArea.setDisable(true);
            btnProceed.setDisable(true);
            textAreaVbox.setDisable(true);
            toggle = 0;
        } else {
            searchAndCollect.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: '#e9ebee';");
            ProgressView.setVisible(false);
            btnProceed.setText("Proceed");
            ProgressView.setVisible(false);
            btnAudioDatabase.setDisable(false);
            btnDestination.setDisable(false);
            textArea.setDisable(false);
            btnProceed.setDisable(false);
            textAreaVbox.setDisable(false);
            toggle = 1;
        }*/
        System.out.println("onSearchDetailsButtonClick");
    }

    public void onCollectSongsButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("collect-songs.fxml"));
            Parent newContent = loader.load();

            mainVBox.getChildren().clear();
            mainVBox.getChildren().add(newContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void onBrowseAudioButtonClick() {
        Main main = new Main();
        directory = main.browseLocation();
        String shortenedString = directory.getAbsolutePath().substring(0, Math.min(directory.getAbsolutePath().length(), 73));
        btnAudioDatabase.setText("   Database: " + shortenedString + "...");
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
        btnDestination.setText("   Destination: " + shortenedString + "...");
    }

    @FXML
    protected void onUpdateDatabaseButtonClick() throws SQLException, IOException, ClassNotFoundException {
        Main main = new Main();
        File file = main.browseFile();
        Database.updateBase(file);
    }

    public void onProceedButtonClick(ActionEvent event) throws SQLException, ClassNotFoundException, IOException {
        searchAndCollect.setStyle("-fx-background-color: #eeefee; -fx-border-color: '#c0c1c2';");
        ProgressView.setVisible(true);
        btnAudioDatabase.setDisable(true);
        btnDestination.setDisable(true);
        textArea.setDisable(true);
        btnProceed.setDisable(true);
        textAreaVbox.setDisable(true);
        Task<Void> task = null;
        btnProceed.setText("Processing");

        if (directory == null || destination == null) {
            showErrorDialog("Empty Location Entry", "Please browse for Audio Database and Destination Location", "Use the location section for this");
            btnProceed.setText("Proceed");
            searchAndCollect.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: '#e9ebee';");
            ProgressView.setVisible(false);
            btnAudioDatabase.setDisable(false);
            btnDestination.setDisable(false);
            textArea.setDisable(false);
            btnProceed.setDisable(false);
            textAreaVbox.setDisable(false);
        } else {
            String text = textArea.getText();
            System.out.println(text);
            System.out.println("Here");
            if (!text.isEmpty()) {
                task = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        Main main = new Main();


                        String[] ISRCCodes = text.split("\\n");

                        for (String ISRCCode : ISRCCodes) {
                            if (ISRCCode.length() == 12) {
                                String done = main.searchAudios(text, directory, destination);
                                if (done.equals("Done")) {
                                    Platform.runLater(() -> {
                                        // Code that updates the UI goes here
                                        btnProceed.setText("Proceed");
                                        searchAndCollect.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: '#e9ebee';");
                                        ProgressView.setVisible(false);
                                        btnAudioDatabase.setDisable(false);
                                        btnDestination.setDisable(false);
                                        textArea.setDisable(false);
                                        btnProceed.setDisable(false);
                                        textAreaVbox.setDisable(false);
                                    });
                                }
                            } else {
                                showErrorDialog("Invalid ISRC Code", "Invalid or empty ISRC Code", ISRCCode);
                            }
                        }
                        return null;
                    }
                };
            } else {
                showErrorDialog("Invalid ISRC Code", "Empty ISRC Code", "Please enter ISRC codes in the text area");
                btnProceed.setText("Proceed");
                searchAndCollect.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: '#e9ebee';");
                ProgressView.setVisible(false);
                btnAudioDatabase.setDisable(false);
                btnDestination.setDisable(false);
                textArea.setDisable(false);
                btnProceed.setDisable(false);
                textAreaVbox.setDisable(false);
            }

        }

        assert task != null;
        task.setOnSucceeded(e -> {
            btnProceed.setText("Proceed");
            NotificationBuilder nb = new NotificationBuilder();
            try {
                nb.displayTray();
            } catch (AWTException ex) {
                throw new RuntimeException(ex);
            }
        });

        new Thread(task).start();
    }

    private static void showErrorDialog(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }


    public void onTestNotifyButtonClick(ActionEvent event) throws AWTException {
        System.out.println("Test Notify Button Click");
        NotificationBuilder nb = new NotificationBuilder();
        nb.displayTray();
    }
}