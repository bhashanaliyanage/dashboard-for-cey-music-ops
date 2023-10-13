package com.example.song_finder_fx;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class UIControllerSearch {

    public VBox textAreaVbox;
    @FXML
    public TextField searchArea;
    public VBox vboxSong;
    public ScrollPane scrlpneSong;
    public Label srchRsSongName;
    public Label srchRsISRC;

    // Search Items
    public void getText() {
        // Getting search keywords
        String text = searchArea.getText();

        // Connecting to database
        DatabaseMySQL db = new DatabaseMySQL();

        scrlpneSong.setVisible(true);
        scrlpneSong.setContent(vboxSong);

        Task<List<Songs>> task = new Task<>() {
            @Override
            protected List<Songs> call() throws Exception {
                return db.searchSongNames(text);
            }
        };

        task.setOnSucceeded(e -> {
            List<Songs> songList = task.getValue();
            Node[] nodes;
            nodes = new Node[songList.size()];
            vboxSong.getChildren().clear();
            for (int i = 0; i < nodes.length; i++) {
                try {
                    nodes[i] = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("layouts/search-song.fxml")));
                    Label lblSongName = (Label) nodes[i].lookup("#srchRsSongName");
                    Label lblISRC = (Label) nodes[i].lookup("#srchRsISRC");
                    lblSongName.setText(songList.get(i).getSongName());
                    lblISRC.setText(songList.get(i).getISRC().trim());
                    vboxSong.getChildren().add(nodes[i]);
                } catch (NullPointerException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Thread thread = new Thread(task);
        thread.start();
    }

    public void onSearchedSongClick(MouseEvent mouseEvent) {
        String name = srchRsSongName.getText();
        String isrc = srchRsISRC.getText();
        System.out.println("Song name: " + name);
        System.out.println("ISRC: " + isrc);
    }
}
