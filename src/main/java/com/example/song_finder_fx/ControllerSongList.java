package com.example.song_finder_fx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
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

    public void songListTestButton() {
        // Getting search keywords
        String songName = "Mawathe Geethaya";
        List<String> songList = Main.getSongList();

        // Connecting to database
        DatabaseMySQL db = new DatabaseMySQL();

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
}
