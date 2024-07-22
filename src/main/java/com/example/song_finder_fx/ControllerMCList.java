package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.SceneController;
import com.example.song_finder_fx.Controller.TextFormatter;
import com.example.song_finder_fx.Model.ManualClaimTrack;
import com.opencsv.CSVWriter;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

import static com.example.song_finder_fx.ControllerSongListNew.showSaveDialog;

public class ControllerMCList {

    @FXML
    private Label lblClaimCount;

    @FXML
    private VBox vbClaimsList;

    public static List<CheckBox> checkBoxes = new ArrayList<>();

    public static List<HBox> hBoxes = new ArrayList<>();

    public static List<Label> labelsSongNo = new ArrayList<>();

    public static List<Label> labelsSongName = new ArrayList<>();

    public static List<ImageView> ivArtworks = new ArrayList<>();

    public static List<Label> labelsComposer = new ArrayList<>();

    public static List<Label> labelsLyricist = new ArrayList<>();

    public static List<ManualClaimTrack> manualClaims = new ArrayList<>();

    public static List<ManualClaimTrack> finalManualClaims = new ArrayList<>();

    public static Map<Integer, ManualClaimTrack> claimMap = new HashMap<>();

    @FXML
    public void initialize() throws SQLException, IOException {
        checkBoxes.clear();
        hBoxes.clear();
        labelsSongNo.clear();
        labelsSongName.clear();
        labelsComposer.clear();
        labelsLyricist.clear();
        ivArtworks.clear();

        lblClaimCount.setText("Loading...");

        Task<Void> taskGetManualClaims = new Task<>() {
            @Override
            protected Void call() throws SQLException {
                manualClaims = DatabasePostgres.getManualClaims();

                int count = 0;
                for (ManualClaimTrack claim : manualClaims) {
                    count++;
                    claimMap.put(claim.getId(), claim);

                    Node node;
                    try {
                        node = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("layouts/manual_claims/manual-claims-list-entry.fxml")));
                        Label lblSongNo = (Label) node.lookup("#lblSongNo");
                        labelsSongNo.add(lblSongNo);
                        Label lblSongName = (Label) node.lookup("#lblSongName");
                        labelsSongName.add(lblSongName);
                        Label lblComposer = (Label) node.lookup("#lblComposer");
                        labelsComposer.add(lblComposer);
                        Label lblLyricist = (Label) node.lookup("#lblLyricist");
                        labelsLyricist.add(lblLyricist);
                        Label lblDate = (Label) node.lookup("#lblDate");
                        Label lblClaimType = (Label) node.lookup("#lblClaimType");
                        CheckBox checkBox = (CheckBox) node.lookup("#checkBox");
                        checkBoxes.add(checkBox);
                        HBox hboxEntry = (HBox) node.lookup("#hboxEntry");
                        hBoxes.add(hboxEntry);
                        ImageView image = (ImageView) node.lookup("#image");
                        ivArtworks.add(image);
                        try {
                            image.setImage(claim.getPreviewImage());
                        } catch (Exception e) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("Error Loading Preview Image");
                            alert.setContentText(String.valueOf(e));
                            Platform.runLater(alert::showAndWait);
                        }

                        lblSongNo.setText(String.valueOf(claim.getId()));
                        lblSongName.setText(claim.getTrackName());
                        lblComposer.setText(claim.getComposer());
                        lblLyricist.setText(claim.getLyricist());
                        lblDate.setText(TextFormatter.getDaysAgo(claim.getDate()));
                        lblDate.setStyle(setColor(claim.getDate()));
                        lblClaimType.setText(claim.getClaimTypeString());

                        int finalCount = count;
                        Platform.runLater(() -> {
                            vbClaimsList.getChildren().add(node);
                            lblClaimCount.setText(String.valueOf(finalCount));
                        });
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }

                Platform.runLater(() -> {
                    try {
                        lblClaimCount.setText(DatabasePostgres.getManualClaimCount());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
                return null;
            }
        };

        taskGetManualClaims.setOnSucceeded(event -> {
            Thread thread = new Thread(() -> {
                try {
                    for (Label label : labelsComposer) {
                        String composer = label.getText();
                        boolean status = DatabasePostgres.checkIfArtistValidated(composer);
                        if (!status) {
                            Platform.runLater(() -> label.setStyle("-fx-text-fill: red"));
                        }
                    }

                    for (Label label : labelsLyricist) {
                        String composer = label.getText();
                        boolean status = DatabasePostgres.checkIfArtistValidated(composer);
                        if (!status) {
                            Platform.runLater(() -> label.setStyle("-fx-text-fill: red"));
                        }
                    }
                } catch (SQLException e) {
                    Platform.runLater(e::printStackTrace);
                }
            });
            thread.start();
        });

        Thread threadGetManualClaims = new Thread(taskGetManualClaims);
        threadGetManualClaims.start();

    }

    private String setColor(LocalDate localDate) {
        // Get the current system date
        LocalDate currentDate = LocalDate.now();

        // Calculate the difference in days
        long daysDifference = ChronoUnit.DAYS.between(localDate, currentDate);

        // Determine the appropriate label based on the difference
        String label;
        if (daysDifference == 0) {
            label = "-fx-text-fill: #72a276";
        } else if (daysDifference == 1) {
            // #F28F3B
            label = "-fx-text-fill: #F28F3B";
        } else {
            // #A72608
            label = "-fx-text-fill: #A72608";
        }
        return label;
    }

    @FXML
    void onGoBack(MouseEvent event) throws IOException {
        Node node = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/manual_claims/manual-claims-main.fxml")));
        Scene scene = ((Node) event.getSource()).getScene();
        VBox mainVBox = (VBox) scene.lookup("#mainVBox");
        mainVBox.getChildren().setAll(node);
    }

    @FXML
    void onCheck(ActionEvent event) throws IOException {
        finalManualClaims.clear();
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                // ID, Name, Composer, Lyricist
                int id = manualClaims.get(i).getId();
                ManualClaimTrack claim = claimMap.get(id);
                System.out.println("claim.getBufferedImage() = " + manualClaims.get(i).getBufferedImage());
                finalManualClaims.add(claim);
                // finalSongNames.add(labelsSongName.get(i).getText());
            }
        }

