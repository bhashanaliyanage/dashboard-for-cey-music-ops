package com.example.song_finder_fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class ControllerSettings {
    private UIController mainUIController = null;
    public Button btnAudioDatabase;
    public Button btnSave;

    public ControllerSettings() {}

    public ControllerSettings(UIController MainUIController) {
        this.mainUIController = MainUIController;
    }

    public void loadThings() throws IOException, SQLException, ClassNotFoundException {
        FXMLLoader loader = new FXMLLoader(ControllerSettings.class.getResource("layouts/settings.fxml"));
        loader.setController(this);
        Parent newContent = loader.load();

        mainUIController.mainVBox.getChildren().clear();
        mainUIController.mainVBox.getChildren().add(newContent);

        String directorySQLite = Main.getDirectoryFromDB();

        if (directorySQLite == null) {
            System.out.println("Directory Null!");
        } else {
            System.out.println("Audio Database Directory: " + directorySQLite);
            btnAudioDatabase.setText("   " + directorySQLite);
        }
    }

    public void onBrowseAudioButtonClick(ActionEvent actionEvent) {
        System.out.println("Test");
        UIController.directory = Main.browseLocation();
        String shortenedString = UIController.directory.getAbsolutePath().substring(0, Math.min(UIController.directory.getAbsolutePath().length(), 73));
        btnAudioDatabase.setText("   Database: " + shortenedString + "...");
    }

    public void onSaveButtonClicked(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        System.out.println("Save Button Clicked");
        File directory = Main.getSelectedDirectory();

        if (directory != null) {
            String directoryString = directory.getAbsolutePath();
            Boolean status = Database.saveDirectory(directoryString);

            if (status) {
                btnSave.setText("Saved");
            } else {
                btnSave.setText("Error Saving!");
            }
        }
    }
}
