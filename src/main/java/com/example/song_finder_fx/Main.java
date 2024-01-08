package com.example.song_finder_fx;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
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
    public static Double PRODUCT_VERSION = 23.04;
    public static Stage primaryStage = null;
    static List<String> songList = new ArrayList<>();
    static File selectedDirectory = null;
    static Clip clip;

    public static void main(String[] args) {
        LauncherImpl.launchApplication(Main.class, LauncherPreloader.class, args);
    }

    public static File browseForFile(Window window) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CSV");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("TXT files (*.csv)", "*.csv"));

        return fileChooser.showOpenDialog(window);
    }

    @Override
    public void init() throws Exception {
        InitPreloader init = new InitPreloader();
        init.checkFunctions();
    }

    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
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

    static boolean playAudio(Path start, String isrc) throws IOException {
        File file = null;
        try (Stream<Path> stream = Files.walk(start)) {
            Path path = getFileByISRC(isrc, stream);

            if (path != null) {
                file = new File(path.toUri());

                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
                return true;
            } else {
                System.out.println("Cannot load file!");
                return false;
            }

        } catch (SQLException | ClassNotFoundException | LineUnavailableException e) {
            // return false;
            throw new RuntimeException(e);
        } catch (UnsupportedAudioFileException e) {
            System.out.println("Unsupported audio");
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
        }
        return false;
    }

    private static Path getFileByISRC(String isrc, Stream<Path> stream) throws SQLException, ClassNotFoundException {
        String fileName = DatabaseMySQL.searchFileName(isrc);
        return stream
                .filter(path -> path.toFile().isFile())
                .filter(path -> path.getFileName().toString().equals(fileName))
                .findFirst()
                .orElse(null);
    }

    public static String getDirectoryFromDB() throws SQLException, ClassNotFoundException {
        Database.createTableForAudioDatabaseLocation();
        String directoryTemp = Database.searchForAudioDB();
        selectedDirectory = new File(directoryTemp);
        return directoryTemp;
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

    public static File browseLocationNew(javafx.stage.Window ownerWindow) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Save As");

        return chooser.showDialog(ownerWindow);
    }

    public static void copyAudio(String isrc, File directory, File destination) throws SQLException, ClassNotFoundException {
        DatabaseMySQL.searchAndCopySongs(isrc, directory, destination);
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

// TODO: 12/9/2023 Side-Panel design for all pages
// TODO: 12/9/2023 Sub views of revenue analysis UI
// TODO: 11/27/2023 Edit list in the invoice view
// TODO: 11/27/2023 Save last invoice details in the database and retrieve when the user is going back to the invoice
// TODO: Offer cancel method after proceed button clicked
// TODO: 12/14/2023 In the check missing ISRC button, it doesn't show results after the program is built
// TODO: 12/15/2023 Change alert dialogs of all functions as check missing ISRCs
// TODO: 12/15/2023 Set not to show alert dialog when there's no missing ISRCs in check missing ISRC function
// TODO: If copy to button clicked and user not chose any location the application starts to search

// TODO: Import Report
// TODO: Missing ISRC > Song Database Update
// TODO: Search Song Database and Assign Payees
// TODO: Give a list of Missing Payees
//  TODO: Update Payees (Manual Process)
