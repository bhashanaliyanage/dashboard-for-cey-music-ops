package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.ErrorDialog;
import com.example.song_finder_fx.Controller.NotificationBuilder;
import com.example.song_finder_fx.Model.Songs;
import com.opencsv.CSVWriter;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ControllerSongListNew {

    @FXML
    private Button btnAddMore;

    @FXML
    private Button btnCopyTo;

    @FXML
    private Button btnGenerateInvoice;

    @FXML
    private VBox hboxListActions;

    @FXML
    private Label lblListCount;

    @FXML
    private VBox mainVBox;

    @FXML
    private ScrollPane scrlpneSong;

    @FXML
    private VBox vBoxInSearchSong;

    @FXML
    private VBox vboxSong;

    @FXML
    void initialize() {
        // Song List
        // List<String> songList = Main.getSongList();
        List<Songs> songListNew = Main.getSongList();

        // Setting up UI
        scrlpneSong.setVisible(true);
        scrlpneSong.setContent(vboxSong);

        Node[] nodes;
        nodes = new Node[songListNew.size()];
        vboxSong.getChildren().clear();

        int listSize = Main.getSongList().size();
        lblListCount.setText(String.valueOf(listSize));

        for (int i = 0; i < nodes.length; i++) {
            try {
                System.out.println("i = " + i);
                Songs songDetail = songListNew.get(i);
                nodes[i] = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("layouts/song-songlist.fxml")));
                Label lblSongNumber = (Label) nodes[i].lookup("#songNumber");
                Label lblSongName = (Label) nodes[i].lookup("#srchRsSongName");
                Label lblISRC = (Label) nodes[i].lookup("#srchRsISRC");
                Label lblArtist = (Label) nodes[i].lookup("#srchRsArtist");
                lblSongNumber.setText(String.valueOf(i + 1));
                lblSongName.setText(songDetail.getTrackTitle());
                lblISRC.setText(songDetail.getISRC());
                lblArtist.setText(songDetail.getSinger());
                vboxSong.getChildren().add(nodes[i]);
            } catch (NullPointerException | IOException ex) {
                AlertBuilder.sendErrorAlert("Error", "Error Loading Layout", ex.toString());
            }
        }
    }

    @FXML
    void onAddMoreButtonClicked(ActionEvent actionEvent) {
        Node node = (Node) actionEvent.getSource();
        Scene scene = node.getScene();
        VBox mainVBox = (VBox) scene.lookup("#mainVBox");
        mainVBox.getChildren().clear();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("layouts/search-details.fxml"));
        Parent newContent = null;
        try {
            newContent = loader.load();
        } catch (IOException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Loading View", e.toString());
        }
        mainVBox.getChildren().add(newContent);
    }

    @FXML
    void onCopyToButtonClicked(ActionEvent event) {
        Task<Void> task;
        // List<String> songList = Main.getSongList();
        List<Songs> songListNew = Main.getSongList();
        if (songListNew.isEmpty()) {
            btnCopyTo.setText("Please add song(s) to list to proceed");
            btnCopyTo.setStyle("-fx-border-color: '#931621'");
        } else {
            File selectedDirectory = Main.getSelectedDirectory();

            if (selectedDirectory.exists()) { // check if the directory exists
                if (selectedDirectory.isDirectory()) { // check if the directory is a directory
                    if (selectedDirectory.canWrite()) { // check if the directory is writable
                        System.out.println("The directory is accessible and writable.");
                        File destination = Main.browseDestination();

                        task = new Task<>() {
                            @Override
                            protected Void call() throws Exception {
                                int songListSize = songListNew.size();

                                for (int i = 0; i < songListSize; i++) {
                                    String isrc = songListNew.get(i).getISRC();

                                    int finalI = i;
                                    Platform.runLater(() -> updateButtonProceed("Copying " + (finalI + 1) + " of " + songListSize));
                                    Main.copyAudio(isrc, selectedDirectory, destination);
                                }
                                return null;
                            }
                        };

                        task.setOnSucceeded(ActionEvent -> {
                            updateButtonProceed("Copy List to Location");

                            if (!DatabaseMySQL.errorBuffer.isEmpty()) {
                                ErrorDialog.showErrorDialogWithLog("File Not Found Error", "Error! Some files are missing in your audio database", DatabaseMySQL.errorBuffer.toString());
                            }

                            try {
                                NotificationBuilder.displayTrayInfo("Execution Completed", "Please check your destination folder for the copied audio files");
                            } catch (AWTException exception) {
                                throw new RuntimeException(exception);
                            }
                        });

                        new Thread(task).start();
                    }
                }
            } else {
                System.out.println("The directory does not exist.");
                btnCopyTo.setText("Error! Please set audio database location in settings");
                btnCopyTo.setStyle("-fx-border-color: '#931621'");
            }
        }
    }

    private void updateButtonProceed(String s) {
        btnCopyTo.setText(s);
    }

    @FXML
    void onGenerateInvoiceButtonClicked(ActionEvent event) {
        Scene scene;
        try {
            FXMLLoader loader = new FXMLLoader(ControllerInvoice.class.getResource("layouts/song-list-invoice.fxml"));
            Parent newContent = loader.load();
            Node node = (Node) event.getSource();
            scene = node.getScene();
            VBox mainVBox = (VBox) scene.lookup("#mainVBox");
            mainVBox.getChildren().setAll(newContent);
        } catch (IOException e) {
            AlertBuilder.sendErrorAlert("Error!", "Error Loading Invoice", e.toString());
        }
    }

    public void onExportSongList(ActionEvent actionEvent) throws AWTException {
        System.out.println("\nControllerSongListNew.onExportSongList");
        List<Songs> songList = Main.getSongListNew();

        // Getting User Location
        Node node = (Node) actionEvent.getSource();
        Scene scene = node.getScene();
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv"));
        chooser.setTitle("Save As");
        File pathTo = chooser.showSaveDialog(scene.getWindow());
        String path = pathTo.getAbsolutePath();
        System.out.println("File Destination: " + path);

        // Creating CSV File
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(path));
            List<String[]> rows = new ArrayList<>();
            String[] header = new String[] {"Product Title","UPC","Track Title","ISRC","Singer","Lyrics","Composer","File Name"};
            rows.add(header);

            for (Songs song :
                    songList) {
                String[] row = getRow(song);
                rows.add(row);
            }

            writer.writeAll(rows);
            writer.close();
        } catch (IOException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Creating CSV File", e.toString());
        }

    }

    @NotNull
    private static String[] getRow(Songs song) {
        String productTitle = song.getProductName();
        String upc = song.getUPC();
        String trackTitle = song.getTrackTitle();
        String isrc = song.getISRC();
        String singer = song.getSinger();
        String lyrics = song.getLyricist();
        String composer = song.getComposer();
        String fileName = song.getFileName();

        String[] row = new String[]{productTitle, upc, trackTitle, isrc, singer, lyrics, composer, fileName};
        return row;
    }
}
