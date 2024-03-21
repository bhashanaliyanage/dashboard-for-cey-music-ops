package com.example.song_finder_fx.UIControllers;

import com.example.song_finder_fx.Controller.SceneController;
import com.example.song_finder_fx.Controller.YoutubeDownload;
import com.example.song_finder_fx.DatabasePostgres;
import com.example.song_finder_fx.Main;
import com.example.song_finder_fx.Model.ManualClaimTrack;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ControllerMCIdentifiers {

    @FXML
    private VBox vbClaimsList;

    public static List<TextField> upcs = new ArrayList<>();

    public static List<TextField> claimCNumbers = new ArrayList<>();

    public static List<TextField> claimISRCs = new ArrayList<>();

    private String currentISRC;

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

            // currentISRC = "";
        }
    }

    @FXML
    void onBack(MouseEvent event) {

    }

    @FXML
    void onGenerate(MouseEvent event) throws IOException, SQLException, ClassNotFoundException {
        System.out.println("ControllerMCIdentifiers.onGenerate");

        File destination = Main.browseLocationNew(SceneController.getWindowFromMouseEvent(event));
        String filePath = destination.getAbsolutePath() + "/ingest.csv";
        File file = new File(filePath);
        CSVWriter csvWriter = new CSVWriter(new FileWriter(file));
        String[] header = getHeader();
        currentISRC = "";
        LocalDate date = LocalDate.now();
        String userName = System.getProperty("user.name");

        csvWriter.writeNext(header);

        int ingestID = DatabasePostgres.addIngest(date, userName, destination.getAbsolutePath(), "ingest.csv");

        if (ingestID > 0) {
            for (int claim = 0; claim < ControllerMCList.finalManualClaims.size(); claim++) {
                String albumTitle = ControllerMCList.finalManualClaims.get(claim).getTrackName();
                String upc = upcs.get(claim).getText();
                String composer = ControllerMCList.finalManualClaims.get(claim).getComposer();
                String lyricist = ControllerMCList.finalManualClaims.get(claim).getLyricist();
                String originalFileName = ControllerMCList.finalManualClaims.get(claim).getYoutubeID() + ".flac";

                if (upc.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Missing Identifier");
                    alert.setHeaderText("Missing UPC");
                    alert.setContentText("UPC number missing for claim: " + ControllerMCList.finalManualClaims.get(claim).getTrackName());

                    alert.showAndWait();
                } else {
                    String[] CSV_Row = getCSV_Row(claim, albumTitle, upc, composer, lyricist, originalFileName);
                    File folder = new File(destination, upc);
                    String fileName = CSV_Row[55];

                    csvWriter.writeNext(CSV_Row);

                    if (!folder.exists()) {
                        boolean folderCreated = folder.mkdir();

                        if (folderCreated) {
                            System.out.println("Folder created successfully: " + folder.getAbsolutePath());
                            downloadAudio(claim, fileName, folder);
                            DatabasePostgres.addIngestProduct(ingestID, upc, albumTitle, CSV_Row[28], composer, lyricist, originalFileName);
                        } else {
                            System.err.println("Error creating folder: " + folder.getAbsolutePath());
                        }
                    } else {
                        System.out.println("Folder already exists: " + folder.getAbsolutePath());
                        downloadAudio(claim, fileName, folder);
                    }
                }
            }
        }

        csvWriter.close();

        System.out.println("csvFile = " + file.getAbsolutePath());
    }

    private static void downloadAudio(int claim, String fileName, File folder) {
        String url = ControllerMCList.finalManualClaims.get(claim).getYouTubeURL();
        String fileLocation = folder.getAbsolutePath() + "\\";
        YoutubeDownload.downloadAudio(url, fileLocation, fileName);
    }

    @NotNull
    private String[] getCSV_Row(int i, String albumTitle, String upc, String composer, String lyricist, String originalFileName) throws SQLException {
        String catNo = getCatNo(i);
        String primaryArtists = getPrimaryArtists(ControllerMCList.finalManualClaims.get(i));
        String releaseDate = getDate();
        String year = getYear(releaseDate);
        String isrc = getISRC(i, currentISRC);
        String writers = String.format("%s | %s", composer, lyricist);
        // System.out.println("currentISRC = " + currentISRC);

        return new String[]{"", albumTitle, "", upc, catNo,
                primaryArtists, "", releaseDate, "Pop",
                "", "", "", "CeyMusic Records",
                year, "CeyMusic Publishing", year, "CeyMusic Records", "N",
                year, "Sri Lanka", "Single", "1",
                "World", "", "SI", "",
                albumTitle, "", isrc, primaryArtists,
                "", "1", "Pop",
                "", "", "",
                "SI", "SI", "", "Y",
                "N", "0", "30",
                year, "Sri Lanka", "",
                composer, lyricist, "", "", "",
                writers, "CeyMusic Publishing | CeyMusic Publishing", "1", "Mid",
                originalFileName, releaseDate};
    }

    @NotNull
    private static String[] getHeader() {
        String[] header = {"//Field name:", "Album title", "Album version", "UPC", "Catalog number", // Done
                "Primary artists", "Featuring artists", "Release date", "Main genre", // Done
                "Main subgenre", "Alternate genre", "Alternate subgenre", "Label", // Done
                "CLine year", "CLine name", "PLine year", "PLine name", "Parental advisory", // Done
                "Recording year", "Recording location", "Album format", "Number of volumes", // Done
                "Territories", "Excluded territories", "Language (Metadata)", "Catalog tier", // Done
                "Track title", "Track version", "ISRC", "Track primary artists", // Done
                "Track featuring artists", "Volume number", "Track main genre", // Done
                "Track main subgenre", "Track alternate genre", "Track alternate subgenre", // Done
                "Track language (Metadata)", "Audio language", "Lyrics", "Available separately", // Done
                "Track parental advisory", "Preview start", "Preview length", // Done
                "Track recording year", "Track recording location", "Contributing artists", // Done
                "Composers", "Lyricists", "Remixers", "Performers", "Producers", // Done
                "Writers", "Publishers", "Track sequence", "Track catalog tier", // Done
                "Original file name", "Original release date", "Movement title",
                "Classical key", "Classical work", "Always send display title",
                "Movement number", "Classical catalog"};
        return header;
    }

    private String getISRC(int i, String isrc) throws SQLException {
        final String[] userISRC = {claimISRCs.get(i).getText()};
        if (Objects.equals(isrc, "")) {
            if (userISRC[0].isEmpty()) {
                userISRC[0] = DatabasePostgres.getNewUGCISRC();
                System.out.println("isrc[0] = " + userISRC[0]);
            }

            currentISRC = userISRC[0];

            return currentISRC;
        } else {
            if (userISRC[0].isEmpty()) {
                // Extract prefix (first 7 characters)
                String prefix = currentISRC.substring(0, 7);

                // Extract suffix (remaining characters)
                String suffixStr = currentISRC.substring(7);
                int suffix = Integer.parseInt(suffixStr);
                suffix++;

                // Format the suffix as a 5-digit integer
                String formattedSuffix = String.format("%05d", suffix);

                currentISRC = prefix + formattedSuffix;

                return currentISRC;
            } else {
                return userISRC[0];
            }
        }
    }

    private String getPrimaryArtists(ManualClaimTrack manualClaimTrack) {
        String lyricist = manualClaimTrack.getLyricist();
        String composer = manualClaimTrack.getComposer();

        return String.format("%s | %s", composer, lyricist);
    }

    private static String getYear(String releaseDate) {
        String[] releaseDateSplit = releaseDate.split("-");
        return releaseDateSplit[0];
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
            String composer = ControllerMCList.finalManualClaims.get(i).getComposer();
            System.out.println("composer = " + composer);
            String lyricist = ControllerMCList.finalManualClaims.get(i).getLyricist();
            System.out.println("lyricist = " + lyricist);
            catNo[0] = DatabasePostgres.getCatNo(composer, lyricist);
            if (catNo[0] == null) {
                TextInputDialog inputDialog = new TextInputDialog(null);
                inputDialog.setTitle("Cannot find Catalog Number");
                inputDialog.setHeaderText("Enter a new catalog number for " + composer + " or " + lyricist);
                inputDialog.setContentText("Catalog Number:");
                inputDialog.showAndWait().ifPresent(newCatNo -> {
                    System.out.println("New catalog number entered: " + newCatNo);
                    catNo[0] = newCatNo;
                });
            }
            assert catNo[0] != null;
            String[] parts = catNo[0].split("-");
            // TODO: 3/13/2024 Show an alert box and get the catalog number from user if no catalog number is returned from the database
            if (Objects.equals(parts[0], "null")) {
                TextInputDialog inputDialog = new TextInputDialog(catNo[0]);
                inputDialog.setTitle("Cannot find Catalog Number");
                inputDialog.setHeaderText("Enter a new catalog number for " + composer + " or " + lyricist);
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
