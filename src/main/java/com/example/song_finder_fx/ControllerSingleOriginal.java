package com.example.song_finder_fx;

import com.example.song_finder_fx.Model.Ingest;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.controlsfx.control.textfield.TextFields;

import java.sql.SQLException;
import java.util.ArrayList;

public class ControllerSingleOriginal {

    @FXML
    private CheckBox cbArtist02;

    @FXML
    private CheckBox cbArtist03;

    @FXML
    private CheckBox cbArtist04;

    @FXML
    private ComboBox<String> comboArtist01Type;

    @FXML
    private ComboBox<String> comboArtist02Type;

    @FXML
    private ComboBox<String> comboArtist03Type;

    @FXML
    private ComboBox<String> comboArtist04Type;

    @FXML
    private DatePicker dpRelease;

    @FXML
    private TextField txtArtist01;

    @FXML
    private TextField txtArtist02;

    @FXML
    private TextField txtArtist03;

    @FXML
    private TextField txtArtist04;

    @FXML
    private TextField txtArtwork;

    @FXML
    private TextField txtAudioFile;

    @FXML
    private TextField txtCatNo;

    @FXML
    private TextField txtISRC;

    @FXML
    private TextField txtProductLabel;

    @FXML
    private TextField txtProductTitle;

    @FXML
    private TextField txtUPC;

    private Ingest ingest = new Ingest();

    @FXML
    public void initialize() throws SQLException, ClassNotFoundException {
        cbArtist02.selectedProperty().addListener((observable, oldValue, newValue) -> {
            // Enable or disable the text field depending on the checkbox state
            txtArtist02.setDisable(!newValue);
            comboArtist02Type.setDisable(!newValue);
        });

        cbArtist03.selectedProperty().addListener((observable, oldValue, newValue) -> {
            // Enable or disable the text field depending on the checkbox state
            txtArtist03.setDisable(!newValue);
            comboArtist03Type.setDisable(!newValue);
        });

        cbArtist04.selectedProperty().addListener((observable, oldValue, newValue) -> {
            // Enable or disable the text field depending on the checkbox state
            txtArtist04.setDisable(!newValue);
            comboArtist04Type.setDisable(!newValue);
        });

        ArrayList<String> artistNames = DatabaseMySQL.getArtistList();
//        ArrayList<String> artistNames = DatabasePostgre.getArtistList();      //Postgress


        TextFields.bindAutoCompletion(txtArtist01, artistNames);
        TextFields.bindAutoCompletion(txtArtist02, artistNames);
        TextFields.bindAutoCompletion(txtArtist03, artistNames);
        TextFields.bindAutoCompletion(txtArtist04, artistNames);

        comboArtist01Type.setItems(FXCollections.observableArrayList("Composer","Lyricist","Singer"));
        comboArtist02Type.setItems(FXCollections.observableArrayList("Composer","Lyricist","Singer"));
        comboArtist03Type.setItems(FXCollections.observableArrayList("Composer","Lyricist","Singer"));
        comboArtist04Type.setItems(FXCollections.observableArrayList("Composer","Lyricist","Singer"));
    }

    public void onCreateIngest(ActionEvent event) throws SQLException, ClassNotFoundException {
        boolean ifAnyNull = checkData();
    }

