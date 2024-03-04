package com.example.song_finder_fx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

public class ControllerSettings {
    private UIController mainUIController = null;
    public Button btnAudioDatabase;
    public Button btnSave;
    public Button btnImportArtists;
    public Label lblVersion;
    public Label lblVersionInfoAboutPage;

    public ControllerSettings(UIController MainUIController) {
        this.mainUIController = MainUIController;
    }

    public void loadThings() throws IOException, SQLException, ClassNotFoundException {
        FXMLLoader loader = new FXMLLoader(ControllerSettings.class.getResource("layouts/settings.fxml"));
        loader.setController(this);
        Parent newContent = loader.load();

        mainUIController.mainVBox.getChildren().clear();
        mainUIController.mainVBox.getChildren().add(newContent);

        String directorySQLite = Main.getAudioDatabaseLocation();

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
        Main.selectedDirectory = UIController.directory;

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
                System.out.println("Saved");
            } else {
                btnSave.setText("Error Saving!");
                System.out.println("Error Saving");
            }
        } else {
            System.out.println("Selected Directory is Null");
        }
    }

    public void loadAbout(Node aboutView) throws IOException {
        mainUIController.mainVBox.getChildren().clear();
        mainUIController.mainVBox.getChildren().add(aboutView);

        Scene scene = aboutView.getScene();
        lblVersionInfoAboutPage = (Label) scene.lookup("#lblVersionInfoAboutPage");
        lblVersionInfoAboutPage.setText(Main.versionInfo.getCurrentVersionInfo());

        if (Main.versionInfo.updateAvailable()) {
            loadUpdate();
        }
    }

    private void loadUpdate() throws IOException {
        FXMLLoader loader = new FXMLLoader(ControllerSettings.class.getResource("layouts/sidepanel-update.fxml"));
        loader.setController(this);
        Parent sidepanelContent = loader.load();

        mainUIController.sideVBox.getChildren().clear();
        mainUIController.sideVBox.getChildren().add(sidepanelContent);

        lblVersion.setText(Main.versionInfo.getUpdateVersionInfo());
    }

    public void onUpdateBtnClick() throws IOException, URISyntaxException {
        File updateFile = Main.versionInfo.getUpdate();
        Desktop.getDesktop().open(updateFile);
    }
}
