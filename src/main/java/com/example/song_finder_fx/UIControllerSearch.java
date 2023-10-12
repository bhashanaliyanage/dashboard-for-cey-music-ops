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
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
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
    public Button btnProceed;

    // Search Items
    public void getText(KeyEvent inputMethodEvent) throws SQLException, ClassNotFoundException {
        String text = searchArea.getText();
        DatabaseMySQL db = new DatabaseMySQL();
        ArrayList<String> songList = db.searchSongNames(text);

        List<Songs> songs = new ArrayList<>();
        songs.add(new Songs("Kaari Na Sanda", "Methun SK"));
        Node[] nodes = new Node[songs.size()];

        // FXMLLoader loader = new FXMLLoader(getClass().getResource("layouts/search-song.fxml"));
        // Parent newContent = loader.load();

        vboxSong.getChildren().clear();
        for (int i = 0; i < nodes.length; i++) {
            try {
                nodes[i] = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("layouts/search-song.fxml")));
                Label lblSongName = (Label) nodes[i].lookup("#srchRsSongName");
                Label lblISRC = (Label) nodes[i].lookup("#srchRsISRC");
                lblSongName.setText(songs.get(i).getSongName());
                lblISRC.setText(songs.get(i).getISRC());
                vboxSong.getChildren().add(nodes[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println(songList.size());

        System.out.println(text);
    }
}
