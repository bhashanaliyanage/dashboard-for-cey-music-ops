package com.example.song_finder_fx;

import com.example.song_finder_fx.Model.Songs;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class ControllerSongSongListInvoice {

    public Label lblISRC;
    @FXML
    private ImageView btnPercentageChange;

    @FXML
    private HBox hboxInvoiceSong;

    @FXML
    private Label songArtist;

    @FXML
    private Label songName;

    @FXML
    private Label songShare;

    @FXML
    private VBox vboxSong;

    /*@FXML
    public void initialize() {
        setSongCount();
    }

    private void setSongCount() {

    }*/

    @FXML
    void onDeleteBtnClicked(ActionEvent event) {

    }

    @FXML
    void onPercentageChangeButtonClicked(MouseEvent event) {
        System.out.println("ControllerSongSongListInvoice.onPercentageChangeButtonClicked");
        String song_share = songShare.getText();
        String song_name = songName.getText();
        String isrc = lblISRC.getText().trim();
        Image plusImg = new Image("com/example/song_finder_fx/images/icon_plus.png");
        Image minusImg = new Image("com/example/song_finder_fx/images/icon_minus.png");
        int adjustedShare = 0;

        if (Objects.equals(song_share, "100%")) {
            for (Songs song : Main.songListNew) {
                if (song.getISRC().equals(isrc)) {
                    song.setPercentage("50%");
                    adjustedShare = 50;
                }
            }
        } else {
            for (Songs song : Main.songListNew) {
                if (song.getISRC().equals(isrc)) {
                    song.setPercentage("100%");
                    adjustedShare = 100;
                }
            }
        }

        // Setting percentage change icon, plus or minus according to the situation
        if (adjustedShare == 100) {
            btnPercentageChange.setImage(minusImg);
            songShare.setText("100%");
        } else {
            btnPercentageChange.setImage(plusImg);
            songShare.setText("50%");
        }
    }

}
