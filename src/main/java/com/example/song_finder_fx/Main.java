package com.example.song_finder_fx;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
    public static Double PRODUCT_VERSION = 23.02;
    public static Stage primaryStage = null;
    static List<String> songList = new ArrayList<>();
    static File selectedDirectory = null;
    static Clip clip;

    public static void main(String[] args) {
        LauncherImpl.launchApplication(Main.class, LauncherPreloader.class, args);
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
// TODO: 12/9/2023 Update the design to show new updates
// TODO: 12/9/2023 Sub views of revenue analysis UI
// TODO: 11/27/2023 Edit list in the invoice view
// TODO: 11/27/2023 Save last invoice details in the database and retrieve when the user is going back to the invoice
// TODO: Offer cancel method after proceed button clicked
// TODO: 12/9/2023 Revenue analysis UI Size

