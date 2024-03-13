package com.example.song_finder_fx.UIControllers;

import com.example.song_finder_fx.Controller.SceneController;
import com.example.song_finder_fx.DatabasePostgre;
import com.opencsv.CSVWriter;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    void onGenerate(MouseEvent event) throws IOException, SQLException {
        System.out.println("ControllerMCIdentifiers.onGenerate");

        Path tempDir = Files.createTempDirectory("ingest");
        Path csvFile = tempDir.resolve("ingest.csv");
        CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFile.toFile()));
        String[] header = {"//Field name:", "Album title", "Album version", "UPC", "Catalog number", // Done
                "Primary artists", "Featuring artists", "Release date", "Main genre", // Done
                "Main subgenre", "Alternate genre", "Alternate subgenre", "Label", // Done
                "CLine year", "CLine name", "PLine year", "PLine name", "Parental advisory", // Done
                "Recording year", "Recording location", "Album format", "Number of volumes", // Done
                "Territories", "Excluded territories", "Language (Metadata)", "Catalog tier", // Done
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

        for (int i = 0; i < ControllerMCList.finalManualClaims.size(); i++) {
            String albumTitle = ControllerMCList.finalManualClaims.get(i).getTrackName();
            String upc = upcs.get(i).getText();

            if (upc.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Missing Identifier");
                alert.setHeaderText("Missing UPC");
                alert.setContentText("UPC number missing for claim: " + ControllerMCList.finalManualClaims.get(i).getTrackName());

                alert.showAndWait();
            } else {
                String catNo = getCatNo(i);
                String primaryArtists = getPrimaryArtists();
                String releaseDate = getDate();
                String year = getYear(releaseDate);

                String[] row = {"", albumTitle, "", upc, catNo,
                        primaryArtists, "", releaseDate, "Pop",
                        "", "", "", "CeyMusic Records",
                        year, "CeyMusic Publishing", year, "CeyMusic Records", "N",
                        year, "Sri Lanka", "Single", "1",
                        "World", "", "SI", ""};

                csvWriter.writeNext(row);
            }
        }

        csvWriter.close();

        System.out.println("csvFile = " + csvFile);
    }

    private String getPrimaryArtists() {
        return "";
    }

    private static String getYear(String releaseDate) {
        String[] releaseDateSplit = releaseDate.split("-");
        String year = releaseDateSplit[0];
        return year;
    }

    @NotNull
    private static String getDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return currentDate.format(formatter);
    }

    private static @NotNull String getCatNo(int i) throws SQLException {
        final String[] catNo = {claimCNumbers.get(i).getText()};

        if (catNo[0].isEmpty()) {
            catNo[0] = DatabasePostgre.getCatNo(ControllerMCList.finalManualClaims.get(i).getComposer(), ControllerMCList.finalManualClaims.get(i).getLyricist());
            String[] parts = catNo[0].split("-");
            // TODO: 3/13/2024 Show an alert box and get the catalog number from user if no catalog number is returned from the database
            if (Objects.equals(parts[0], "null")) {
                TextInputDialog inputDialog = new TextInputDialog(catNo[0]);
                inputDialog.setTitle("Catalog Number");
                inputDialog.setHeaderText("Enter a new catalog number:");
                inputDialog.setContentText("Catalog Number:");
                inputDialog.showAndWait().ifPresent(newCatNo -> {
                    System.out.println("New catalog number entered: " + newCatNo);
                    catNo[0] = newCatNo;
                });
            }
        }
        return catNo[0];
    }

    @FXML
    void toRoot(MouseEvent event) {

    }

}
