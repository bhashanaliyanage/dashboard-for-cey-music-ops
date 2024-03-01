package com.example.song_finder_fx;

import com.example.song_finder_fx.Model.Songs;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.TextFields;

import java.sql.SQLException;
import java.util.List;

public class ControllerMCTrack {

    @FXML
    private TextField txtTrackTitle;

    @FXML
    private TextField txtTrimEnd;

    @FXML
    private TextField txtTrimStart;

    @FXML
    private TextField txtComposer;

    @FXML
    private TextField txtLyricist;

    @FXML
    public void initialize() throws SQLException, ClassNotFoundException {
        // List<String> songTitles = DatabaseMySQL.getAllSongs();
        List<String> songTitles = DatabasePostgre.getAllSongTitles();
        TextFields.bindAutoCompletion(txtTrackTitle, songTitles);

        txtTrackTitle.setOnAction(event -> {
            Songs songs;
            String songName = txtTrackTitle.getText();
            // System.out.println("songName = " + songName);
            songs = DatabasePostgre.searchContributors(songName);

            txtComposer.setText(songs.getComposer());
            txtLyricist.setText(songs.getLyricist());
        });
    }

    public void getSongContributors(ActionEvent event) throws SQLException, ClassNotFoundException {
        System.out.println("ControllerMCTrack.getSongContributors");

        String songName = txtTrackTitle.getText();
        System.out.println("songName = " + songName);
        Songs songs = DatabaseMySQL.searchContributors(songName);

        txtComposer.setText(songs.getComposer());
        txtLyricist.setText(songs.getLyricist());
    }
}
