package com.example.song_finder_fx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Main extends Application {

    static List<String> songList = new ArrayList<>();
    static File selectedDirectory = null;
    static Clip clip;

    public static void main(String[] args) {
        new Thread(() -> launch(args)).start();
    }

    public static boolean deleteSongFromList(String isrc) {
        boolean status = songList.remove(isrc);
        if (status) {
            System.out.println("ISRC: " + isrc + " Removed from Song List");
        }
        return status;
    }

    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        // Loading layout file
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("layouts/main-view.fxml"));
        Scene scene = new Scene(loader.load(), 1030, 610);
        final String[] audioDatabasePath = {null};

        stage.setTitle("CeyMusic Toolkit 2023.2");
        stage.setScene(scene);
        stage.setMinWidth(995);
        stage.setMinHeight(700);

        Image image = new Image("com/example/song_finder_fx/icons/icon.png");

        stage.getIcons().add(image);

        stage.show();

        Task<Void> task;
        task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                audioDatabasePath[0] = getDirectoryFromDB();
                return null;
            }
        };

        Thread t = new Thread(task);
        t.start();
        t.join();

        Platform.runLater(() -> {
            Button btnAudioDatabase = (Button) scene.lookup("#btnAudioDatabase");
            btnAudioDatabase.setText("   Audio Database: " + audioDatabasePath[0]);
        });

        stage.setOnCloseRequest(e -> Platform.exit());
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
        try (Stream<Path> stream = Files.walk(start)) {
            Path path = getFileByISRC(isrc, stream);

            if (path != null) {
                // TODO: Play audio, handle audio player UI
                File file = new File(path.toUri());

                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
                return true;
            } else {
                // TODO: Handle UI showing audio file is missing
                System.out.println("Cannot load file!");
                return false;
            }

        } catch (SQLException | ClassNotFoundException | LineUnavailableException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedAudioFileException e) {
            System.out.println("Unsupported audio");
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

// TODO: 11/27/2023 Report Generator
// TODO: 11/27/2023 Play button in the search song
// TODO: 11/27/2023 Add to list in the search song
// TODO: 11/27/2023 Edit list in the invoice view
// TODO: 11/27/2023 Save last invoice details in the database and retrieve when the user is going back to the invoice
// TODO: Add a separate threads check database
// TODO: Add another VBox to the song-view.fxml to show similar results for the song that user is viewing by song title or something
// TODO: Disable button of the main UI when the proceed button clicked
// TODO: Offer cancel method after proceed button clicked
// TODO: Improve search to search by singer, composer, lyricist, and also search by all at once
// TODO: Most viewed and recently viewed songs
// TODO: Try to add a waveform for audio
// TODO: 11/14/2023 https://youtu.be/V9nDH2iBJSM?si=oCx3NjivV7nBJl8y&t=1022
