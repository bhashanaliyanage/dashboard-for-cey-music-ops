package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Model.Songs;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.sql.SQLException;
import java.util.List;

public class ControllerSearchSong {

    @FXML
    private Button btnCopy;

    @FXML
    private ImageView btnPlay;

    @FXML
    private ImageView btnPlay1;

    @FXML
    private HBox hbox2;

    @FXML
    private HBox hboxSongSearch;

    @FXML
    private MenuItem miComposer;

    @FXML
    private MenuItem miFeaturing;

    @FXML
    private MenuItem miLyricist;

    @FXML
    private MenuItem miProductName;

    @FXML
    private MenuItem miSinger;

    @FXML
    private MenuItem miSongName;

    @FXML
    private MenuItem miUPC;

    @FXML
    private MenuItem mi_ISRC;

    @FXML
    private Label searchResultComposer;

    @FXML
    private Label searchResultISRC;

    @FXML
    private Label searchResultLyricist;

    @FXML
    private Label songName;

    @FXML
    private Label songSinger;

    @FXML
    private Label songType;

    @FXML
    private VBox vboxSongDetails;

    @FXML
    private VBox vboxSongSearch;

    Songs songDetails;

    @FXML
    void contextM(ActionEvent event) {
        System.out.println("ControllerSearch.contextM");
        String isrc = searchResultISRC.getText();
        try {
            songDetails = DatabasePostgres.searchSongDetails(isrc);
            System.out.println("Fetched song details!");
        } catch (SQLException | ClassNotFoundException e) {
            AlertBuilder.sendErrorAlert("Error!", "Error Occurred While Fetching Song", e.toString());
        }
    }

    @FXML
    void copyComposer(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();

        Songs song = new Songs("null", "null");
        try {
            song = DatabasePostgres.searchSongDetails(searchResultISRC.getText());
        } catch (SQLException | ClassNotFoundException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Fetching Song Details", e.toString());
        }

        content.putString(song.getComposer());
        boolean status = clipboard.setContent(content);

        System.out.println("status = " + status);

        if (status) {
            btnCopy.setText("Copied");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> btnCopy.setText("Copy")));
            timeline.play();
        }
    }

    @FXML
    void copyFeaturing(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();

        Songs song = new Songs("null", "null");
        try {
            song = DatabasePostgres.searchSongDetails(searchResultISRC.getText());
        } catch (SQLException | ClassNotFoundException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Fetching Song Details", e.toString());
        }

        content.putString(song.getFeaturing());
        boolean status = clipboard.setContent(content);
        System.out.println("status = " + status);
        if (status) {
            btnCopy.setText("Copied");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> btnCopy.setText("Copy")));
            timeline.play();
        }
    }

    @FXML
    void copyISRC(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();

        Songs song = new Songs("null", "null");
        try {
            song = DatabasePostgres.searchSongDetails(searchResultISRC.getText());
        } catch (SQLException | ClassNotFoundException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Fetching Song Details", e.toString());
        }

        content.putString(song.getISRC());
        boolean status = clipboard.setContent(content);
        System.out.println("status = " + status);
        if (status) {
            btnCopy.setText("Copied");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> btnCopy.setText("Copy")));
            timeline.play();
        }
    }

    @FXML
    void copyLyricist(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();

        Songs song = new Songs("null", "null");
        try {
            song = DatabasePostgres.searchSongDetails(searchResultISRC.getText());
        } catch (SQLException | ClassNotFoundException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Fetching Song Details", e.toString());
        }

        content.putString(song.getLyricist());
        boolean status = clipboard.setContent(content);
        System.out.println("status = " + status);
        if (status) {
            btnCopy.setText("Copied");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> btnCopy.setText("Copy")));
            timeline.play();
        }
    }

    @FXML
    void copyProductName(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();

        Songs song = new Songs("null", "null");
        try {
            song = DatabasePostgres.searchSongDetails(searchResultISRC.getText());
        } catch (SQLException | ClassNotFoundException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Fetching Song Details", e.toString());
        }

        content.putString(song.getProductName());
        boolean status = clipboard.setContent(content);
        System.out.println("status = " + status);
        if (status) {
            btnCopy.setText("Copied");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> btnCopy.setText("Copy")));
            timeline.play();
        }
    }

    @FXML
    void copySinger(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();

        Songs song = new Songs("null", "null");
        try {
            song = DatabasePostgres.searchSongDetails(searchResultISRC.getText());
        } catch (SQLException | ClassNotFoundException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Fetching Song Details", e.toString());
        }

        content.putString(song.getSinger());
        boolean status = clipboard.setContent(content);
        System.out.println("status = " + status);
        if (status) {
            btnCopy.setText("Copied");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> btnCopy.setText("Copy")));
            timeline.play();
        }
    }

    @FXML
    void copySongName(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();

        Songs song = new Songs("null", "null");
        try {
            song = DatabasePostgres.searchSongDetails(searchResultISRC.getText());
        } catch (SQLException | ClassNotFoundException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Fetching Song Details", e.toString());
        }

        content.putString(song.getTrackTitle());
        boolean status = clipboard.setContent(content);
        System.out.println("status = " + status);
        if (status) {
            btnCopy.setText("Copied");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> btnCopy.setText("Copy")));
            timeline.play();
        }
    }

    @FXML
    void copyUPC(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();

        Songs song = new Songs("null", "null");
        try {
            song = DatabasePostgres.searchSongDetails(searchResultISRC.getText());
        } catch (SQLException | ClassNotFoundException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Fetching Song Details", e.toString());
        }

        content.putString(song.getUPC());
        boolean status = clipboard.setContent(content);
        System.out.println("status = " + status);
        if (status) {
            btnCopy.setText("Copied");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> btnCopy.setText("Copy")));
            timeline.play();
        }
    }

    @FXML
    void onAddToListButtonClickedInSearchSong(MouseEvent event) {
        // Getting scene
        Node node = (Node) event.getSource();
        Scene scene = node.getScene();

        // Getting label from scene
        Label songListButtonSubtitle = (Label) scene.lookup("#lblSongListSub");

        // Adding songs to list
        String isrc = searchResultISRC.getText();
        Songs song = null;
        try {
            song = DatabasePostgres.searchSongDetails(isrc);
        } catch (SQLException | ClassNotFoundException e) {
            AlertBuilder.sendErrorAlert("Error!", "Error Occurred While Fetching Song", e.toString());
        }
        Main.addSongToList(song);

        List<Songs> songList = Main.getSongListNew();
        int songListLength = songList.size();

        if (songListLength > 1) {
            String text = songList.getFirst().getISRC() + " + " + (songListLength - 1) + " other songs added";
            songListButtonSubtitle.setText(text);
            System.out.println(text);
        } else {
            songListButtonSubtitle.setText(songList.getFirst().getISRC());
            System.out.println(songList.getFirst());
        }

        hbox2.setStyle("-fx-border-color: #6eb0e0");
    }

    @FXML
    void onBtnPlayClicked(MouseEvent event) {

    }

    @FXML
    void onSearchedSongClick(MouseEvent event) {

    }

    @FXML
    void onSearchedSongPress2(KeyEvent event) {

    }

}