        Node node = SceneController.loadLayout("layouts/manual_claims/manual-claims-identifiers.fxml");
        Scene scene = SceneController.getSceneFromEvent(event);
        VBox main = SceneController.getMainVBox(scene);
        main.getChildren().clear();
        main.getChildren().add(node);
    }

    @FXML
    void onSelectNone() {
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setSelected(false);
        }
    }

    @FXML
    void onSelectAll() {
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setSelected(true);
        }
    }

    public void onExportSelected(ActionEvent actionEvent) {
        List<ManualClaimTrack> selectedClaims = new ArrayList<>();

        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                selectedClaims.add(manualClaims.get(i));
            }
        }

        if (selectedClaims.isEmpty()) {
            System.out.println("No claims selected. Aborting export.");
        } else {
            File file = showSaveDialog(actionEvent);
            if (file == null) {
                System.out.println("No file selected. Aborting export.");
            } else {
                String path = file.getAbsolutePath();
                if (!path.toLowerCase().endsWith(".csv")) {
                    path += ".csv";
                }

                try (CSVWriter writer = new CSVWriter(new FileWriter(path))) {
                    List<String[]> rows = getRows(selectedClaims);

                    writer.writeAll(rows);
                    System.out.println("CSV file created successfully: " + path);

                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(new File(path));
                    }
                } catch (IOException e) {
                    System.err.println("Error creating CSV file: " + e.getMessage());
                    // You might want to show an error dialog to the user here
                }
            }
        }
    }

    private static @NotNull List<String[]> getRows(List<ManualClaimTrack> selectedClaims) {
        List<String[]> rows = new ArrayList<>();

        // Add header
        String[] header = {"ID", "Track Name", "Lyricist", "Composer", "YouTube ID", "YouTube URL", "Date", "Trim Start", "Trim End", "Claim Type"};
        rows.add(header);

        // Add data rows
        for (ManualClaimTrack claim : selectedClaims) {
            String[] row = {
                    String.valueOf(claim.getId()),
                    claim.getTrackName(),
                    claim.getLyricist(),
                    claim.getComposer(),
                    claim.getYoutubeID(),
                    claim.getYouTubeURL(),
                    claim.getDate().toString(),
                    claim.getTrimStart() != null ? claim.getTrimStart() : "",
                    claim.getTrimEnd() != null ? claim.getTrimEnd() : "",
                    claim.getClaimTypeString()
            };
            rows.add(row);
        }
        return rows;
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

    @FXML
    void onArchiveSelected() {
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                String songNo = labelsSongNo.get(i).getText();
                try {
                    DatabasePostgres.archiveSelectedClaim(songNo);
                    hBoxes.get(i).setDisable(true);
                } catch (SQLException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("An error occurred");
                    alert.setContentText(String.valueOf(e));
                    Platform.runLater(alert::showAndWait);

                    // e.printStackTrace();
                }
            }
        }
    }
}