    private boolean checkData() throws SQLException, ClassNotFoundException {
        final boolean[] status = {false};

        //<editor-fold desc="Product Title">
        String productTitle = txtProductTitle.getText();
        if (productTitle.isEmpty()) {
            status[0] = true;
            txtProductTitle.setStyle("-fx-border-color: red;");
        } else {
            ingest.setProductTitle(productTitle);
            txtProductTitle.setStyle("-fx-border-color: '#e9ebee';");
        }
        //</editor-fold>

        //<editor-fold desc="UPC">
        String upc = txtUPC.getText();
        if (upc.isEmpty()) {
            status[0] = true;
            txtUPC.setStyle("-fx-border-color: red;");
        } else {
            if (upc.matches("[0-9]+")) {
                ingest.setProductUPC(upc);
                txtUPC.setStyle("-fx-border-color: '#e9ebee';");
            }
        }
        //</editor-fold>

        //<editor-fold desc="Artist 01 Name">
        String artist01 = txtArtist01.getText();
        if (artist01.isEmpty()) { // Checking the artist TextField if empty
            status[0] = true;
            txtArtist01.setStyle("-fx-border-color: red;");
        } else {
            handleArtistValidation(artist01, status, 1, txtArtist01);
        }
        //</editor-fold>

        //<editor-fold desc="Other Artists Name">
        if (cbArtist02.isSelected()) {
            String artist = txtArtist02.getText();
            if (artist.isEmpty()) {
                status[0] = true;
                txtArtist02.setStyle("-fx-border-color: red;");
            } else {
                handleArtistValidation(artist, status, 2, txtArtist02);
            }
        }

        if (cbArtist03.isSelected()) {
            String artist = txtArtist03.getText();
            if (artist.isEmpty()) {
                status[0] = true;
                txtArtist03.setStyle("-fx-border-color: red;");
            } else {
                handleArtistValidation(artist, status, 3, txtArtist03);
            }
        }

        if (cbArtist04.isSelected()) {
            String artist = txtArtist04.getText();
            if (artist.isEmpty()) {
                status[0] = true;
                txtArtist04.setStyle("-fx-border-color: red;");
            } else {
                handleArtistValidation(artist, status, 4, txtArtist04);
            }
        }
        //</editor-fold>

        //<editor-fold desc="Artist 01 Type">
        String artist01Type = comboArtist01Type.getValue();
        if (artist01Type == null) {
            status[0] = true;
            comboArtist01Type.setStyle("-fx-border-color: red;");
        } else {
            ingest.setArtistType(1, artist01Type);
            comboArtist01Type.setStyle("-fx-border-color: '#e9ebee';");
        }
        //</editor-fold>

        //<editor-fold desc="Other Artists Type">
        if (cbArtist02.isSelected()) {
            String artistType = comboArtist02Type.getValue();
            if (artistType == null) {
                status[0] = true;
                comboArtist02Type.setStyle("-fx-border-color: red;");
            } else {
                ingest.setArtistType(2, artistType);
                comboArtist02Type.setStyle("-fx-border-color: '#e9ebee';");
            }
        }

        if (cbArtist03.isSelected()) {
            String artistType = comboArtist03Type.getValue();
            if (artistType == null) {
                status[0] = true;
                comboArtist03Type.setStyle("-fx-border-color: red;");
            } else {
                ingest.setArtistType(3, artistType);
                comboArtist03Type.setStyle("-fx-border-color: '#e9ebee';");
            }
        }

        if (cbArtist04.isSelected()) {
            String artistType = comboArtist04Type.getValue();
            if (artistType == null) {
                status[0] = true;
                comboArtist04Type.setStyle("-fx-border-color: red;");
            } else {
                ingest.setArtistType(4, artistType);
                comboArtist04Type.setStyle("-fx-border-color: '#e9ebee';");
            }
        }
        //</editor-fold>

        return status[0];
    }

    private void handleArtistValidation(String artist, boolean[] status, int artistNumber, TextField artistTextField) throws SQLException, ClassNotFoundException {
        if (!(artist.matches(".*\\d.*"))) { // Rejecting input if any numerical characters are available
            if (DatabaseMySQL.searchArtistTable(artist)) { // Checking the artist in artist table
                ingest.setArtist(artist, artistNumber);
                artistTextField.setStyle("-fx-border-color: '#e9ebee';");
            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Artist Not Found");
                alert.setHeaderText("The artist " + artist + " is not found in the database.");
                alert.setContentText("Would you like to add this artist to the database?");

                ButtonType buttonTypeYes = new ButtonType("Yes");
                ButtonType buttonTypeNo = new ButtonType("No");

                alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                alert.showAndWait().ifPresent(response -> {
                    if (response == buttonTypeYes) { // Adding artist to the artist table if user clicks yes
                        System.out.println("Add artist to the database");
                        try {
                            DatabaseMySQL.registerNewCeyMusicArtist(artist);
//                            DatabasePostgre.registerNewCeyMusicArtist(artist);        //Postgress

                            ingest.setArtist(artist, artistNumber);
                        } catch (SQLException | ClassNotFoundException e) {
                            Alert error = new Alert(Alert.AlertType.ERROR);
                            error.setTitle("Error");
                            error.setHeaderText(null);
                            error.setContentText("An error occurred while trying to register the artist: " + e.getMessage());

                            error.showAndWait();

                            status[0] = true;
                        }
                    }
                });
            }
        }
    }

    public void onGetCatNo(ActionEvent event) throws SQLException, ClassNotFoundException {
        String mainArtist = txtArtist01.getText();
        if (mainArtist.isEmpty()) { // Checking the artist TextField if empty
            txtArtist01.setStyle("-fx-border-color: red;");
        } else {
            String catNo = DatabaseMySQL.getCatNoFor(mainArtist);
//            String catNo = DatabasePostgre.getCatNoFor(mainArtist);       //Postgerss


            // System.out.println("catNo = " + catNo);
        if (catNo != null) {
            txtCatNo.setText(catNo);
        } else {
            TextInputDialog inputDialog = new TextInputDialog(catNo);
            inputDialog.setTitle("Catalog Number");
            inputDialog.setHeaderText("Enter a new catalog number:");
            inputDialog.setContentText("Catalog Number:");
            inputDialog.showAndWait().ifPresent(newCatNo -> {
                // Handle the new catalog number (e.g., update your data model)
                System.out.println("New catalog number entered: " + newCatNo);
            });
        }
        }
    }
}
