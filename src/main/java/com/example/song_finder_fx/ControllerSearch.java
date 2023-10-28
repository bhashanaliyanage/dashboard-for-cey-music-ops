package com.example.song_finder_fx;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class ControllerSearch {
    private final UIController uiController;
    public TextField searchArea;
    public ScrollPane scrlpneSong;
    public VBox vboxSong;
    public VBox vboxSongSearch;
    public VBox vBoxInSearchSong;
    public Label searchResultSongName;
    public Label searchResultISRC;
    public Label searchResultArtist;
    public Label songNameViewTitle;
    public Label songName;
    public Label songISRC;
    public Label songSinger;
    public Label songComposer;
    public Label songLyricist;
    public Label songUPC;
    public Label songProductName;
    public Label songShare;
    public ImageView songArtwork;
    public ImageView btnPlay;
    public Button btnAddToList;
    public Button btnOpenLocation;
    public Button btnCopyTo;

    public ControllerSearch(UIController uiController) {
        this.uiController = uiController;
    }

    public void loadThingsTempForISRC() throws ClassNotFoundException {
        Connection con = uiController.checkDatabaseConnection();

        if (con != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("layouts/search-details-by-isrc.fxml"));
                loader.setController(this);
                Parent newContent = loader.load();
                uiController.mainVBox.getChildren().clear();
                uiController.mainVBox.getChildren().add(newContent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("onSearchDetailsButtonClick");
        } else {
            UIController.showErrorDialog("Database Connection Error!", "Error Connecting to Database", "Please check your XAMPP server up and running");
        }
    }

    public void loadThingsTempForSongName() throws ClassNotFoundException {
        Connection con = uiController.checkDatabaseConnection();

        if (con != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("layouts/search-details.fxml"));
                loader.setController(this);
                Parent newContent = loader.load();
                uiController.mainVBox.getChildren().clear();
                uiController.mainVBox.getChildren().add(newContent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("onSearchDetailsButtonClick");
        } else {
            UIController.showErrorDialog("Database Connection Error!", "Error Connecting to Database", "Please check your XAMPP server up and running");
        }
    }

    public void getTextForISRC() throws IOException {
        // Getting search keywords
        String text = searchArea.getText();

        // Connecting to database
        DatabaseMySQL db = new DatabaseMySQL();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("layouts/song-view.fxml"));
        loader.load();

        scrlpneSong.setVisible(true);
        scrlpneSong.setContent(vboxSong);

        Task<List<Songs>> task = new Task<>() {
            @Override
            protected java.util.List<Songs> call() throws Exception {
                return db.searchSongNamesByISRC(text);
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
                    Label lblArtist = (Label) nodes[i].lookup("#srchRsArtist");
                    lblSongName.setText(songList.get(i).getSongName());
                    lblISRC.setText(songList.get(i).getISRC().trim());
                    lblArtist.setText(songList.get(i).getSinger().trim());
                    vboxSongSearch.getChildren().add(nodes[i]);
                } catch (NullPointerException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Thread thread = new Thread(task);
        thread.start();
    }

    public void onSearchedSongClick(MouseEvent mouseEvent) throws SQLException, ClassNotFoundException, IOException {
        String name = searchResultSongName.getText();
        String isrc = searchResultISRC.getText();

        DatabaseMySQL db = new DatabaseMySQL();
        List<String> songDetails = db.searchSongDetails(isrc);

        // For reference
        System.out.println("Song name: " + name);
        System.out.println("ISRC: " + isrc);

        // Getting the current parent layout
        FXMLLoader loader = new FXMLLoader(getClass().getResource("layouts/song-view.fxml"));
        loader.setController(this);
        Parent newContent = loader.load();
        uiController.mainVBox.getChildren().clear();

        // Setting the new layout
        uiController.mainVBox.getChildren().add(newContent);

        // Setting values for labels
        songISRC.setText(songDetails.get(0));
        songProductName.setText(songDetails.get(1));
        songUPC.setText(songDetails.get(2));
        songName.setText(songDetails.get(3));
        songNameViewTitle.setText(songDetails.get(3));
        songSinger.setText(songDetails.get(4));
        songComposer.setText(songDetails.get(6));
        songLyricist.setText(songDetails.get(7));
        songShare.setText("No Detail");
    }

    public void getText(KeyEvent keyEvent) {
        // Getting search keywords
        String text = searchArea.getText();

        // Connecting to database
        DatabaseMySQL db = new DatabaseMySQL();

        scrlpneSong.setVisible(true);
        scrlpneSong.setContent(vboxSong);

        Task<java.util.List<Songs>> task = new Task<>() {
            @Override
            protected java.util.List<Songs> call() throws Exception {
                return db.searchSongDetailsBySongName(text);
            }
        };

        task.setOnSucceeded(e -> {
            List<Songs> songList = task.getValue();
            Node[] nodes;
            nodes = new Node[songList.size()];
            vboxSong.getChildren().clear();
            for (int i = 0; i < nodes.length; i++) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("layouts/search-song.fxml"));
                    loader.setController(this);
                    nodes[i] = loader.load();
                    searchResultSongName.setText(songList.get(i).getSongName());
                    searchResultISRC.setText(songList.get(i).getISRC().trim());
                    searchResultArtist.setText(songList.get(i).getSinger().trim());
                    vboxSong.getChildren().add(nodes[i]);
                } catch (NullPointerException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Thread thread = new Thread(task);
        thread.start();
    }

    public void onBtnPlayClicked(MouseEvent mouseEvent) {
    }

    public void onAddToListButtonClicked(ActionEvent actionEvent) {
    }

    public void onOpenFileLocationButtonClicked(MouseEvent mouseEvent) {
    }

    public void onCopyToButtonClicked(MouseEvent mouseEvent) {
    }
}
