package com.example.song_finder_fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
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
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("main-view.fxml"));
        Scene scene = new Scene(loader.load(), 1030, 610);

        stage.setTitle("CeyMusic Toolkit 2023.1");
        stage.setScene(scene);
        stage.setMinWidth(995);
        stage.setMinHeight(650);

        Image image = new Image("com/example/song_finder_fx/icons/icon.png");

        stage.getIcons().add(image);

        /*FXMLLoader loader_sub = new FXMLLoader(getClass().getResource("search-details.fxml"));
        Parent newContent = loader_sub.load();
        UIController controller = loader.getController();

        controller.mainVBox.getChildren().clear();
        controller.mainVBox.getChildren().add(newContent);*/

        stage.show();
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

    public String searchAudios(String ISRCs, File directory, File destination) throws SQLException, ClassNotFoundException, IOException {
        String[] ISRCCodes = ISRCs.split("\\n");
        Database.SearchSongsFromDB(ISRCCodes, directory, destination);
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

// TODO: Remember last entered database location, and destination location
// TODO: Show a report of copied files, what are not found,
//  offer a button to export the file names/ send an email to an admin to collect those audio files
// TODO: Implement tabs
// TODO: Implement get song data by ISRCs
// TODO: Check current progress with test cases
// TODO: Using firebase
// TODO: Adding admin switch
