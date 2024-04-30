package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.UserSettingsManager;
import com.example.song_finder_fx.Model.ProductVersion;
import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Main extends Application {
    public static Stage primaryStage = null;
    static List<String> songList = new ArrayList<>();
    static File selectedDirectory = null;
    static Clip clip;
    public static ProductVersion versionInfo = new ProductVersion(23.09);

    public static void main(String[] args) {
        LauncherImpl.launchApplication(Main.class, LauncherPreloader.class, args);
    }

    public static File browseForCSV(Window window) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));
        // fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));

        return fileChooser.showOpenDialog(window);
    }

    public static File browseForImage(Window window) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files (*.jpg, *.jpeg)", "*.jpg", "*.jpeg"));
        // fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Image Files (*.jpg, *.jpeg)", "*.jpg", "*.jpeg"));

        return fileChooser.showOpenDialog(window);
    }

    @Override
    public void init() throws Exception {
        InitPreloader init = new InitPreloader();
        init.checkFunctions();
    }

    @Override
    public void start(Stage stage) {
        Main.primaryStage = stage;
    }

    public static boolean deleteSongFromList(String isrc) {
        boolean status = songList.remove(isrc);
        if (status) {
            System.out.println("ISRC: " + isrc + " Removed from Song List");
        }
        return status;
    }

    public static Clip getClip() {
        return clip;
    }

    public static void addSongToList(String isrc) {
        songList.add(isrc);
        System.out.println("========================");
        for (String isrcs : songList) {
            System.out.println(isrcs);
        }
    }

    public static List<String> getSongList() {
        return songList;
    }

    public static void directoryCheck() {
        if (selectedDirectory != null) {
            System.out.println(selectedDirectory.getAbsolutePath());
        } else {
            System.out.println("No audio database directory specified");
            selectedDirectory = Main.browseLocation();
            System.out.println(selectedDirectory.getAbsolutePath());
        }
    }

    public static Boolean directoryCheckNew() {
        return selectedDirectory != null;
    }

    public static File getSelectedDirectory() {
        return selectedDirectory;
    }

    static boolean playAudio(Path searchPath, String isrc) throws IOException {
        File file = null;
        System.out.println("searchPath = " + searchPath.toString());
        try (Stream<Path> stream = Files.walk(searchPath)) {
            Platform.runLater(() -> System.out.println("After walking..."));
            Path path = getFileByISRC(isrc, stream);

            if (path != null) {
                file = new File(path.toUri());
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                Platform.runLater(() -> System.out.println("Starting Audio..."));
                clip.start();
                return true;
            } else {
                Platform.runLater(() -> System.out.println("Cannot load file!"));
                return false;
            }

        } catch (SQLException | ClassNotFoundException | LineUnavailableException e) {
            Platform.runLater(() -> {
                throw new RuntimeException(e);
            });
        } catch (UnsupportedAudioFileException e) {
            System.out.println("Unsupported audio");
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
        }
        return false;
    }

    private static Path getFileByISRC(String isrc, Stream<Path> stream) throws SQLException, ClassNotFoundException {
        String fileName = DatabasePostgres.searchFileName(isrc);
        return stream
                .filter(path -> path.toFile().isFile())
                .filter(path -> path.getFileName().toString().equals(fileName))
                .findFirst()
                .orElse(null);
    }

    public static String getAudioDatabaseLocation() throws SQLException, ClassNotFoundException {
        UserSettingsManager userSettingsManager = new UserSettingsManager();
        // Database.createTableForAudioDatabaseLocation();
        String directoryTemp = userSettingsManager.getADB();
        selectedDirectory = new File(directoryTemp);
        return directoryTemp;
    }

    public static File browseLocation() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose a directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            selectedDirectory = chooser.getSelectedFile();
            System.out.println("Selected audio database directory: " + selectedDirectory.getAbsolutePath());
        }
        return selectedDirectory;
    }

    public static File browseLocationNew(javafx.stage.Window ownerWindow) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Save As");

        return chooser.showDialog(ownerWindow);
    }

    public static void copyAudio(String isrc, File directory, File destination) throws SQLException, ClassNotFoundException {
        DatabaseMySQL.searchAndCopySongs(isrc, directory, destination);
//        DatabasePostgre.searchAndCopySongs(isrc, directory, destination);     //Postgress

    }

    public static File browseDestination() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose a directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showOpenDialog(null);
        File selectedDirectory = null;
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedDirectory = chooser.getSelectedFile();
            System.out.println("Selected destination directory: " + selectedDirectory.getAbsolutePath());
        }
        return selectedDirectory;
    }
}

// Invoice
// TODO: 11/27/2023 Edit list in the invoice view

// Song List
// TODO: Export Song List data to a CSV
// TODO: 2/5/2024 Add a delete all button to the list

// User Experience
// TODO: 11/27/2023 Save last invoice details in the database and retrieve when the user is going back to the invoice
// TODO: Offer cancel method after proceed button clicked
// TODO: 12/15/2023 Change alert dialogs of all functions as check missing ISRCs
// TODO: 2/8/2024 System Tray
// TODO: 2/14/2024 Make it play FLACs

// Performance
// TODO: If copy to button clicked and user not chose any location the application starts to search

// Side Panels
// TODO: 12/9/2023 Side-Panel design for all pages

// Revenue Analysis
// TODO: 12/9/2023 Sub views of revenue analysis UI
// TODO: Retrieve artist names from Postgres
// TODO: A view to edit payee list

// Search
// TODO: 2/8/2024 Edit Song Details
// TODO: Change song list retrieval process to Postgres

// Ingests
// TODO: 3/26/2024 Debug Ingest CSV

// Manual Claims
// TODO: Revise ingest CSV file naming
// TODO: Change the progress bar color to red if any error happens to give user a feedback
// TODO: A view to view created ingests
    // TODO: Update Payee table from created ingests upon request
// TODO: Keep track of songs and artists that are not available on the database when adding manual claims
// TODO: Edit details on an added manual claim in the add manual claim view
// TODO: Remove an added manual claim in the add manual claim view
// TODO: Reset add manual claim view after adding a manual claim
// TODO: Tags For Manual Claims List (for Group Claims , Tv Programmes , Covers)
// TODO: Batch Edit Artworks
// TODO: Show artwork in identifier view
// TODO: Batch Paste UPCs
