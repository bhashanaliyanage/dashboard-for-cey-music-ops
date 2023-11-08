package com.example.song_finder_fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class ControllerSettings {
    private UIController mainUIController = null;
    public Button btnAudioDatabase;
    public Button btnSave;
    public Button btnImportArtists;

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

    public void onBrowseAudioButtonClick() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select a directory");
        // File selectedDirectory = chooser.showDialog(btnAudioDatabase.getScene().getWindow());

        UIController.directory = chooser.showDialog(btnAudioDatabase.getScene().getWindow());

        if (UIController.directory != null) {
            String shortenedString = UIController.directory.getAbsolutePath().substring(0, Math.min(UIController.directory.getAbsolutePath().length(), 73));
            btnAudioDatabase.setText("   Database: " + shortenedString + "...");
        } else {
            System.out.println("No directory specified");
        }
    }

    public void onImportArtistsButtonClick() throws SQLException, ClassNotFoundException, IOException {
        System.out.println("ControllerSettings.onImportArtistsButtonClick");

        // Prompt user to import CSV
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        File csv = fileChooser.showOpenDialog(btnAudioDatabase.getScene().getWindow());
        btnImportArtists.setText("   " + csv.getName());

        // Make table
        DatabaseMySQL.createTableArtists();

        // Import to table in a separate thread
        DatabaseMySQL.importToArtistsTable(csv);

        // Update user interface
        btnImportArtists.setText("   Execution Complete");
    }

    public void onSaveButtonClicked() throws SQLException, ClassNotFoundException {
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
