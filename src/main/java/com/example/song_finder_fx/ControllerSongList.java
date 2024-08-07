package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Model.Songs;
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
    }

    public void loadList() {
        // Song List
        // List<String> songList = Main.getSongList();
        List<Songs> songListNew = Main.getSongList();

        // Setting up UI
        scrlpneSong.setVisible(true);
        scrlpneSong.setContent(vboxSong);

        Node[] nodes;
        nodes = new Node[songListNew.size()];
        vboxSong.getChildren().clear();

        for (int i = 0; i < nodes.length; i++) {
            try {
                System.out.println("i = " + i);
                Songs songDetail = songListNew.get(i);
                nodes[i] = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("layouts/song-songlist.fxml")));
                Label lblSongName = (Label) nodes[i].lookup("#srchRsSongName");
                Label lblISRC = (Label) nodes[i].lookup("#srchRsISRC");
                Label lblArtist = (Label) nodes[i].lookup("#srchRsArtist");
                lblSongName.setText(songDetail.getTrackTitle());
                lblISRC.setText(songDetail.getISRC());
                lblArtist.setText(songDetail.getSinger());
                vboxSong.getChildren().add(nodes[i]);
            } catch (IOException e) {
                AlertBuilder.sendErrorAlert("Error", "Error Loading Layout", e.toString());
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
                        controllerInvoice.loadThings();
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
