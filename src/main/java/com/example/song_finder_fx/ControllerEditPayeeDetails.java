package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.NotificationBuilder;
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
            setupAutoCompletion(txtTrackName, songs);

        } catch (SQLException e) {
            NotificationBuilder.displayTrayInfo("Unable to fetch catalog", e.toString());
        }

    }

    private void setupAutoCompletion(TextField textField, List<String> artists) {
        TextFields.bindAutoCompletion(textField, artists);
    }

    @FXML
    void onSave() {
        String trackName = txtTrackName.getText();
        String composer = txtComposer.getText();
        String lyricist = txtLyricist.getText();
        String payee01 = txtPayee01.getText();
        String payee01Share = txtPayee01Share.getText();
        String payee02 = txtPayee02.getText();
        String payee02Share = txtPayee02Share.getText();
        String payee03 = txtPayee03.getText();
        String payee03Share = txtPayee03Share.getText();

        boolean validations;

        // Reset styles
        resetStyles();

        // Validate payee information
        if (!validatePayeeInfo(payee01, payee01Share, payee02, payee02Share, payee03, payee03Share)) {
            validations = false;
        }
    }

    private void resetStyles() {
        TextField[] fields = {txtTrackName, txtComposer, txtLyricist, txtPayee01, txtPayee01Share,
                txtPayee02, txtPayee02Share, txtPayee03, txtPayee03Share};
        for (TextField field : fields) {
            if (field != null) {
                field.setStyle("");
            }
        }
    }

    private boolean validatePayeeInfo(String payee01, String payee01Share, String payee02, String payee02Share, String payee03, String payee03Share) {
        /*String[] values = {payee01, payee01Share, payee02, payee02Share, payee03, payee03Share};

        for (String string : values) {
            if ()
        }*/

        if (payee01 == null) {
            txtPayee01.setStyle("-fx-border-color: red;");
            return false;
        } else if (payee01.isEmpty()) {
            txtPayee01.setStyle("-fx-border-color: red;");
            return false;
        }

        if (payee01Share == null) {
            txtPayee01Share.setStyle("-fx-border-color: red;");
            return false;
        } else if (payee01Share.isEmpty()) {
            txtPayee01Share.setStyle("-fx-border-color: red;");
            return false;
        }


        try {
            double share1 = Double.parseDouble(payee01Share);
            double share2 = 0;
            double share3 = 0;

            // Check Payee 02
            if (payee02 != null && !payee02.isEmpty()) {

                if (payee02.equals(payee01)) {
                    AlertBuilder.sendErrorAlert("Error", "Error Validating Payee", "Payee 02 should not be the same as payee 01");
                    return false;
                }

                if (payee02Share == null || payee02Share.isEmpty()) {
                    txtPayee02Share.setStyle("-fx-border-color: red;");
                    AlertBuilder.sendErrorAlert("Error", "Error Validating Share", "Payee 02 share must be assigned.");
                    return false;
                }
                share2 = Double.parseDouble(payee02Share);
            }

            // Check Payee 03
            if (payee03 != null && !payee03.isEmpty()) {

                if (payee03.equals(payee02) || payee03.equals(payee01)) {
                    AlertBuilder.sendErrorAlert("Error", "Error Validating Payee", "Payee 03 should not be the same as payee 01 or 02");
                    return false;
                }

                if (payee03Share == null || payee03Share.isEmpty()) {
                    txtPayee03Share.setStyle("-fx-border-color: red;");
                    AlertBuilder.sendErrorAlert("Error", "Error Validating Share", "Payee 03 share must be assigned.");
                    return false;
                }
                share3 = Double.parseDouble(payee03Share);
            }

            // Validate shares based on the number of payees
            if (payee02 == null || payee02.isEmpty()) {
                if (share1 != 100) {
                    txtPayee01Share.setStyle("-fx-border-color: red;");
                    AlertBuilder.sendErrorAlert("Error", "Error Validating Share", "When only Payee 01 is assigned, their share must be 100.");
                    return false;
                }
            } else if (payee03 == null || payee03.isEmpty()) {
                double total = share1 + share2;
                if (Math.abs(total - 100) > 0.01) { // Using epsilon comparison for floating-point
                    txtPayee01Share.setStyle("-fx-border-color: red;");
                    txtPayee02Share.setStyle("-fx-border-color: red;");
                    AlertBuilder.sendErrorAlert("Error", "Error Validating Share", "The total share of all payees must equal 100.");
                    return false;
                }
            } else {
                double total = share1 + share2 + share3;
                if (Math.abs(total - 100) > 0.01) { // Using epsilon comparison for floating-point
                    txtPayee01Share.setStyle("-fx-border-color: red;");
                    txtPayee02Share.setStyle("-fx-border-color: red;");
                    txtPayee03Share.setStyle("-fx-border-color: red;");
                    AlertBuilder.sendErrorAlert("Error", "Error Validating Share", "The total share of all payees must equal 100.");
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Validating Share", "Invalid share value. Please enter valid numbers.");
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
