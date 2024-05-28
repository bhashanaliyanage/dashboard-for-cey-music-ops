package com.example.song_finder_fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
        String song_share = songShare.getText();
        String song_name = songName.getText();
        Image plusImg = new Image("com/example/song_finder_fx/images/icon_plus.png");
        Image minusImg = new Image("com/example/song_finder_fx/images/icon_minus.png");

        /*if (Objects.equals(song_share, "100%")) {
            // boolean status = Database.changePercentage(song_name, "50%");
            List<Songs> songList = Main.getSongList();

            if (status) {
                songShare.setText("50%");
                btnPercentageChange.setImage(plusImg);
            }
        } else {
            // boolean status = Database.changePercentage(song_name, "100%");
            if (status) {
                songShare.setText("100%");
                btnPercentageChange.setImage(minusImg);
            }
        }*/
    }

}
