package com.example.song_finder_fx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;
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

    // Search Items
    public void getText() throws SQLException, ClassNotFoundException {
        // Printing new line
        System.out.println("========");

        // Getting search keywords
        String text = searchArea.getText();

        // Connecting to database
        DatabaseMySQL db = new DatabaseMySQL();
        List<Songs> songList = db.searchSongNames(text);

        Node[] nodes = new Node[songList.size()];

        vboxSong.getChildren().clear();
        for (int i = 0; i < nodes.length; i++) {
            try {
                nodes[i] = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("layouts/search-song.fxml")));
                Label lblSongName = (Label) nodes[i].lookup("#srchRsSongName");
                Label lblISRC = (Label) nodes[i].lookup("#srchRsISRC");
                lblSongName.setText(songList.get(i).getSongName());
                lblISRC.setText(songList.get(i).getISRC().trim());
                vboxSong.getChildren().add(nodes[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
