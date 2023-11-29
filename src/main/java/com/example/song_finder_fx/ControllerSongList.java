package com.example.song_finder_fx;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class ControllerSongList {
    private final UIController mainUIController;
    public ScrollPane scrlpneSong;
    public VBox vboxSong;
    public Label lblListCount;
    public Button btnCopyTo;

    public ControllerSongList(UIController mainUIController) {
        this.mainUIController = mainUIController;
    }

    public void loadThings() throws ClassNotFoundException {
        Connection con = mainUIController.checkDatabaseConnection();

        if (con != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("layouts/song-list.fxml"));
                loader.setController(this);
                Parent newContent = loader.load();
                mainUIController.mainVBox.getChildren().clear();
                mainUIController.mainVBox.getChildren().add(newContent);

                loadList();

                int listSize = Main.getSongList().size();
                lblListCount.setText("Total: " + listSize);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            UIController.showErrorDialog(
                    "Database Connection Error!",
                    "Error Connecting to Database",
                    "Please check your XAMPP server up and running");
        }
    }

    public void loadList() {
        // Song List
        List<String> songList = Main.getSongList();

        // Connecting to database
        DatabaseMySQL db = new DatabaseMySQL();

        // Setting up UI
        scrlpneSong.setVisible(true);
        scrlpneSong.setContent(vboxSong);

        Node[] nodes;
        nodes = new Node[songList.size()];
        vboxSong.getChildren().clear();

        for (int i = 0; i < nodes.length; i++) {
            try {
                List<String> songDetail = db.searchSongDetails(songList.get(i));
                nodes[i] = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("layouts/song-songlist.fxml")));
                Label lblSongName = (Label) nodes[i].lookup("#srchRsSongName");
                Label lblISRC = (Label) nodes[i].lookup("#srchRsISRC");
                Label lblArtist = (Label) nodes[i].lookup("#srchRsArtist");
                lblSongName.setText(songDetail.get(3));
                lblISRC.setText(songDetail.get(0));
                lblArtist.setText(songDetail.get(4));
                vboxSong.getChildren().add(nodes[i]);
            } catch (NullPointerException | IOException ex) {
                ex.printStackTrace();
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void onCopyToButtonClicked() {
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

                        task.setOnSucceeded(event -> {
                            updateButtonProceed("Copy List to Location");

                            if (!DatabaseMySQL.errorBuffer.isEmpty()) {
                                ErrorDialog.showErrorDialogWithLog("File Not Found Error", "Error! Some files are missing in your audio database", DatabaseMySQL.errorBuffer.toString());
                            }

                            NotificationBuilder nb = new NotificationBuilder();

                            try {
                                nb.displayTrayInfo("Execution Completed", "Please check your destination folder for the copied audio files");
                            } catch (AWTException exception) {
                                throw new RuntimeException(exception);
                            }
                        });

                        new Thread(task).start();
                    }
                }
            } else {
                System.out.println("The directory does not exist.");
                btnCopyTo.setText("Error! Please setCountry audio database location in settings");
                btnCopyTo.setStyle("-fx-border-color: '#931621'");
            }
        }
    }

    public void onAddMoreButtonClicked(ActionEvent actionEvent) throws IOException {
        Node node = (Node) actionEvent.getSource();
        Scene scene = node.getScene();
        VBox mainVBox = (VBox) scene.lookup("#mainVBox");
        mainVBox.getChildren().clear();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("layouts/search-details.fxml"));
        Parent newContent = loader.load();
        mainVBox.getChildren().add(newContent);
    }

    public void onGenerateInvoiceButtonClicked(ActionEvent actionEvent) {
        ControllerInvoice controllerInvoice = new ControllerInvoice(mainUIController);
        Node node = (Node) actionEvent.getSource();
        Scene scene = node.getScene();
        Button btnGenerateInvoice = (Button) scene.lookup("#btnGenerateInvoice");
        btnGenerateInvoice.setText("Loading...");
        Thread t = loadInvoicePageThread(actionEvent, controllerInvoice);
        t.start();
    }

    private static Thread loadInvoicePageThread(ActionEvent actionEvent, ControllerInvoice controllerInvoice) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    try {
                        controllerInvoice.loadThings(actionEvent);
                    } catch (IOException | SQLException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
                return null;
            }
        };

        return new Thread(task);
    }

    private void updateButtonProceed(String s) {
        btnCopyTo.setText(s);
    }
}
