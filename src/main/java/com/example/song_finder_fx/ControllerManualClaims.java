package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.NotificationBuilder;
import com.example.song_finder_fx.Controller.TextFormatter;
import com.example.song_finder_fx.Model.ManualClaimTrack;
import com.example.song_finder_fx.Model.Songs;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static com.example.song_finder_fx.Controller.ManualClaims.manualClaims;

public class ControllerManualClaims {

    @FXML
    public WebView ytPlayer;

    @FXML
    public VBox vboxTracks;

    public static VBox vboxTracksStatic;

    @FXML
    private TextField txtURL;

    public static TextField txtURL_Static;

    @FXML
    private ComboBox<String> comboClaimType;

    public static ComboBox<String> comboClaimTypeStatic;

    @FXML
    private Button btnAddClaim;

    @FXML
    public void initialize() {
        // comboClaimType.getItems().addAll("Unspecified", "TV Programs", "Manual Claim", "Single SR");
        comboClaimType.getItems().addAll(
                "Unspecified",
                "TV Programs",
                "Manual Claim",
                "Single SR",
                "Channel One",
                "Charana",
                "Chat Programmes",
                "Dawasak Da Handawaka",
                "Derana",
                "Derana 60+",
                "Derana Little Star",
                "Dream Star",
                "Hiru",
                "Hiru Star",
                "Imorich Tunes",
                "ITN",
                "Ma Nowana Mama",
                "Monara TV",
                "Roo Tunes",
                "Roopawahini",
                "Sirasa",
                "Siyatha",
                "Supreme TV",
                "Swarnawahini"
        );

        txtURL_Static = txtURL;
        comboClaimTypeStatic = comboClaimType;
        vboxTracksStatic = vboxTracks;
    }

    @FXML
    void onCheckBtnClicked() throws IOException, SQLException {
        loadURL();
    }

    private void loadURL() throws SQLException, IOException {
        String URL = txtURL.getText();

        if (!Objects.equals(URL, "")) {
            String ID2 = TextFormatter.extractYoutubeID(URL);

            Thread threadLoadVideo = getThreadLoadVideo(ID2);
            threadLoadVideo.start();

            // If this ID is in the manual claims database, show an alert.
            int previousClaims = DatabasePostgres.checkPreviousClaims(ID2);
            if (previousClaims > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Previous Added Claims Found");
                alert.setHeaderText("Previous Added Claims Found");
                alert.setContentText(previousClaims + " Previous claims found for this video");

                alert.showAndWait();
            }

            comboClaimType.requestFocus();

            Node node = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/manual_claims/manual-claims-track.fxml")));
            vboxTracks.getChildren().clear();
            vboxTracks.getChildren().add(node);

            manualClaims.clear();
        } else {
            System.out.println("URL Empty");
        }
    }

    private @NotNull Thread getThreadLoadVideo(String ID2) {
        Task<List<Songs>> taskLoadVideo = new Task<>() {
            @Override
            protected List<Songs> call() {
                try {
                    String embedID = "https://www.youtube.com/embed/" + ID2;
                    Platform.runLater(() -> ytPlayer.getEngine().load(embedID));
                    // ytPlayer.getEngine().load(embedID);
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error!");
                        alert.setHeaderText("Error Loading URL");
                        alert.setContentText(e.toString());
                        alert.showAndWait();
                    });
                }
                return null;
            }
        };

        Thread threadLoadVideo = new Thread(taskLoadVideo);
        return threadLoadVideo;
    }

    public void onAddManualClaim() {
        Task<List<Songs>> taskAddClaim = new Task<>() {
            @Override
            protected java.util.List<Songs> call() {
                for (ManualClaimTrack claim : manualClaims) {
                    try {
                        String songName = claim.getTrackName();

                        int status = DatabasePostgres.addManualClaim(claim);

                        Platform.runLater(() -> {
                            try {
                                if (status < 1) {
                                    NotificationBuilder.displayTrayError("Error!", "Error Adding Manual Claim");
                                } else {
                                    NotificationBuilder.displayTrayInfo("Manual Claim Added", "Your Claim for " + songName + " is successfully added");
                                    // Node node = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/manual_claims/manual-claims-track.fxml")));
                                    Node node = FXMLLoader.load(Objects.requireNonNull(UIController.class.getResource("layouts/manual_claims/manual-claims.fxml")));
                                    UIController.mainNodes[6] = node;
                                    UIController.mainVBoxStatic.getChildren().setAll(node);
                                    // vboxTracks.getChildren().clear();
                                    // vboxTracks.getChildren().add(node);
                                }
                            } catch (IOException e) {
                                Platform.runLater(() -> {
                                    throw new RuntimeException(e);
                                });
                            }
                        });
                    } catch (IOException | SQLException e) {
                        e.printStackTrace();
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("Error Occurred When Adding Claim");
                            alert.setContentText(e.toString());
                            alert.showAndWait();
                        });
                    }
                }

                manualClaims.clear();

                Platform.runLater(() -> {
                    btnAddClaim.setText("Add Manual Claim");
                });

                return null;
            }
        };

        Thread threadAddClaim = new Thread(taskAddClaim);
        threadAddClaim.start();

    }

    @FXML
    void onGoBack(MouseEvent event) throws IOException {
        Node node = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/manual_claims/manual-claims-main.fxml")));
        Scene scene = ((Node) event.getSource()).getScene();
        VBox mainVBox = (VBox) scene.lookup("#mainVBox");
        mainVBox.getChildren().setAll(node);
    }

    public void urlOnAction() throws SQLException, IOException {
        loadURL();
    }
}
