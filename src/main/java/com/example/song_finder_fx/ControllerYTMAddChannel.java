package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.NotificationBuilder;
import com.example.song_finder_fx.Controller.YoutubeDownload;
import com.example.song_finder_fx.Model.YoutubeData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

public class ControllerYTMAddChannel {

    @FXML
    private TextField txtChannelName;

    @FXML
    private TextField txtURL;

    @FXML
    private ToggleGroup type;

    @FXML
    void onSave(ActionEvent event) {
        boolean isValid = true;
        String channelName = txtChannelName.getText().trim();
        String url = txtURL.getText().trim();
        ToggleButton selectedType = (ToggleButton) type.getSelectedToggle();

        // Reset styles
        resetStyles();

        // Validate channel name
        if (channelName.isEmpty()) {
            setErrorStyle(txtChannelName);
            isValid = false;
        }

        // Validate URL
        if (url.isEmpty()) {
            setErrorStyle(txtURL);
            isValid = false;
        }

        if (selectedType == null) {
            AlertBuilder.sendErrorAlert("Error", "Please select a type (Channel or Playlist).", null);
            return;
        }

        String selectedTypeValue = selectedType.getText();

        // Validate URL format based on type
        if (selectedTypeValue.equals("Channel")) {
            if (!isValidChannelUrl(url)) {
                AlertBuilder.sendErrorAlert("Error", null, "Invalid YouTube channel URL format.");
                isValid = false;
            }
        } else if (selectedTypeValue.equals("Playlist")) {
            if (!isValidPlaylistUrl(url)) {
                AlertBuilder.sendErrorAlert("Error", null, "Invalid YouTube playlist URL format.");
                isValid = false;
            }
        }

        if (isValid) {
            System.out.println("Validations passed");// Save channel to database

            YoutubeData channel = new YoutubeData();
            channel.setName(channelName);
            channel.setType(selectedTypeValue.equals("Channel") ? 1 : 2);
            channel.setUrl(url);

            YoutubeDownload youtubeDownload = new YoutubeDownload();

            if (youtubeDownload.addChannelToDatabase(channel)) {
                // AlertBuilder.sendInfoAlert("Success", null, "Channel added to database.");
                NotificationBuilder.displayTrayInfo("Channel added to database.", "Channel added to database.");
            } else {
                // AlertBuilder.sendErrorAlert("Error", null, "Failed to add channel to database.");
                NotificationBuilder.displayTrayError("Failed to add channel to database.", "Failed to add channel to database.");
            }
        }
    }

    private boolean isValidChannelUrl(String url) {
        // Basic validation for channel URL format
        return url.matches("https://www\\.youtube\\.com/@[\\w-]+(/.*)?");
    }

    private boolean isValidPlaylistUrl(String url) {
        // Basic validation for playlist URL format
        return url.matches("https://www\\.youtube\\.com/playlist\\?list=[\\w-]+");
    }

    private void resetStyles() {
        txtChannelName.setStyle("");
        txtURL.setStyle("");
    }

    private void setErrorStyle(TextField... textFields) {
        for (TextField textField : textFields) {
            textField.setStyle("-fx-border-color: red;");
        }
    }

}