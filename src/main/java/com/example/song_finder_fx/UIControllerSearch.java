package com.example.song_finder_fx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class UIControllerSearch {

    public VBox textAreaVbox;
    public TextField searchArea;
    public VBox vboxSong;
    public Label searchResult;
    public Label srchRsSongName;
    public Label srchRsISRC;
    public HBox searchAndCollect;
    public ImageView ProgressView;
    public Button btnProceed;

    // Search Items
    public void getText(KeyEvent inputMethodEvent) throws SQLException, ClassNotFoundException {
        String text = searchArea.getText();
        DatabaseMySQL db = new DatabaseMySQL();
        ArrayList<String> songList;
        songList = db.searchSongNames(text);
        Node[] nodes = new Node[songList.size()];

        // FXMLLoader loader = new FXMLLoader(getClass().getResource("layouts/search-song.fxml"));
        // Parent newContent = loader.load();

        vboxSong.getChildren().clear();
        for (int i = 0; i < nodes.length; i++) {
            try {
                nodes[i] = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("layouts/search-song.fxml")));
                vboxSong.getChildren().add(nodes[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println(songList.size());

        System.out.println(text);
    }
}
