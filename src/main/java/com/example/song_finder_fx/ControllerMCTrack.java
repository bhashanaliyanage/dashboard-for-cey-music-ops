package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.ManualClaims;
import com.example.song_finder_fx.Model.ManualClaimTrack;
import com.example.song_finder_fx.Model.Songs;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class ControllerMCTrack {

    public Spinner spinnerStart;
    public Spinner spinnerEnd;

    @FXML
    private TextField txtTrackTitle;

    @FXML
    private TextField txtComposer;

    @FXML
    private TextField txtLyricist;

    @FXML
    private Button btnAddTrack;

    @FXML
    private TitledPane titledPane;

    @FXML
    public void initialize() throws SQLException {
        // List<String> songTitles = DatabaseMySQL.getAllSongs();
        List<String> songTitles = DatabasePostgre.getAllSongTitles();
        TextFields.bindAutoCompletion(txtTrackTitle, songTitles);

        txtTrackTitle.setOnAction(event -> {
            Songs songs;
            String songName = txtTrackTitle.getText();
            // System.out.println("songName = " + songName);
            try {
                songs = DatabasePostgre.searchContributors(songName);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            txtComposer.setText(songs.getComposer());
            txtLyricist.setText(songs.getLyricist());
        });
    }

    public void getSongContributors(ActionEvent event) throws SQLException, ClassNotFoundException {
        System.out.println("ControllerMCTrack.getSongContributors");

        String songName = txtTrackTitle.getText();
        System.out.println("songName = " + songName);
//        Songs songs = DatabaseMySQL.searchContributors(songName);
        Songs songs =  DatabasePostgre.searchContributors(songName);

        txtComposer.setText(songs.getComposer());
        txtLyricist.setText(songs.getLyricist());
    }

    public void onAddTrack(ActionEvent event) throws IOException {
        Node node = (Node) event.getSource();
        Scene scene = node.getScene();
        TextField txtURL = (TextField) scene.lookup("#txtURL");
        VBox vboxTracks = (VBox) scene.lookup("#vboxTracks");

        Node nodeTrack = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/manual-claims-track.fxml")));
        String trackName = txtTrackTitle.getText();
        String lyricist = txtLyricist.getText();
        String composer = txtComposer.getText();
        // String trimStart = spinnerStart.get();
        // String trimEnd = spinnerEnd.get();

        String url = txtURL.getText();
        System.out.println(url);

        // Front-End validation
        boolean ifAnyNull = checkData();

        if (!ifAnyNull) {
            ManualClaimTrack track = new ManualClaimTrack(trackName, lyricist, composer, url);
            ManualClaims.manualClaims.add(track);
            titledPane.setText(trackName);
            titledPane.setExpanded(false);
            btnAddTrack.setDisable(true);
            vboxTracks.getChildren().add(nodeTrack);
        }
    }

    private boolean checkData() {
        boolean status = false;
        String trackName = txtTrackTitle.getText();
        String lyricist = txtLyricist.getText();
        String composer = txtComposer.getText();

        if (trackName.isEmpty()) {
            status = true;
            txtTrackTitle.setStyle("-fx-border-color: red;");
        } else {
            txtTrackTitle.setStyle("-fx-border-color: '#e9ebee';");
        }

        if (lyricist.isEmpty()) {
            status = true;
            txtLyricist.setStyle("-fx-border-color: red;");
        } else {
            txtLyricist.setStyle("-fx-border-color: '#e9ebee';");
        }

        if (composer.isEmpty()) {
            status = true;
            txtComposer.setStyle("-fx-border-color: red;");
        } else {
            txtComposer.setStyle("-fx-border-color: '#e9ebee';");
        }

        return status;
    }
}
