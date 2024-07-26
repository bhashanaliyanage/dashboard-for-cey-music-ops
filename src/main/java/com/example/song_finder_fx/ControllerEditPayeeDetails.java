package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.NotificationBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.TextFields;

import java.sql.SQLException;
import java.util.List;

public class ControllerEditPayeeDetails {

    @FXML
    private Label lblTrackName;

    @FXML
    private Label lblTrackNumber;

    @FXML
    private TextField txtComposer;

    @FXML
    private TextField txtLyricist;

    @FXML
    private TextField txtPayee01;

    @FXML
    private TextField txtPayee01Share;

    @FXML
    private TextField txtPayee02;

    @FXML
    private TextField txtPayee02Share;

    @FXML
    private TextField txtPayee03;

    @FXML
    private TextField txtPayee03Share;

    @FXML
    private TextField txtTrackName;

    // Static variables
    public static Label staticLblTrackName;
    public static Label staticLblTrackNumber;
    public static TextField staticTxtComposer;
    public static TextField staticTxtLyricist;
    public static TextField staticTxtPayee01;
    public static TextField staticTxtPayee01Share;
    public static TextField staticTxtPayee02;
    public static TextField staticTxtPayee02Share;
    public static TextField staticTxtPayee03;
    public static TextField staticTxtPayee03Share;
    public static TextField staticTxtTrackName;

    // public static ControllerPayeeUpdaterEntry parent;

    @FXML
    void initialize() {

        // Assign FXML variables to static variables
        staticLblTrackName = lblTrackName;
        staticLblTrackNumber = lblTrackNumber;
        staticTxtComposer = txtComposer;
        staticTxtLyricist = txtLyricist;
        staticTxtPayee01 = txtPayee01;
        staticTxtPayee01Share = txtPayee01Share;
        staticTxtPayee02 = txtPayee02;
        staticTxtPayee02Share = txtPayee02Share;
        staticTxtPayee03 = txtPayee03;
        staticTxtPayee03Share = txtPayee03Share;
        staticTxtTrackName = txtTrackName;

        try {
            List<String> artists = DatabasePostgres.getAllValidatedArtists();
            List<String> songs = DatabasePostgres.getAllSongTitles();
            // Set up auto-completion for the payee text fields
            setupAutoCompletion(txtPayee01, artists);
            setupAutoCompletion(txtPayee02, artists);
            setupAutoCompletion(txtPayee03, artists);
            setupAutoCompletion(txtComposer, artists);
            setupAutoCompletion(txtLyricist, artists);
        } catch (SQLException e) {
            NotificationBuilder.displayTrayInfo("Unable to fetch catalog", e.toString());
        }

    }

    private void setupAutoCompletion(TextField textField, List<String> artists) {
        TextFields.bindAutoCompletion(textField, artists);
    }

    @FXML
    void onSave(ActionEvent event) {

    }

}
