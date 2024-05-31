package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.SceneController;
import com.example.song_finder_fx.Controller.UserSettingsManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class ControllerSettings {
    private UIController mainUIController = null;
    public Button btnAudioDatabase;
    public Button btnSave;
    public Button btnImportArtists;
    public Label lblVersion;
    public Label lblVersionInfoAboutPage;

    public ControllerSettings() {

    }

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


    //not  use in Fxml
    public void onImportArtistsButtonClick() throws SQLException, ClassNotFoundException, IOException {
        System.out.println("ControllerSettings.onImportArtistsButtonClick");

        // Prompt user to import CSV
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        File csv = fileChooser.showOpenDialog(btnAudioDatabase.getScene().getWindow());
        btnImportArtists.setText("   " + csv.getName());

        // Make table
//        DatabaseMySQL.createTableArtists();

        // Import to table in a separate thread
//        DatabaseMySQL.importToArtistsTable(csv);

        // Update user interface
        btnImportArtists.setText("   Execution Complete");
    }

    public void onSaveButtonClicked() throws SQLException, ClassNotFoundException {
        // System.out.println("Save Button Clicked");
        File directory = Main.getSelectedDirectory();

        if (Main.selectedDirectory.exists()) { // check if the directory exists
            if (Main.selectedDirectory.isDirectory()) { // check if the directory is a directory
                if (Main.selectedDirectory.canWrite()) {
                    String directoryString = directory.getAbsolutePath();
                    Properties settings = new Properties();
                    settings.setProperty("adb", directoryString);
                    UserSettingsManager.saveUserSettings(settings);
                    btnSave.setText("Saved");
                }
            }
        }

        /*if (directory.isDirectory()) {
            // Boolean status = Database.saveDirectory(directoryString);

            if (status) {
                btnSave.setText("Saved");
                System.out.println("Saved");
            } else {
                btnSave.setText("Error Saving!");
                System.out.println("Error Saving");
            }
        } else {
            System.out.println("Selected Directory is Null");
        }*/
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
        // loader.setController(this);
        Parent sidepanelContent = loader.load();

        mainUIController.sideVBox.getChildren().clear();
        mainUIController.sideVBox.getChildren().add(sidepanelContent);

        // lblVersion.setText(Main.versionInfo.getUpdateVersionInfo());
    }

    public void onUpdateBtnClick(ActionEvent event) throws IOException, URISyntaxException {
        Scene scene = SceneController.getSceneFromEvent(event);
        Button btnUpdate = (Button) scene.lookup("#btnUpdate");
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> btnUpdate.setText("Downloading"));
                File updateFile = Main.versionInfo.getUpdate();
                Platform.runLater(() -> {
                    try {
                        Desktop.getDesktop().open(updateFile);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                Platform.runLater(() -> btnUpdate.setText("Installing..."));
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.start();
    }
}
