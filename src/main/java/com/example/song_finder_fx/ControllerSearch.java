package com.example.song_finder_fx;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import javax.sound.sampled.Clip;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.List;
import java.util.Objects;

public class ControllerSearch {
    private final UIController uiController;
    public TextField searchArea;
    public ScrollPane scrlpneSong;
    public VBox vboxSong;
    public VBox vboxSongSearch;
    public Label searchResultSongName;
    public Label searchResultISRC;
    public Label searchResultArtist;
    public Label songName;
    public Label songISRC;
    public Label songSinger;

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

    /*public void getText(KeyEvent keyEvent) {
        // Getting search keywords
        String text = searchArea.getText();

        // Connecting to database
        DatabaseMySQL db = new DatabaseMySQL();

        scrlpneSong.setVisible(true);
        scrlpneSong.setContent(vboxSong);

        Task<java.util.List<Songs>> task = new Task<>() {
            @Override
            protected java.util.List<Songs> call() throws Exception {
                return db.searchSongDetailsBySearchType(text, searchType);
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
    }*/

    public void onBtnPlayClicked(MouseEvent mouseEvent) {
        Image img = new Image("com/example/song_finder_fx/images/icon _timer.png");

        Main.directoryCheck();

        Node node = (Node) mouseEvent.getSource();
        Scene scene = node.getScene();

        /*Label lblISRC = (Label) scene.lookup("#songISRC");
        Label lblPlayerSongName = (Label) scene.lookup("#lblPlayerSongName");
        ImageView imgMediaPico = (ImageView) scene.lookup("#imgMediaPico");
        Label lblSongName = (Label) scene.lookup("#songName");
        Label lblArtist = (Label) scene.lookup("#songSinger");
        Label lblPlayerArtist = (Label) scene.lookup("#lblPlayerSongArtst");
        */

        String isrc = songISRC.getText();

        Task<Void> task;
        Path start = Paths.get(Main.selectedDirectory.toURI());
        final boolean[] status = new boolean[1];

        System.out.println(isrc);

        uiController.lblPlayerSongName.setText("Loading audio");
        uiController.lblPlayerSongName.setStyle("-fx-text-fill: '#000000'");
        uiController.imgMediaPico.setImage(img);

        task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Clip clip = Main.getClip();
                if (clip != null) {
                    clip.stop();
                    status[0] = Main.playAudio(start, isrc);
                } else {
                    status[0] = Main.playAudio(start, isrc);
                }
                return null;
            }
        };

        task.setOnSucceeded(event -> UIController.setPlayerInfo(status, uiController.lblPlayerSongName, songName, uiController.lblPlayerSongArtst, songSinger, uiController.imgMediaPico));

        new Thread(task).start();
    }

    public void onAddToListButtonClicked(ActionEvent actionEvent) {
    }

    public void onOpenFileLocationButtonClicked(MouseEvent mouseEvent) {
    }

    public void onCopyToButtonClicked(MouseEvent mouseEvent) {
    }
}
