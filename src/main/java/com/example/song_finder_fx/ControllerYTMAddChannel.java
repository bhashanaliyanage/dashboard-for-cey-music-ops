package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
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
        String channelName = txtChannelName.getText().trim();
        String url = txtURL.getText().trim();
        ToggleButton selectedType = (ToggleButton) type.getSelectedToggle();

        // Validate channel name
        if (channelName.isEmpty()) {
            AlertBuilder.sendErrorAlert("Error", "Channel name cannot be empty.", null);
            return;
        }

        // Validate URL
        if (url.isEmpty()) {
            AlertBuilder.sendErrorAlert("Error", "URL cannot be empty.", null);
            return;
        }

        if (selectedType == null) {
            AlertBuilder.sendErrorAlert("Error", "Please select a type (Channel or Playlist).", null);
            return;
        }

        String selectedTypeValue = selectedType.getText();

        // Validate URL format based on type
        if (selectedTypeValue.equals("Channel")) {
            if (!isValidChannelUrl(url)) {
                AlertBuilder.sendErrorAlert("Error", "Invalid YouTube channel URL format.", null);
                return;
            }
        } else if (selectedTypeValue.equals("Playlist")) {
            if (!isValidPlaylistUrl(url)) {
                AlertBuilder.sendErrorAlert("Error", "Invalid YouTube playlist URL format.", null);
                return;
            }
        }

        System.out.println("Validations passed");// Save channel to database
    }

    private boolean isValidChannelUrl(String url) {
        // Basic validation for channel URL format
        return url.matches("https://www\\.youtube\\.com/@[\\w-]+(/.*)?");
    }

    private boolean isValidPlaylistUrl(String url) {
        // Basic validation for playlist URL format
        return url.matches("https://www\\.youtube\\.com/playlist\\?list=[\\w-]+");
    }

}