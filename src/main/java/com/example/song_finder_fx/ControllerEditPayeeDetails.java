package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.NotificationBuilder;
import com.example.song_finder_fx.Model.IngestCSVData;
import com.example.song_finder_fx.Model.Payee;
import com.example.song_finder_fx.Model.PayeeUpdaterUI;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.TextFields;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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
    private List<String> payees;

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

        Task<Void> loadValues = new Task<>() {
            @Override
            protected Void call() {
                try {
                    List<String> artists = DatabasePostgres.getAllValidatedArtists();
                    List<String> songs = DatabasePostgres.getAllSongTitles();
                    payees = DatabasePostgres.getAllPayees();

                    // Set up auto-completion for the payee text fields
                    Platform.runLater(() -> {
                        // System.out.println("Here");
                        setupAutoCompletion(txtPayee01, artists);
                        setupAutoCompletion(txtPayee02, artists);
                        setupAutoCompletion(txtPayee03, artists);
                        setupAutoCompletion(txtComposer, artists);
                        setupAutoCompletion(txtLyricist, artists);
                        setupAutoCompletion(txtTrackName, songs);
                    });
                } catch (SQLException e) {
                    Platform.runLater(() -> {
                        NotificationBuilder.displayTrayInfo("Unable to fetch catalog", e.toString());
                    });
                }
                return null;
            }
        };

        Thread thread = new Thread(loadValues);
        thread.start();
    }

    private void setupAutoCompletion(TextField textField, List<String> artists) {
        TextFields.bindAutoCompletion(textField, artists);
    }

    @FXML
    void onSave() {
        resetStyles();

        String trackNumber = lblTrackNumber.getText();
        int trackNumberInt = Integer.parseInt(trackNumber);

        String trackName = getTextSafely(txtTrackName);
        String composer = getTextSafely(txtComposer);
        String lyricist = getTextSafely(txtLyricist);
        String payee01 = getTextSafely(txtPayee01);
        String payee01Share = getTextSafely(txtPayee01Share);
        String payee02 = getTextSafely(txtPayee02);
        String payee02Share = getTextSafely(txtPayee02Share);
        String payee03 = getTextSafely(txtPayee03);
        String payee03Share = getTextSafely(txtPayee03Share);

        Payee payee = validateAndAutoAssignPayeeInfo(trackName, composer, lyricist, payee01, payee01Share, payee02, payee02Share, payee03, payee03Share);

        if (payee != null) {
            // Update the current UI
            txtPayee01.setText(payee.getPayee1());
            txtPayee01Share.setText(payee.getShare1());
            txtPayee02.setText(payee.getPayee2());
            txtPayee02Share.setText(payee.getShare2());
            txtPayee03.setText(payee.getPayee3());
            txtPayee03Share.setText(payee.getShare3());

            PayeeUpdaterUI ui = ControllerPayeeUpdater.payeeUpdaterUIS.get(trackNumberInt - 1);
            IngestCSVData data = ui.getData();

            data.setPayee(payee);
            ui.setData(data);

            ControllerPayeeUpdater.payeeUpdaterUIS.set(trackNumberInt - 1, ui);
            ui.getLblPayee01().setText(payee.getPayee1() + " (" + payee.getShare1() + "%)");
            ui.getLblPayee01().setStyle("-fx-text-fill: '#FC9F5B'");
            if (payee.getPayee2() != null) {
                ui.getLblPayee02().setText(payee.getPayee2() + " (" + payee.getShare2() + "%)");
                ui.getLblPayee02().setStyle("-fx-text-fill: '#FC9F5B'");
            }

            NotificationBuilder.displayTrayInfo("Saved", "Payee information has been successfully updated.");

            UIController.blankSidePanel();
        }
    }

    private Payee validateAndAutoAssignPayeeInfo(String trackName, String composer, String lyricist, String payee01, String payee01Share, String payee02, String payee02Share, String payee03, String payee03Share) {
        Payee payee = new Payee();
        payee.setPayee1(payee01);
        payee.setShare1(payee01Share);
        payee.setPayee2(payee02);
        payee.setShare2(payee02Share);
        payee.setPayee3(payee03);
        payee.setShare3(payee03Share);

        // Case 1: No payee information filled
        if (isEmptyOrNull(payee01) && isEmptyOrNull(payee02) && isEmptyOrNull(payee03)) {
            if (confirmAutoAssign("No payee information entered. Would you like to auto-assign from composer and lyricist?")) {
                return autoAssignFromComposerLyricist(composer, lyricist);
            }
            return null;
        }

        // Case 2: Only first payee filled without share
        if (!isEmptyOrNull(payee01) && isEmptyOrNull(payee01Share) && isEmptyOrNull(payee02) && isEmptyOrNull(payee03)) {
            if (confirmAutoAssign("Only first payee entered. Would you like to auto-assign 100% share to " + payee01 + "?")) {
                payee.setShare1("100");
                return payee;
            }
            return null;
        }

        // Case 3 & 4: Auto-assign remaining share
        double totalShare = parseShareSafely(payee01Share) + parseShareSafely(payee02Share) + parseShareSafely(payee03Share);

        if (totalShare < 100) {
            if (!isEmptyOrNull(payee02) && isEmptyOrNull(payee02Share)) {
                double remainingShare = 100 - parseShareSafely(payee01Share) - parseShareSafely(payee03Share);
                if (confirmAutoAssign("Would you like to auto-assign the remaining " + remainingShare + "% share to " + payee02 + "?")) {
                    payee.setShare2(String.format("%.0f", remainingShare));
                    return payee;
                }
            } else if (!isEmptyOrNull(payee03) && isEmptyOrNull(payee03Share)) {
                double remainingShare = 100 - parseShareSafely(payee01Share) - parseShareSafely(payee02Share);
                if (confirmAutoAssign("Would you like to auto-assign the remaining " + remainingShare + "% share to " + payee03 + "?")) {
                    payee.setShare3(String.format("%.0f", remainingShare));
                    return payee;
                }
            }
        }

        // Perform the original validation
        if (validatePayeeInfo(
                payee.getPayee1(),
                payee.getShare1(),
                payee.getPayee2(),
                payee.getShare2(),
                payee.getPayee3(),
                payee.getShare3())
        ) {
            return payee;
        }

        return null;
    }

    private double parseShareSafely(String share) {
        try {
            return Double.parseDouble(share);
        } catch (NumberFormatException | NullPointerException e) {
            return 0;
        }
    }

    private Payee autoAssignFromComposerLyricist(String composer, String lyricist) {
        // 01
        /*if (!isEmptyOrNull(composer)) {
            txtPayee01.setText(composer);
            txtPayee01Share.setText("100");
        }
        if (!isEmptyOrNull(lyricist) && !lyricist.equals(composer)) {
            if (!isEmptyOrNull(composer)) {
                txtPayee02.setText(lyricist);
                txtPayee01Share.setText("50");
                txtPayee02Share.setText("50");
            } else {
                txtPayee01.setText(lyricist);
                txtPayee01Share.setText("100");
            }
        }*/

        // 02
        Payee payee = new Payee();
        boolean isComposerRegistered = payees.contains(composer);
        boolean isLyricistRegistered = payees.contains(lyricist);
        boolean isSameArtist = composer.equals(lyricist);

        if (isSameArtist && isComposerRegistered) {
            // Composer and lyricist are the same registered artist
            payee.setPayee1(composer);
            payee.setShare1("100");
        } else if (isComposerRegistered && isLyricistRegistered) {
            // Both composer and lyricist are registered
            payee.setPayee1(composer);
            payee.setPayee2(lyricist);
            payee.setShare1("50");
            payee.setShare2("50");
        } else if (isComposerRegistered) {
            // Only composer is registered
            payee.setPayee1(composer);
            payee.setShare1("100");
        } else if (isLyricistRegistered) {
            // Only lyricist is registered
            payee.setPayee1(lyricist);
            payee.setShare1("100");
        } else {
            // Neither composer nor lyricist is registered
            AlertBuilder.sendInfoAlert("Warning", "Unregistered Artists",
                    "Neither the composer nor the lyricist is registered in the artist list.");
            return null;
        }

        return payee;

    }

    private boolean confirmAutoAssign(String message) {
        return AlertBuilder.getSendConfirmationAlert("Auto-assign Payee Information", null, message);
    }

    private boolean isEmptyOrNull(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String getTextSafely(TextField textField) {
        return textField.getText() != null ? textField.getText().trim() : "";
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
