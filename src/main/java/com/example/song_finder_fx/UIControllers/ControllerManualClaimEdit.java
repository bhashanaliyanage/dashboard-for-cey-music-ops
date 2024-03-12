package com.example.song_finder_fx.UIControllers;

import com.example.song_finder_fx.DatabasePostgre;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.Objects;

public class ControllerManualClaimEdit {

    @FXML
    private Label lblClaimID;

    @FXML
    private TextField txtComposer;

    @FXML
    private TextField txtLyricist;

    @FXML
    private TextField txtSongName;

    @FXML
    void onSave() throws SQLException {
        String songID = lblClaimID.getText();
        String trackName = txtSongName.getText();
        String composer = txtComposer.getText();
        String lyricist = txtLyricist.getText();

        for (int i = 0; i < ControllerMCList.labelsSongNo.size(); i++) {
            if (Objects.equals(ControllerMCList.labelsSongNo.get(i).getText(), songID)) {
                DatabasePostgre.editManualClaim(songID, trackName, composer, lyricist);

                ControllerMCList.labelsSongName.get(i).setText(trackName);
                ControllerMCList.labelsComposer.get(i).setText(composer);
                ControllerMCList.labelsLyricist.get(i).setText(lyricist);
            }
        }
    }

}
