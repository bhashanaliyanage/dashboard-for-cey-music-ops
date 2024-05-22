package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.ErrorDialog;
import com.example.song_finder_fx.Controller.NotificationBuilder;
import com.example.song_finder_fx.Model.Songs;
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

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
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
        List<String> songList = Main.getSongList();

        // Setting up UI
        scrlpneSong.setVisible(true);
        scrlpneSong.setContent(vboxSong);

        Node[] nodes;
        nodes = new Node[songList.size()];
        vboxSong.getChildren().clear();

        int listSize = Main.getSongList().size();
        lblListCount.setText(String.valueOf(listSize));

        for (int i = 0; i < nodes.length; i++) {
            try {
                System.out.println("i = " + i);
                Songs songDetail = DatabaseMySQL.searchSongDetails(songList.get(i));
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
                ex.printStackTrace();
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    void onAddMoreButtonClicked(ActionEvent event) {

    }

    @FXML
    void onCopyToButtonClicked(ActionEvent event) {
        Task<Void> task;
        List<String> songList = Main.getSongList();
        if (songList.isEmpty()) {
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
                                int songListSize = songList.size();

                                for (int i = 0; i < songListSize; i++) {
                                    String isrc = songList.get(i);

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
    void onGenerateInvoiceButtonClicked(ActionEvent event) throws IOException {
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

}
