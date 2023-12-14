package com.example.song_finder_fx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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

    public void loadAbout() throws IOException {
        FXMLLoader loader = new FXMLLoader(ControllerSettings.class.getResource("layouts/about.fxml"));
        loader.setController(this);
        Parent newContent = loader.load();

        mainUIController.mainVBox.getChildren().clear();
        mainUIController.mainVBox.getChildren().add(newContent);

        String versionInfo = "Build " + Main.PRODUCT_VERSION + " by Bhashana Liyanage";
        lblVersionInfoAboutPage.setText(versionInfo);

        if ((InitPreloader.PRODUCT_VERSION != null) && (Main.PRODUCT_VERSION < InitPreloader.PRODUCT_VERSION)) {
            loadUpdate();
        }
    }

    private void loadUpdate() throws IOException {
        FXMLLoader loader = new FXMLLoader(ControllerSettings.class.getResource("layouts/sidepanel-update.fxml"));
        loader.setController(this);
        Parent sidepanelContent = loader.load();

        mainUIController.sideVBox.getChildren().clear();
        mainUIController.sideVBox.getChildren().add(sidepanelContent);

        lblVersion.setText("Version " + InitPreloader.PRODUCT_VERSION);
    }

    /*public void onUpdateBtnClick() throws IOException {
        File setup = new File("http:\\192.168.1.200:8080\\ceymusic-dahsboard-software-updates\\CrystalDiskInfo8_17_13.exe");
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(setup);
        }
    }*/

    public void onUpdateBtnClick() throws IOException {
        /*String location = InitPreloader.updateLocation;
        Path path = Paths.get(location);
        try (BufferedInputStream in = new BufferedInputStream(new URL(location).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream("CrystalDiskInfo8_17_13.exe")) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, 1024);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        Path tempDir = Files.createTempDirectory("CeyMusic_Dashboard_UpdateTemp");
        InputStream in = new URL(InitPreloader.updateLocation).openStream();
        Path tempFile = Files.createTempFile(tempDir, "update_cey_dash", ".exe");
        Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
        Desktop.getDesktop().open(new File(tempFile.toUri()));
    }
}
