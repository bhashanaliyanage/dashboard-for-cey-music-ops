package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.SceneController;
import com.example.song_finder_fx.Controller.YoutubeDownload;
import com.example.song_finder_fx.Model.ManualClaimTrack;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
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

    @FXML
    private Label lblClaimCount;

    public static List<TextField> upcs = new ArrayList<>();

    public static List<TextField> claimCNumbers = new ArrayList<>();

    public static List<TextField> claimISRCs = new ArrayList<>();

    private String currentISRC;

    @FXML
    public void initialize() throws IOException {
        vbClaimsList.getChildren().clear();
        upcs.clear();
        claimCNumbers.clear();
        claimISRCs.clear();

        List<ManualClaimTrack> claims = ControllerMCList.finalManualClaims;
        int claimCount = claims.size();

        lblClaimCount.setText("Total: " + claimCount);

        for (int i = 0; i < claimCount; i++) {
            ManualClaimTrack claim = claims.get(i);

            Node entry = SceneController.loadLayout("layouts/manual_claims/mci-entry.fxml");

            vbClaimsList.getChildren().add(entry);

            Label claimID = (Label) entry.lookup("#claimID");
            String claimIDString = String.valueOf(claim.getId());
            System.out.println("claimIDString = " + claimIDString);
            claimID.setText(claimIDString);

            Label claimName = (Label) entry.lookup("#claimName");
            String claimNameText = claim.getTrackName();
            System.out.println("claimNameText = " + claimNameText);
            claimName.setText(claimNameText);

            Label lblComposer = (Label) entry.lookup("#lblComposer");
            String composer = claim.getComposer();
            System.out.println("composer = " + composer);
            lblComposer.setText(composer);

            Label lblLyricist = (Label) entry.lookup("#lblLyricist");
            String lyricist = claim.getLyricist();
            System.out.println("lyricist = " + lyricist);
            lblLyricist.setText(lyricist);

            TextField claimUPC = (TextField) entry.lookup("#claimUPC");
            upcs.add(claimUPC);

            TextField claimCNumber = (TextField) entry.lookup("#claimCNumber");
            claimCNumbers.add(claimCNumber);

            TextField claimISRC = (TextField) entry.lookup("#claimISRC");
            claimISRCs.add(claimISRC);

            ImageView imgClaimPreview = (ImageView) entry.lookup("#imgClaimPreview");
            try {
                imgClaimPreview.setImage(setImage(claim));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }



        /*TextField firstUPC_Field = upcs.getFirst();
        firstUPC_Field.setOnKeyReleased(event -> {
            if (event.isShortcutDown() && event.getCode().equals(KeyCode.V)) {
                String inputText = firstUPC_Field.getText();
                String test = "8721093238195\n" +
                        "8721093238201\n" +
                        "8721093238218";
                String[] values = inputText.split("\n");
                for (String value : values) {
                    System.out.println("value = " + value);
                }
            }
        });*/
    }

    private Image setImage(ManualClaimTrack claim) throws IOException, URISyntaxException {
        if (claim.getPreviewImage() != null) {
            return claim.getPreviewImage();
        } else {
            File uploadArtwork = new File("src/main/resources/com/example/song_finder_fx/images/manual_claims/upload_artwork_90.jpg");
            return SwingFXUtils.toFXImage(ImageIO.read(uploadArtwork), null);
        }
    }

    @FXML
    void onBack(MouseEvent event) {
        Node nodeMCIdentifiers = ControllerManualClaimsMain.nodeMC_List;
        Scene scene = SceneController.getSceneFromEvent(event);
        VBox mainVBox = (VBox) scene.lookup("#mainVBox");
        mainVBox.getChildren().setAll(nodeMCIdentifiers);
        System.out.println("ControllerMCIdentifiers.onBack");
    }

    @FXML
    void onGenerate(MouseEvent event) throws IOException, SQLException {
        currentISRC = "";
        LocalDate date = LocalDate.now();
        String userName = System.getProperty("user.name");
        final String[] ingestFileName = {"ingest.csv"};

        // Getting total claims for the loop
        int totalClaims = ControllerMCList.finalManualClaims.size();

        // Getting ingest file name
        TextInputDialog inputIngestFileName = new TextInputDialog("ingest");
        inputIngestFileName.setTitle("Ingest CSV File Name");
        inputIngestFileName.setHeaderText("Enter a file name for ingest");
        inputIngestFileName.setContentText("File Name: ");
        inputIngestFileName.showAndWait().ifPresent(fileName -> {
            ingestFileName[0] = fileName + ".csv";
            System.out.println("Ingest Filename Entered: " + fileName);
        });

        // Front End Validation
        for (int claimID = 0; claimID < totalClaims; claimID++) {
            final String[] upc = {upcs.get(claimID).getText()};
            String catNo = claimCNumbers.get(claimID).getText();
            // currentISRC = claimISRCs.get(claimID).getText();

            // Validating UPCs
            if (upc[0].isEmpty()) {
                TextInputDialog inputDialog = new TextInputDialog(null);
                inputDialog.setTitle("Invalid UPC");
                inputDialog.setHeaderText("Enter a UPC number for " + ControllerMCList.finalManualClaims.get(claimID).getTrackName());
                inputDialog.setContentText("UPC: ");
                int finalClaimID = claimID;
                inputDialog.showAndWait().ifPresent(newUPC -> {
                    upcs.get(finalClaimID).setText(newUPC);
                    System.out.println("New UPC entered: " + newUPC);
                });
            }

            System.out.println("claimID = " + (claimID + 1));

            // Validating Catalog Numbers
            if (catNo.isEmpty()) {
                System.out.println("Catalog Number is null for Claim: " + claimID + 1);
                // Check catalog numbers from database if no user input available
                String trackTitle = ControllerMCList.finalManualClaims.get(claimID).getTrackName();
                String composer = ControllerMCList.finalManualClaims.get(claimID).getComposer();
                String lyricist = ControllerMCList.finalManualClaims.get(claimID).getLyricist();
                catNo = DatabasePostgres.getCatNo(composer, lyricist);

                // Request catalog number from user if there are no catalog numbers available in the database
                if (catNo == null) {
                    requestCatNo(composer, lyricist, claimID, trackTitle);
                }

                // Request catalog number if it is not recognizable
                assert catNo != null;
                String[] parts = catNo.split("-");
                if (Objects.equals(parts[0], "null")) {
                    requestCatNo(composer, lyricist, claimID, trackTitle);
                }
            }
        }

        /*currentISRC = claimISRCs.getFirst().getText();
        if (currentISRC.isEmpty()) {
            currentISRC = requestNewISRC();
        }*/

        // Switching scenes
        Node node = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/ingests/generate_ingest.fxml")));
        Scene scene = SceneController.getSceneFromEvent(event);
        VBox vBox = SceneController.getMainVBox(scene);
        vBox.getChildren().setAll(node);

        // Getting scene objects
        Label lblIngestID = (Label) scene.lookup("#lblIngestID");
        Label lblProcess = (Label) scene.lookup("#lblProcess");
        Label lblLocation = (Label) scene.lookup("#lblLocation");
        ProgressBar progressBar = (ProgressBar) scene.lookup("#progressBar");

        // Requesting file location from user
        File destination = Main.browseLocationNew(scene.getWindow());

        // Setting file location in the UI
        lblLocation.setText(destination.getAbsolutePath());

        // Create ingest CSV and get writer object
        CsvListWriter csvWriter = getCsvListWriter(destination, ingestFileName[0]);

        // Creating entry in ingests database and getting ingest ID
        int ingestID = DatabasePostgres.addManualClaimIngest(date, userName, destination.getAbsolutePath(), ingestFileName[0]);

        if (ingestID > 0) {
            // Updating UI with ingest ID
            lblIngestID.setText(String.valueOf(ingestID));

            // Executing rest of the tasks as a background task
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    // Looping through claims
                    for (int claimID = 0; claimID < totalClaims; claimID++) {
                        // Getting progress
                        double progress = (double) (claimID + 1) / totalClaims;

                        // Getting ingest details
                        String albumTitle = ControllerMCList.finalManualClaims.get(claimID).getTrackName();
                        final String[] upc = {upcs.get(claimID).getText()};
                        String composer = ControllerMCList.finalManualClaims.get(claimID).getComposer();
                        String lyricist = ControllerMCList.finalManualClaims.get(claimID).getLyricist();
                        String youtubeID = ControllerMCList.finalManualClaims.get(claimID).getYoutubeID();
                        String originalFileName = youtubeID + "-" + albumTitle + ".flac";

                        // Writing CSV row
                        List<String> CSV_Row = getCSV_Row(claimID, albumTitle, upc[0], composer, lyricist, originalFileName);
                        csvWriter.write(CSV_Row);

                        // Creating sub-folders by UPC
                        File folder = createSubFolder(upc[0], destination);

                        // Getting the artwork from database, saving it to created subfolder
                        BufferedImage artwork = ControllerMCList.finalManualClaims.get(claimID).getBufferedImage();
                        if (artwork != null) {
                            Platform.runLater(() -> lblProcess.setText("Getting Artwork for: " + albumTitle));
                            try {
                                String outputPath = folder.getAbsolutePath() + "\\" + upc[0] + ".jpg";
                                Platform.runLater(() -> System.out.println("outputPath = " + outputPath));
                                ImageIO.write(artwork, "jpg", new File(outputPath));
                            } catch (IOException e) {
                                Platform.runLater(() -> progressBar.setStyle("-fx-background-color: red;"));
                                Platform.runLater(e::printStackTrace);
                            }
                        } else {
                            try {
                                String path = "src/main/resources/com/example/song_finder_fx/images/manual_claims/upload_artwork.jpg";
                                BufferedImage tempArtwork = ImageIO.read(new File(path));
                                Platform.runLater(() -> lblProcess.setText("Error Getting Artwork. Copying Temporary Artwork: " + albumTitle));
                                String outputPath = folder.getAbsolutePath() + "\\" + upc[0] + ".jpg";
                                ImageIO.write(tempArtwork, "jpg", new File(outputPath));
                            } catch (IOException e) {
                                System.out.println("Error in Artwork: " + e);
                            }
                        }

                        // Downloading audio to a temporary directory
                        Platform.runLater(() -> lblProcess.setText("Downloading Audio for: " + albumTitle));
                        final String[] fileLocation = new String[1];
                        String fileName = CSV_Row.get(55);
                        downloadAudio(claimID, fileName, fileLocation);

                        // Trimming audio if needed and copying it to the sub-folder created
                        trimAndCopyAudio(claimID, albumTitle, fileLocation, fileName, folder, lblProcess);

                        Platform.runLater(() -> progressBar.setProgress(progress));
                        Platform.runLater(() -> lblProcess.setText("Done"));
                    }

                    // Converting CSV to a byte array to store it in the database
                    String csvContent = csvWriter.toString();
                    byte[] byteArray = csvContent.getBytes(StandardCharsets.UTF_8);

                    try {
                        DatabasePostgres.addIngestCSV(byteArray, ingestID);
                    } catch (SQLException e) {
                        Platform.runLater(e::printStackTrace);
                    }

                    csvWriter.close();
                    return null;
                }
            };

            Thread thread = new Thread(task);
            thread.start();
        } else {
            lblIngestID.setText("Error Getting Ingest ID");
            lblIngestID.setStyle("-fx-text-fill: red");
        }
    }

    private static void requestCatNo(String composer, String lyricist, int claimID, String trackTitle) {
        TextInputDialog inputDialog = new TextInputDialog(null);
        inputDialog.setTitle("Cannot find Catalog Number for " + trackTitle);
        inputDialog.setHeaderText("Enter a new catalog number for " + composer + " or " + lyricist);
        inputDialog.setContentText("Catalog Number:");
        inputDialog.showAndWait().ifPresent(newCatNo -> {
            claimCNumbers.get(claimID).setText(newCatNo);
            System.out.println("New catalog number entered: " + newCatNo);
        });
    }

    private static void trimAndCopyAudio(int claimID, String albumTitle, String[] fileLocation, String fileName, File folder, Label lblProcess) throws IOException, InterruptedException {
        if (ControllerMCList.finalManualClaims.get(claimID).getTrimStart() != null) {
            System.out.println("Trim Start: " + ControllerMCList.finalManualClaims.get(claimID).getTrimStart());
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

            // Files.copy(sourcePath, destinationPath);
            YoutubeDownload.convertAudio(sourcePath, destinationPath);
        }
    }

    private static void downloadAudio(int claimID, String fileName, String[] fileLocation) {
        String url = "";
        String fileLocation1 = "";

        try {
            url = ControllerMCList.finalManualClaims.get(claimID).getYouTubeURL();
            Path tempDir = Files.createTempDirectory("ceymusic_dashboard_audio");
            fileLocation1 = tempDir.toString();
            YoutubeDownload.downloadAudio(url, fileLocation1, fileName);
            fileLocation[0] = fileLocation1;
        } catch (IOException | InterruptedException e) {
            String finalUrl = url;
            String finalFileLocation = fileLocation1;
            Platform.runLater(() -> AlertBuilder.sendErrorAlert("Error", "Error Downloading Audio", String.format("YouTube URL: %s\nTemporary File Path: %s\nException: %s", finalUrl, finalFileLocation, e)));
        }
    }

    @NotNull
    private static File createSubFolder(String upc, File destination) {
        File folder = new File(destination, upc);
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
        return folder;
    }

    @NotNull
    private static CsvListWriter getCsvListWriter(File destination, String fileName) throws IOException {
        // Creating ingest file inside the location given by user
        String filePath = destination.getAbsolutePath() + "/" + fileName;
        new File(filePath);
        CsvListWriter csvWriter = new CsvListWriter(new FileWriter(filePath), CsvPreference.STANDARD_PREFERENCE);

        // Writing header
        List<String> header = getHeader();
        csvWriter.write(header);
        return csvWriter;
    }

    @NotNull
    private List<String> getCSV_Row(int i, String albumTitle, String upc, String composer, String lyricist, String originalFileName) throws SQLException {
        String catNo = getCatNo(i);
        String primaryArtists = getPrimaryArtists(ControllerMCList.finalManualClaims.get(i));
        String releaseDate = getDate();
        String year = getYear(releaseDate);
        String isrc = getISRC(i, currentISRC);
        Platform.runLater(() -> System.out.println("isrc = " + isrc));
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

    private String getISRC(int i, String isrc) {
        final String[] userISRC = {claimISRCs.get(i).getText()};
        if (Objects.equals(isrc, "")) {
            if (userISRC[0].isEmpty()) {
                // userISRC[0] = requestNewISRC();
                userISRC[0] = "";
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

        if (Objects.equals(lyricist, composer)) {
            return composer;
        } else {
            return String.format("%s | %s", composer, lyricist);
        }
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
        // Get catalog numbers from user input
        final String[] catNo = {claimCNumbers.get(i).getText()};

        if (catNo[0].isEmpty()) {
            // Check catalog numbers from database if no user input available
            String composer = ControllerMCList.finalManualClaims.get(i).getComposer();
            String lyricist = ControllerMCList.finalManualClaims.get(i).getLyricist();
            catNo[0] = DatabasePostgres.getCatNo(composer, lyricist);

            // Request catalog number from user if there are no catalog numbers available in the database
            if (catNo[0] == null) {
                requestCatalogNumber(composer, lyricist, catNo);
            }

            // Request catalog number if it is not recognizable
            String[] parts = catNo[0].split("-");
            if (Objects.equals(parts[0], "null")) {
                requestCatalogNumber(composer, lyricist, catNo);
            }
        }
        return catNo[0];
    }

    private static void requestCatalogNumber(String composer, String lyricist, String[] catNo) {
        Platform.runLater(() -> {
            TextInputDialog inputDialog = new TextInputDialog(null);
            inputDialog.setTitle("Cannot find Catalog Number");
            inputDialog.setHeaderText("Enter a new catalog number for " + composer + " or " + lyricist);
            inputDialog.setContentText("Catalog Number:");
            inputDialog.showAndWait().ifPresent(newCatNo -> {
                System.out.println("New catalog number entered: " + newCatNo);
                catNo[0] = newCatNo;
            });
        });
    }

    @FXML
    void toRoot() {

    }

    public void onTest() {
        // Getting total claims for the loop
        int totalClaims = ControllerMCList.finalManualClaims.size();
        System.out.println("Total Claim Count: " + totalClaims);

        // Front End Validation
        for (int claimID = 0; claimID < totalClaims; claimID++) {
            final String[] upc = {upcs.get(claimID).getText()};
            String catNo = claimCNumbers.get(claimID).getText();
            final String[] userISRC = {claimISRCs.get(claimID).getText()};
            System.out.println((claimID + 1) + " | " + upc[0] + " | " + catNo + " | " + userISRC[0]);
        }
    }

    @FXML
    void onBulkPaste(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard.hasString()) {
            String inputText = clipboard.getString();
            String[] values = inputText.split("\n");

            // Assign each value to the corresponding text field
            for (int i = 0; i < Math.min(values.length, upcs.size()); i++) {
                upcs.get(i).setText(values[i]);
            }
        }
    }

    @FXML
    void onBulkPasteCatNos(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();

        if (clipboard.hasString()) {
            String inputText = clipboard.getString();
            String[] values = inputText.split("\n");

            // Assign each value to the corresponding text field
            for (int i = 0; i < Math.min(values.length, claimCNumbers.size()); i++) {
                claimCNumbers.get(i).setText(values[i]);
            }
        }
    }
}
