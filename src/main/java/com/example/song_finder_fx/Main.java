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
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    static List<String> songList = new ArrayList<>();
    static File selectedDirectory = null;

    public static void main(String[] args) {
        new Thread(() -> launch(args)).start();
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

    public String searchAudios(String ISRCs, File directory, File destination) throws SQLException, ClassNotFoundException, AWTException {
        String[] ISRCCodes = ISRCs.split("\\n");
        DatabaseMySQL.SearchSongsFromDB(ISRCCodes, directory, destination);
        NotificationBuilder nb = new NotificationBuilder();
        nb.displayTrayInfo("Execution Completed", "Please check your destination folder for the copied audio files");
        return "Done";
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

// TODO: Search by ISRC
// TODO: Listen audios
// TODO: Remember last entered database location, and destination location
// TODO: Show a report of copied files, what are not found,
//  offer a button to export the file names/ send an email to an admin to collect those audio files
// TODO: Check current progress with test cases
// TODO: Adding admin switch
// TODO: Add a function to open output folder when done
// TODO: Make about section
// TODO: Add singer's name when searching songs
// TODO: Handle database not connected error
// TODO: Add a place to show the featuring artist in song-view.fxml
// TODO: Implement a column in database to put CeyMusic share
// TODO: Add another VBox to the song-view.fxml to show similar results for the song that user is viewing by song title or something
// TODO: In DatabaseMySQL, make SearchSongsFromDB method uses MySQL database
// TODO: Keyboard movement handling for search
