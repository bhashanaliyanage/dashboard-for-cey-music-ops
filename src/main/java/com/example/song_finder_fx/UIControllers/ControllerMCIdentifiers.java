package com.example.song_finder_fx.UIControllers;

import com.example.song_finder_fx.Controller.SceneController;
import com.example.song_finder_fx.Controller.YoutubeDownload;
import com.example.song_finder_fx.ControllerSettings;
import com.example.song_finder_fx.DatabasePostgres;
import com.example.song_finder_fx.Main;
import com.example.song_finder_fx.Model.ManualClaimTrack;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
        upcs.clear();
        for (int i = 0; i < ControllerMCList.finalManualClaims.size(); i++) {
            Node entry = SceneController.loadLayout("layouts/manual_claims/mci-entry.fxml");

            vbClaimsList.getChildren().add(entry);

            Label claimID = (Label) entry.lookup("#claimID");
            claimID.setText(String.valueOf(ControllerMCList.finalManualClaims.get(i).getId()));

            Label claimName = (Label) entry.lookup("#claimName");
            claimName.setText(ControllerMCList.labelsSongName.get(i).getText());

            TextField claimUPC = (TextField) entry.lookup("#claimUPC");
            // System.out.println("claimUPC.getText() = " + claimUPC.getText());
            upcs.add(claimUPC);

            TextField claimCNumber = (TextField) entry.lookup("#claimCNumber");
            claimCNumbers.add(claimCNumber);

            TextField claimISRC = (TextField) entry.lookup("#claimISRC");
            claimISRCs.add(claimISRC);

            // currentISRC = "";
        }
    }

    @FXML
    void onBack(MouseEvent event) {

    }

    @FXML
    void onGenerate(MouseEvent event) throws IOException, SQLException {
        Node node = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/ingests/generate_ingest.fxml")));
        Scene scene = SceneController.getSceneFromEvent(event);
        VBox vBox = SceneController.getMainVBox(scene);
        vBox.getChildren().setAll(node);

        Label lblIngestID = (Label) scene.lookup("#lblIngestID");
        Label lblProcess = (Label) scene.lookup("#lblProcess");
        ProgressBar progressBar = (ProgressBar) scene.lookup("#progressBar");

        File destination = Main.browseLocationNew(scene.getWindow());
        String filePath = destination.getAbsolutePath() + "/ingest.csv";
        File file = new File(filePath);
        CsvListWriter csvWriter = new CsvListWriter(new FileWriter(filePath), CsvPreference.STANDARD_PREFERENCE);
        List<String> header = getHeader();
        currentISRC = "";
        LocalDate date = LocalDate.now();
        String userName = System.getProperty("user.name");

        csvWriter.write(header);

        int ingestID = DatabasePostgres.addIngest(date, userName, destination.getAbsolutePath(), "ingest.csv");

        if (ingestID > 0) {
            lblIngestID.setText(String.valueOf(ingestID));

            int totalClaims = ControllerMCList.finalManualClaims.size();

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    for (int claimID = 0; claimID < totalClaims; claimID++) {
                        // Platform.runLater(() -> System.out.println("Test"));
                        double progress = (double) (claimID + 1) / totalClaims;

                        String albumTitle = ControllerMCList.finalManualClaims.get(claimID).getTrackName();
                        String upc = upcs.get(claimID).getText();

                        String composer = ControllerMCList.finalManualClaims.get(claimID).getComposer();
                        String lyricist = ControllerMCList.finalManualClaims.get(claimID).getLyricist();
                        String originalFileName = ControllerMCList.finalManualClaims.get(claimID).getYoutubeID() + ".flac";

                        if (upc.isEmpty()) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Missing Identifier");
                            alert.setHeaderText("Missing UPC");
                            alert.setContentText("UPC number missing for claim: " + ControllerMCList.finalManualClaims.get(claimID).getTrackName());

                            Platform.runLater(alert::showAndWait);
                        } else {
                            List<String> CSV_Row = getCSV_Row(claimID, albumTitle, upc, composer, lyricist, originalFileName);

                            File folder = new File(destination, upc);
                            String fileName = CSV_Row.get(55);

                            csvWriter.write(CSV_Row);

                            if (!folder.exists()) {
                                boolean folderCreated = folder.mkdir();

                                if (folderCreated) {
                                    System.out.println("Folder created successfully: " + folder.getAbsolutePath());
                                } else {
                                    System.err.println("Error creating folder: " + folder.getAbsolutePath());
                                }
                            } else {
                                System.out.println("Folder already exists: " + folder.getAbsolutePath());
                            }

                            final String[] fileLocation = new String[1];

                            Platform.runLater(() -> lblProcess.setText("Downloading Audio for: " + albumTitle));

                            try {
                                String url = ControllerMCList.finalManualClaims.get(claimID).getYouTubeURL();
                                Path tempDir = Files.createTempDirectory("ceymusic_dashboard_audio");
                                String fileLocation1 = tempDir.toString();
                                YoutubeDownload.downloadAudio(url, fileLocation1, fileName);
                                fileLocation[0] = fileLocation1;
                            } catch (IOException e1) {
                                Platform.runLater(() -> {
                                    Alert alert1 = new Alert(Alert.AlertType.ERROR);
                                    alert1.setTitle("Error");
                                    alert1.setHeaderText("An error occurred");
                                    alert1.setContentText(String.valueOf(e1));
                                    Platform.runLater(alert1::showAndWait);
                                });
                            }

                            if (!Objects.equals(ControllerMCList.finalManualClaims.get(claimID).getTrimStart(), "null")) {

                                Platform.runLater(() -> lblProcess.setText("Trimming Audio for: " + albumTitle));

                                String trimStart = ControllerMCList.finalManualClaims.get(claimID).getTrimStart();
                                String trimEnd = ControllerMCList.finalManualClaims.get(claimID).getTrimEnd();

                                String sourceFilePath = fileLocation[0] + "\\" + fileName;
                                String outputPath = folder.getAbsolutePath() + "\\" + fileName;
                                try {
                                    YoutubeDownload.trimAudio(sourceFilePath, outputPath, trimStart, trimEnd);
                                    Platform.runLater(() -> lblProcess.setText("Done"));
                                } catch (IOException | InterruptedException exception) {
                                    Platform.runLater(() -> {
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.setTitle("Error");
                                        alert.setHeaderText("An error occurred");
                                        alert.setContentText(String.valueOf(exception));
                                        Platform.runLater(alert::showAndWait);
                                        Platform.runLater(() -> lblProcess.setText("Error"));
                                    });
                                }
                            } else {
                                Platform.runLater(() -> lblProcess.setText("Copying Audio for: " + albumTitle));
                                String sourceFilePath = fileLocation[0] + "\\" + fileName;
                                Path sourcePath = Paths.get(sourceFilePath);
                                Path destinationPath = Paths.get(folder.getAbsolutePath(), fileName);

                                System.out.println("sourcePath = " + sourcePath);
                                System.out.println("destinationPath = " + destinationPath);

                                Files.copy(sourcePath, destinationPath);
                            }
                        }

                        Platform.runLater(() -> progressBar.setProgress(progress));
                    }

                    csvWriter.close();
                    Platform.runLater(() -> lblProcess.setText("Done"));
                    return null;
                }
            };

            Thread thread = new Thread(task);
            thread.start();
        } else {
            lblIngestID.setText("Error Getting Ingest ID");
            lblIngestID.setStyle("-fx-text-fill: red");
        }

        System.out.println("csvFile = " + file.getAbsolutePath());
    }

    @NotNull
    private List<String> getCSV_Row(int i, String albumTitle, String upc, String composer, String lyricist, String originalFileName) throws SQLException {
        String catNo = getCatNo(i);
        String primaryArtists = getPrimaryArtists(ControllerMCList.finalManualClaims.get(i));
        String releaseDate = getDate();
        String year = getYear(releaseDate);
        String isrc = getISRC(i, currentISRC);
        String writers = String.format("%s | %s", composer, lyricist);

        List<String> row = new ArrayList<>();
        row.add("");
        row.add(albumTitle);
        row.add("");
        row.add(upc);
        row.add(catNo);
        row.add(primaryArtists);
        row.add("");
        row.add(releaseDate);
        row.add("Pop");
        row.add("");
        row.add("");
        row.add("");
        row.add("CeyMusic Records");
        row.add(year);
        row.add("CeyMusic Publishing");
        row.add(year);
        row.add("CeyMusic Records");
        row.add("N");
        row.add(year);
        row.add("Sri Lanka");
        row.add("Single");
        row.add("1");
        row.add("World");
        row.add("");
        row.add("SI");
        row.add("");
        row.add(albumTitle);
        row.add("");
        row.add(isrc);
        row.add(primaryArtists);
        row.add("");
        row.add("1");
        row.add("Pop");
        row.add("");
        row.add("");
        row.add("");
        row.add("SI");
        row.add("SI");
        row.add("");
        row.add("Y");
        row.add("N");
        row.add("0");
        row.add("30");
        row.add(year);
        row.add("Sri Lanka");
        row.add("");
        row.add(composer);
        row.add(lyricist);
        row.add("");
        row.add("");
        row.add("");
        row.add(writers);
        row.add("CeyMusic Publishing | CeyMusic Publishing");
        row.add("1");
        row.add("Mid");
        row.add(originalFileName);
        row.add(releaseDate);

        return row;
    }

    @NotNull
    private static List<String> getHeader() {
        return Arrays.asList(
                "//Field name:", "Album title", "Album version", "UPC", "Catalog number",
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
                "Movement number", "Classical catalog"
        );
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
