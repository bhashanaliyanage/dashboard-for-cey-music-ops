package com.example.song_finder_fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class ControllerAddReport {

    @FXML
    private ComboBox<String> comboMonth;

    @FXML
    private ImageView imgImportCaution;

    @FXML
    private Label lblImport;

    @FXML
    private Label lblReportProgress;

    @FXML
    private ScrollPane scrlpneMain;

    @FXML
    private TextField txtYear;

    @FXML
    void initialize() {
        // Create a list of month names
        String[] months = {
                "January", "February", "March", "April",
                "May", "June", "July", "August",
                "September", "October", "November", "December"
        };

        // Add the months to the ComboBox
        comboMonth.getItems().addAll(months);
    }

    @FXML
    void onLoadReport(ActionEvent event) {
        boolean ifAnyNull = checkData();
    }

    private boolean checkData() {
        boolean status = false;

        String month = comboMonth.getSelectionModel().getSelectedItem();
        String year = txtYear.getText();

        /*System.out.println("month = " + month);
        System.out.println("year = " + year);*/

        if (month == null) {
            status = true;
            comboMonth.setStyle("-fx-border-color: red;");
        } else {
            comboMonth.setStyle("-fx-border-color: '#e9ebee';");
        }

        if (year.isEmpty()) {
            // TODO: Validate Further
            status = true;
            txtYear.setStyle("-fx-border-color: red;");
        } else {
            txtYear.setStyle("-fx-border-color: '#e9ebee';");
        }

        return status;
    }

}
