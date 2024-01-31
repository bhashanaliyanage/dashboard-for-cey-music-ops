package com.example.song_finder_fx;

import com.example.song_finder_fx.Model.Search;
import com.example.song_finder_fx.Model.Songs;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ControllerSearch {
    @FXML
    private Label lblSearchType;

    @FXML
    private ScrollPane scrlpneSong;

    @FXML
    private TextField searchArea;

    @FXML
    private VBox vboxSong;
    private final Search search = new Search();
    public ControllerSearch() {}

    @FXML
    void btnSetSearchTypeComposer() {
        lblSearchType.setText("Composer");
        search.setType("COMPOSER");
    }

    @FXML
    void btnSetSearchTypeISRC() {
        lblSearchType.setText("ISRC");
        search.setType("ISRC");
    }

    @FXML
    void btnSetSearchTypeLyricist() {
        lblSearchType.setText("Lyricist");
        search.setType("LYRICIST");
    }

    @FXML
    void btnSetSearchTypeName() {
        lblSearchType.setText("Name");
        search.setType("TRACK_TITLE");
    }

    @FXML
    void btnSetSearchTypeSinger() {
        lblSearchType.setText("Singer");
        search.setType("SINGER");
    }

    @FXML
    void getText(KeyEvent event) throws IOException {
        // Getting search keywords
        String text = searchArea.getText();

        scrlpneSong.setVisible(true);
        scrlpneSong.setContent(vboxSong);

        Task<List<Songs>> task = new Task<>() {
            @Override
            protected java.util.List<Songs> call() throws Exception {
                return search.search(text);
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
                    Label lblSongName = (Label) nodes[i].lookup("#songName");
                    Label lblISRC = (Label) nodes[i].lookup("#searchResultISRC");
                    Label lblArtist = (Label) nodes[i].lookup("#songSinger");
                    Label lblComposer = (Label) nodes[i].lookup("#searchResultComposer");
                    Label lblLyricist = (Label) nodes[i].lookup("#searchResultLyricist");
                    lblSongName.setText(songList.get(i).getTrackTitle());
                    lblISRC.setText(songList.get(i).getISRC().trim());
                    lblArtist.setText(songList.get(i).getSinger().trim());
                    lblComposer.setText(songList.get(i).getComposer().trim());
                    lblLyricist.setText(songList.get(i).getLyricist().trim());
                    vboxSong.getChildren().add(nodes[i]);
                } catch (NullPointerException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Thread thread = new Thread(task);
        thread.start();
    }
}
