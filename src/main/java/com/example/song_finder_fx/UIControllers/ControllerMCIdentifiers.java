package com.example.song_finder_fx.UIControllers;

import com.example.song_finder_fx.Controller.SceneController;
import com.opencsv.CSVWriter;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ControllerMCIdentifiers {

    @FXML
    private VBox vbClaimsList;

    public static List<TextField> upcs = new ArrayList<>();

    public static List<TextField> claimCNumbers = new ArrayList<>();

    public static List<TextField> claimISRCs = new ArrayList<>();

    @FXML
    public void initialize() throws IOException {
        vbClaimsList.getChildren().clear();
        for (int i = 0; i < ControllerMCList.finalManualClaims.size(); i++) {
            Node entry = SceneController.loadLayout("layouts/manual_claims/mci-entry.fxml");

            Label claimID = (Label) entry.lookup("#claimID");
            claimID.setText(String.valueOf(ControllerMCList.finalManualClaims.get(i).getId()));

            Label claimName = (Label) entry.lookup("#claimName");
            claimName.setText(ControllerMCList.labelsSongName.get(i).getText());

            TextField claimUPC = (TextField) entry.lookup("#claimUPC");
            upcs.add(claimUPC);

            TextField claimCNumber = (TextField) entry.lookup("#claimCNumber");
            claimCNumbers.add(claimCNumber);

            TextField claimISRC = (TextField) entry.lookup("#claimISRC");
            claimISRCs.add(claimISRC);

            vbClaimsList.getChildren().add(entry);
        }
    }

    @FXML
    void onBack(MouseEvent event) {

    }

    @FXML
    void onGenerate(MouseEvent event) throws IOException {
        System.out.println("ControllerMCIdentifiers.onGenerate");

        Path tempDir = Files.createTempDirectory("ingest");
        Path csvFile = tempDir.resolve("ingest.csv");
        CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFile.toFile()));
        String[] header = {"//Field name:", "Album title", "Album version", "UPC", "Catalog number",
                "Primary artists", "Featuring artists", "Release date", "Main genre",
                "Main subgenre", "Alternate genre", "Alternate subgenre", "Label",
                "CLine year", "CLine name", "PLine year", "PLine name", "Parental advisory",
                "Recording year", "Recording location", "Album format", "Number of volumes",
                "Territories", "Excluded territories", "Language (Metadata)", "Catalog tier",
                "Track title", "Track version", "ISRC", "Track primary artists",
                "Track featuring artists", "Volume number", "Track main genre",
                "Track main subgenre", "Track alternate genre", "Track alternate subgenre",
                "Track language (Metadata)", "Audio language", "Lyrics", "Available separately",
                "Track parental advisory", "Preview start", "Preview length",
                "Track recording year", "Track recording location", "Contributing artists",
                "Composers", "Lyricists", "Remixers", "Performers", "Producers",
                "Writers", "Publishers", "Track sequence", "Track catalog tier",
                "Original file name", "Original release date", "Movement title",
                "Classical key", "Classical work", "Always send display title",
                "Movement number", "Classical catalog"};

        csvWriter.writeNext(header);

        /*for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                System.out.println("labelsSongName = " + labelsSongName.get(i).getText());
            }
        }*/

        csvWriter.close();

        System.out.println("csvFile = " + csvFile);
    }

    @FXML
    void toRoot(MouseEvent event) {

    }

}
