package com.example.song_finder_fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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
    }

    @FXML
    void onSave(ActionEvent event) {

    }

}
