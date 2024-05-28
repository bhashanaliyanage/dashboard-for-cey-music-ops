package com.example.song_finder_fx;

import com.example.song_finder_fx.Model.Songs;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import javax.sound.sampled.Clip;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class SongSongListController {

    @FXML
    private ImageView imgDeleteSong;

    @FXML
    private ImageView imgPlaySong;

    @FXML
    private Label srchRsArtist;

    @FXML
    private Label srchRsISRC;

    @FXML
    private Label srchRsSongName;

    @FXML
    private VBox vboxSong;

    @FXML
    void onDeleteSongClicked(MouseEvent event) {
        String isrc = srchRsISRC.getText();

        System.out.println("\nSongSongListController.onDeleteSongClicked");
        System.out.println("Selected Song: " + isrc);

        boolean status = Main.deleteSongFromList(isrc);

        if (status) {
            srchRsISRC.setText("-");
            srchRsISRC.setDisable(true);
            srchRsArtist.setText("-");
            srchRsArtist.setDisable(true);
            srchRsSongName.setText(isrc + " Removed from the list");
            srchRsSongName.setDisable(true);
            imgDeleteSong.setVisible(false);
            imgPlaySong.setVisible(false);

            Node node = (Node) event.getSource();
            Scene scene = node.getScene();

            // List<String> songList = Main.getSongList();
            List<Songs> songListNew = Main.getSongList();
            int listSize = songListNew.size();
            Label lblListCount = (Label) scene.lookup("#lblListCount");
            Label songListButtonSubtitle = (Label) scene.lookup("#lblSongListSub");
            lblListCount.setText(String.valueOf(listSize));

            try {
                if (listSize > 1) {
                    String isrcTemp = songListNew.getFirst().getISRC();
                    String text = isrcTemp + " + " + (listSize - 1) + " other songs added";

                    songListButtonSubtitle.setText(text);
                    // System.out.println("Test" + text);
                } else {
                    songListButtonSubtitle.setText(songListNew.getFirst().getISRC());
//                    System.out.println(songList.getFirst());
                }
            } catch (Exception e) {
                songListButtonSubtitle.setText("Click Here to Add Songs");
            }
        }
    }

    @FXML
    void onPlaySongClicked(MouseEvent mouseEvent) {
        Image img = new Image("com/example/song_finder_fx/images/icon _timer.png");

        Main.directoryCheck();

        Node node = (Node) mouseEvent.getSource();
        Scene scene = node.getScene();

        Label lblPlayerSongName = (Label) scene.lookup("#lblPlayerSongName");
        Label lblPlayerArtist = (Label) scene.lookup("#lblPlayerSongArtst");
        ImageView imgMediaPico = (ImageView) scene.lookup("#imgMediaPico");

        String isrc = srchRsISRC.getText();

        Platform.runLater(() -> {
            System.out.println("Song Name: " + srchRsSongName.getText());
            System.out.println("ISRC: " + isrc);
            System.out.println("Artist: " + srchRsArtist.getText());
        });

        Task<Void> task;
        Path start = Paths.get(Main.selectedDirectory.toURI());
        final boolean[] status = new boolean[1];

        lblPlayerSongName.setText("Loading audio");
        lblPlayerSongName.setStyle("-fx-text-fill: '#000000'");
        imgMediaPico.setImage(img);

        task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Clip clip = Main.getClip();
                if (clip != null) {
                    clip.stop();
                }
                status[0] = Main.playAudio(start, isrc);
                return null;
            }
        };

        Songs song = new Songs();

        task.setOnSucceeded(event -> Platform.runLater(() -> UIController.setPlayerInfo(status, lblPlayerSongName, lblPlayerArtist, imgMediaPico, song)));

        new Thread(task).start();
    }

}
