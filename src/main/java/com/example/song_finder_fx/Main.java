package com.example.song_finder_fx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class Main extends Application {

    public static void main(String[] args) {
        new Thread(() -> launch(args)).start();
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Loading layout file
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("layouts/main-view.fxml"));
        Scene scene = new Scene(loader.load(), 1030, 610);

        stage.setTitle("CeyMusic Toolkit 2023.1");
        stage.setScene(scene);
        stage.setMinWidth(995);
        stage.setMinHeight(650);

        Image image = new Image("com/example/song_finder_fx/icons/icon.png");

        stage.getIcons().add(image);

        stage.show();

        stage.setOnCloseRequest(e -> Platform.exit());
    }

    public File browseFile() {
        FileChooser fileChooser = new FileChooser();

        return fileChooser.showOpenDialog(null);
    }

    public File browseLocation() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose a directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showOpenDialog(null);
        File selectedDirectory = null;
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedDirectory = chooser.getSelectedFile();
            System.out.println("Selected audio database directory: " + selectedDirectory.getAbsolutePath());
        }
        return selectedDirectory;
    }

    public String searchAudios(String ISRCs, File directory, File destination) throws SQLException, ClassNotFoundException, AWTException {
        String[] ISRCCodes = ISRCs.split("\\n");
        DatabaseMySQL.SearchSongsFromDB(ISRCCodes, directory, destination);
        NotificationBuilder nb = new NotificationBuilder();
        nb.displayTray();
        return "Done";
    }

    public File browseDestination() {
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

// TODO: Search by ISRC
// TODO: Listen audios
// TODO: Remember last entered database location, and destination location
// TODO: Show a report of copied files, what are not found,
//  offer a button to export the file names/ send an email to an admin to collect those audio files
// TODO: Check current progress with test cases
// TODO: Adding admin switch
// TODO: Add a function to open output folder when done
// TODO: Make about section