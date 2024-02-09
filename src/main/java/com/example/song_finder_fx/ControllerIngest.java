package com.example.song_finder_fx;

import com.example.song_finder_fx.Model.Ingest;
import com.opencsv.exceptions.CsvValidationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class ControllerIngest {
    public TextField txtProductTitle;
    public TextField txtPrimaryArtist;
    public Label lblUPCCount;
    public TextArea txtAreaUPC;
    public Label lblDestination;
    @FXML
    private ImageView imgFeedback;

    @FXML
    private ImageView imgPayeeUpdate;

    @FXML
    private ImageView imgSongDB_Status;

    @FXML
    private Label lblCountMissingISRCs;

    @FXML
    private Label lblFeedbackProgress;

    @FXML
    private Label lblNIFeedback;

    @FXML
    private Label lblPayeeProgress;

    @FXML
    private Label lblPayeeUpdate;

    @FXML
    private Label lblSongDB_Progress;

    @FXML
    private Label lblSongDB_Update;

    @FXML
    private Label lblUpdateNote;

    @FXML
    private ScrollPane scrlpneMain;

    @FXML
    private VBox vboxUpdateSongDB;
    private Ingest ingest = new Ingest();

    public ControllerIngest() {
    }

    @FXML
    void onImportCSVBtnClick(ActionEvent event) throws IOException, CsvValidationException {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select FUGA Report");
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Image imgCaution = new Image("com/example/song_finder_fx/images/caution.png");
        String[] upcs;
        int upcCount;

        File csv = chooser.showOpenDialog(stage);

        if (csv != null) {
            System.out.println("Report Imported");
            ingest.setCSV(csv);
            lblNIFeedback.setText("Validating CSV");
            boolean validate = ingest.validate();

            if (validate) {
                System.out.println("Validated!");
                lblNIFeedback.setText("CSV Validated");
                lblNIFeedback.setStyle("-fx-text-fill: #000000");
                upcs = ingest.getUPCArray();
                upcCount = (upcs.length / 25) + 1;
                ingest.setUPCCount(upcCount);
                lblUPCCount.setText(String.valueOf(upcCount));
            } else {
                System.out.println("Not Validated!");
                lblNIFeedback.setText("Invalid CSV Structure Please Check Columns");
                imgFeedback.setImage(imgCaution);
                imgFeedback.setVisible(true);
            }
        } else {
            System.out.println("No Report Imported");
        }
    }

    public void generate() throws CsvValidationException, IOException {
        String productTitle = txtProductTitle.getText();
        String primaryArtist = txtPrimaryArtist.getText();
        String upcs = txtAreaUPC.getText();

        System.out.println("productTitle = " + productTitle);
        System.out.println("primaryArtist = " + primaryArtist);
        System.out.println("upcs = " + upcs);

        boolean anyEmpty = false;

        anyEmpty = isAnyEmpty(productTitle, anyEmpty, primaryArtist, upcs);

        if (!anyEmpty) {
            System.out.println("Proceed!");
            ingest.writeCSV();
        }
    }

    private boolean isAnyEmpty(String productTitle, boolean anyEmpty, String primaryArtist, String upcs) {
        //<editor-fold desc="CSV">
        if (!ingest.isCSV()) {
            lblNIFeedback.setStyle("-fx-text-fill: red");
            lblNIFeedback.setText("Please Add CSV");
            anyEmpty = true;
        } else {
            lblNIFeedback.setStyle("-fx-text-fill: #000000");
        }
        //</editor-fold>

        //<editor-fold desc="Product Title">
        if (productTitle.isEmpty()) {
            txtProductTitle.setStyle("-fx-border-color: red;");
            anyEmpty = true;
        } else {
            txtProductTitle.setStyle("-fx-border-color: '#e9ebee';");
            ingest.setProductTitle(productTitle);
        }
        //</editor-fold>

        //<editor-fold desc="Primary Artist">
        if (primaryArtist.isEmpty()) {
            txtPrimaryArtist.setStyle("-fx-border-color: red;");
            anyEmpty = true;
        } else {
            txtPrimaryArtist.setStyle("-fx-border-color: '#e9ebee';");
            ingest.setPrimaryArtist(primaryArtist);
        }
        //</editor-fold>

        //<editor-fold desc="UPCs">
        if (upcs.isEmpty()) {
            txtAreaUPC.setStyle("-fx-border-color: red;");
            anyEmpty = true;
        } else {
            int upcCount = ingest.getUPCCount();
            System.out.println("upcCount = " + upcCount);
            String[] upcArray = upcs.split("\\n");
            System.out.println("upcArray.length = " + upcArray.length);
            if (upcArray.length != upcCount) {
                txtAreaUPC.setStyle("-fx-border-color: red;");
                anyEmpty = true;
            } else {
                txtAreaUPC.setStyle("-fx-border-color: '#e9ebee';");
                ingest.setUPCArray(upcArray);
            }
        }
        //</editor-fold>

        //<editor-fold desc="Destination">
        if (!ingest.isDestination()) {
            anyEmpty = true;
            lblDestination.setText("Please select destination");
            lblDestination.setStyle("-fx-text-fill: red");
        } else {
            lblDestination.setStyle("-fx-text-fill: #000000");
        }
        //</editor-fold>

        return anyEmpty;
    }

    public void onBrowseForDestinationBtnClicked(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Destination");
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File destination = chooser.showDialog(stage);
        lblDestination.setText(destination.getAbsolutePath());
        lblDestination.setStyle("-fx-text-fill: #000000");
        ingest.setDestination(destination);
    }
}
