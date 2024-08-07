package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.ErrorDialog;
import com.example.song_finder_fx.Controller.NotificationBuilder;
import com.example.song_finder_fx.Controller.SceneController;
import com.example.song_finder_fx.Model.Songs;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ControllerSongListNew {

    public Button btnExport;
    public Button btnImport;
    @FXML
    private Button btnAddMore;

    @FXML
    private Button btnCopyTo;

    @FXML
    private Button btnGenerateInvoice;

    @FXML
    private VBox hboxListActions;

    @FXML
    private Label lblListCount;

    @FXML
    private VBox mainVBox;

    @FXML
    private ScrollPane scrlpneSong;

    @FXML
    private VBox vBoxInSearchSong;

    @FXML
    private VBox vboxSong;

    @FXML
    void initialize() {
        // Song List
        // List<String> songList = Main.getSongList();
        loadSongList();
    }

    private void loadSongList() {
        List<Songs> songListNew = Main.getSongList();

        updateButtons(songListNew);

        // Setting up UI
        scrlpneSong.setVisible(true);
        scrlpneSong.setContent(vboxSong);

        Node[] nodes;
        nodes = new Node[songListNew.size()];
        vboxSong.getChildren().clear();

        int listSize = Main.getSongList().size();
        lblListCount.setText(String.valueOf(listSize));

        for (int i = 0; i < nodes.length; i++) {
            try {
                System.out.println("i = " + i);
                Songs songDetail = songListNew.get(i);
                nodes[i] = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("layouts/song-songlist.fxml")));
                Label lblSongNumber = (Label) nodes[i].lookup("#songNumber");
                Label lblSongName = (Label) nodes[i].lookup("#srchRsSongName");
                Label lblISRC = (Label) nodes[i].lookup("#srchRsISRC");
                Label lblArtist = (Label) nodes[i].lookup("#srchRsArtist");
                lblSongNumber.setText(String.valueOf(i + 1));
                lblSongName.setText(songDetail.getTrackTitle());
                lblISRC.setText(songDetail.getISRC());
                lblArtist.setText(songDetail.getSinger());
                vboxSong.getChildren().add(nodes[i]);
            } catch (NullPointerException | IOException ex) {
                AlertBuilder.sendErrorAlert("Error", "Error Loading Layout", ex.toString());
            }
        }
    }

    private void updateButtons(List<Songs> songListNew) {
        if (songListNew.isEmpty()) {
            disableButtons();
        } else {
            enableButtons();
        }
    }

    private void refreshSongList() {
        loadSongList();
    }

    private void enableButtons() {
        btnCopyTo.setDisable(false);
        btnExport.setDisable(false);
        btnGenerateInvoice.setDisable(false);
        // btnImport.setDisable(false);
    }

    private void disableButtons() {
        btnCopyTo.setDisable(true);
        btnExport.setDisable(true);
        btnGenerateInvoice.setDisable(true);
        // btnImport.setDisable(true);
    }

    @FXML
    void onAddMoreButtonClicked(ActionEvent actionEvent) {
        Node node = (Node) actionEvent.getSource();
        Scene scene = node.getScene();
        VBox mainVBox = (VBox) scene.lookup("#mainVBox");
        mainVBox.getChildren().clear();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("layouts/search-details.fxml"));
        Parent newContent = null;
        try {
            newContent = loader.load();
        } catch (IOException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Loading View", e.toString());
        }
        mainVBox.getChildren().add(newContent);
    }

    @FXML
    void onCopyToButtonClicked() {
        Task<Void> task;
        // List<String> songList = Main.getSongList();
        List<Songs> songListNew = Main.getSongList();
        if (songListNew.isEmpty()) {
            btnCopyTo.setText("Please add song(s) to list to proceed");
            btnCopyTo.setStyle("-fx-border-color: '#931621'");
        } else {
            File selectedDirectory = Main.getSelectedDirectory();

            if (selectedDirectory.exists()) { // check if the directory exists
                if (selectedDirectory.isDirectory()) { // check if the directory is a directory
                    if (selectedDirectory.canWrite()) { // check if the directory is writable
                        System.out.println("The directory is accessible and writable.");
                        File destination = Main.browseDestination();

                        task = new Task<>() {
                            @Override
                            protected Void call() throws Exception {
                                int songListSize = songListNew.size();

                                for (int i = 0; i < songListSize; i++) {
                                    String isrc = songListNew.get(i).getISRC();

                                    int finalI = i;
                                    Platform.runLater(() -> updateButtonProceed("Copying " + (finalI + 1) + " of " + songListSize));
                                    Main.copyAudio(isrc, selectedDirectory, destination);
                                }
                                return null;
                            }
                        };

                        task.setOnSucceeded(ActionEvent -> {
                            updateButtonProceed("Copy List to Location");

                            if (!DatabaseMySQL.errorBuffer.isEmpty()) {
                                ErrorDialog.showErrorDialogWithLog("File Not Found Error", "Error! Some files are missing in your audio database", DatabaseMySQL.errorBuffer.toString());
                            }

                            NotificationBuilder.displayTrayInfo("Execution Completed", "Please check your destination folder for the copied audio files");
                        });

                        new Thread(task).start();
                    }
                }
            } else {
                System.out.println("The directory does not exist.");
                btnCopyTo.setText("Error! Please set audio database location in settings");
                btnCopyTo.setStyle("-fx-border-color: '#931621'");
            }
        }
    }

    private void updateButtonProceed(String s) {
        btnCopyTo.setText(s);
    }

    @FXML
    void onGenerateInvoiceButtonClicked(ActionEvent event) {
        Scene scene;
        try {
            FXMLLoader loader = new FXMLLoader(ControllerInvoice.class.getResource("layouts/song-list-invoice.fxml"));
            Parent newContent = loader.load();
            Node node = (Node) event.getSource();
            scene = node.getScene();
            VBox mainVBox = (VBox) scene.lookup("#mainVBox");
            mainVBox.getChildren().setAll(newContent);
        } catch (IOException e) {
            AlertBuilder.sendErrorAlert("Error!", "Error Loading Invoice", e.toString());
        }
    }

    public void onExportSongList(ActionEvent actionEvent) {
        System.out.println("\nControllerSongListNew.onExportSongList");
        List<Songs> songList = Main.getSongListNew();

        File pathTo = showSaveDialog(actionEvent);
        if (pathTo == null) {
            System.out.println("Destination not selected. Aborting task.");
            return;
        }

        String path = pathTo.getAbsolutePath();
        if (!path.toLowerCase().endsWith(".csv")) {
            path += ".csv";
        }
        System.out.println("File Destination: " + path);

        try (CSVWriter writer = new CSVWriter(new FileWriter(path))) {
            List<String[]> rows = new ArrayList<>();
            String[] header = {"Product Title", "UPC", "Track Title", "ISRC", "Singer", "Lyrics", "Composer", "File Name"};
            rows.add(header);

            for (Songs song : songList) {
                rows.add(getRow(song));
            }

            writer.writeAll(rows);

            File file = new File(path);
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
        } catch (IOException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Creating CSV File", e.toString());
        }
    }

    @NotNull
    private static String[] getRow(Songs song) {
        String productTitle = song.getProductName();
        if ((productTitle == null) || (productTitle.isEmpty()) || (productTitle.equals("null"))) {
            productTitle = "#N/A";
        }
        String upc = song.getUPC();
        if ((upc == null) || (upc.isEmpty()) || (upc.equals("null"))) {
            upc = "#N/A";
        }
        String trackTitle = song.getTrackTitle();
        if ((trackTitle == null) || (trackTitle.isEmpty()) || (trackTitle.equals("null"))) {
            trackTitle = "#N/A";
        }
        String isrc = song.getISRC();
        if ((isrc == null) || (isrc.isEmpty()) || (isrc.equals("null"))) {
            isrc = "#N/A";
        }
        String singer = song.getSinger();
        if ((singer == null) || (singer.isEmpty()) || (singer.equals("null"))) {
            singer = "#N/A";
        }
        String lyrics = song.getLyricist();
        if ((lyrics == null) || (lyrics.isEmpty()) || (lyrics.equals("null"))) {
            lyrics = "#N/A";
        }
        String composer = song.getComposer();
        if ((composer == null) || (composer.isEmpty()) || (composer.equals("null"))) {
            composer = "#N/A";
        }
        String fileName = song.getFileName();
        if ((fileName == null) || (fileName.isEmpty()) || (fileName.equals("null"))) {
            fileName = "#N/A";
        }

        return new String[]{productTitle, upc, trackTitle, isrc, singer, lyrics, composer, fileName};
    }

    public void onImport(ActionEvent actionEvent) {
        System.out.println("ControllerSongListNew.onImport");

        int importType = getImportType(); // 1 = Append; 2 = Replace; 0 = Default

        Scene scene = SceneController.getSceneFromEvent(actionEvent);
        Label songListButtonSubtitle = (Label) scene.lookup("#lblSongListSub");

        File pathTo = showOpenDialog(actionEvent);
        List<Songs> songListNew = new ArrayList<>();

        if (importType == 1) {
            if (pathTo != null) {
                String path = pathTo.getAbsolutePath();
                System.out.println("File Destination: " + path);

                try {
                    readCSV(path, songListNew, songListButtonSubtitle, lblListCount, importType);
                    refreshSongList();
                } catch (IOException e) {
                    AlertBuilder.sendErrorAlert("Error", "Cannot Open CSV", e.toString());
                }
            }
        }
        if (importType == 2) {
            if (pathTo != null) {
                String path = pathTo.getAbsolutePath();
                System.out.println("File Destination: " + path);

                try {
                    readCSV(path, songListNew, songListButtonSubtitle, lblListCount, importType);
                    refreshSongList();
                } catch (IOException e) {
                    AlertBuilder.sendErrorAlert("Error", "Cannot Open CSV", e.toString());
                }
            }
        }
    }

    private static void readCSV(String path, List<Songs> songListNew, Label songListButtonSubtitle, Label lblListCount, int importType) throws IOException {
        CSVReader reader = new CSVReader(new FileReader(path));
        String[] nextLine;
        try {
            int count = 0;
            reader.readNext(); // Skipping Header
            while ((nextLine = reader.readNext()) != null) {
                Songs song = getSong(nextLine);
                songListNew.add(song);
                count++;
            }

            if (importType == 1) {
                Main.songListNew.addAll(songListNew);
            }
            if (importType == 2) {
                Main.songListNew.clear();
                Main.songListNew.addAll(songListNew);
            }

            updateUserInterface(songListNew, songListButtonSubtitle, lblListCount, count);
        } catch (CsvValidationException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Validating CSV", e.toString());
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    private static void updateUserInterface(List<Songs> songListNew, Label songListButtonSubtitle, Label lblListCount, int count) throws AWTException {
        NotificationBuilder.displayTrayInfo("New Song List Appended", count + " song(s) added to the song list");

        int newSongListSize = Main.songListNew.size();
        lblListCount.setText(String.valueOf(newSongListSize));

        try {
            if (newSongListSize > 1) {
                String isrcTemp = songListNew.getFirst().getISRC();
                String text = isrcTemp + " + " + (newSongListSize - 1) + " other songs added";

                songListButtonSubtitle.setText(text);
            } else {
                songListButtonSubtitle.setText(songListNew.getFirst().getISRC());
            }
        } catch (Exception e) {
            songListButtonSubtitle.setText("Click Here to Add Songs");
        }
    }

    private static @NotNull Songs getSong(String[] nextLine) {
        String productTitle = nextLine[0];
        String upc = nextLine[1];
        String trackTitle = nextLine[2];
        String isrc = nextLine[3];
        String singer = nextLine[4];
        String lyrics = nextLine[5];
        String composer = nextLine[6];
        String fileName = nextLine[7];

        Songs song = new Songs(trackTitle, isrc, singer, composer, lyrics);
        song.setProductTitle(productTitle);
        song.setUPC(upc);
        song.setFileName(fileName);
        return song;
    }

    private File showOpenDialog(ActionEvent actionEvent) {
        // Getting User Location
        Node node = (Node) actionEvent.getSource();
        Scene scene = node.getScene();
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv"));
        chooser.setTitle("Save As");
        return chooser.showOpenDialog(scene.getWindow());
    }

    static File showSaveDialog(ActionEvent actionEvent) {
        // Getting User Location
        Node node = (Node) actionEvent.getSource();
        Scene scene = node.getScene();
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv"));
        chooser.setTitle("Save As");
        return chooser.showSaveDialog(scene.getWindow());
    }

    private static int getImportType() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        // Adding Icon to Alert Dialog
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        String icon = "com/example/song_finder_fx/icons/icon (Custom).png";
        stage.getIcons().add(new Image(icon));

        alert.setTitle("Import Options");
        alert.setHeaderText("Choose an option:");
        alert.setContentText("Do you want to append or replace?");

        ButtonType appendButton = new ButtonType("Append");
        ButtonType replaceButton = new ButtonType("Replace");

        alert.getButtonTypes().setAll(appendButton, replaceButton);

        final int[] importType = {0};

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == appendButton) {
                // Handle append action
                System.out.println("Append selected");
                importType[0] = 1;
            } else if (buttonType == replaceButton) {
                // Handle replace action
                System.out.println("Replace selected");
                importType[0] = 2;
            }
        });

        return importType[0];
    }
}
