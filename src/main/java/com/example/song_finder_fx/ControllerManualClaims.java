package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.NotificationBuilder;
import com.example.song_finder_fx.Controller.TextFormatter;
import com.example.song_finder_fx.Model.ManualClaimTrack;
import com.example.song_finder_fx.Model.Songs;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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

    public static WebView ytPlayerStatic;

    public static List<String> ceyMusicArtists;

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
        ytPlayerStatic = ytPlayer;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
                    ceyMusicArtists = DatabasePostgres.getAllCeyMusicArtists();
                } catch (Exception ignore) {

                }
                return null;
            }
        };

        new Thread(task).start();
    }

    @FXML
    void onCheckBtnClicked() {
        try {
            loadURL();
        } catch (IOException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Loading URL", e.toString());
        }
    }

    private void loadURL() throws IOException {
        String URL = txtURL.getText();

        if (!Objects.equals(URL, "")) {
            String ID2 = TextFormatter.extractYoutubeID(URL);

            Thread threadLoadVideo = getThreadLoadVideo(ID2);
            threadLoadVideo.start();

            Thread showPreviousClaims = getThreadPreviousClaims(ID2);
            showPreviousClaims.start();

            comboClaimType.requestFocus();

            Node node = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/manual_claims/manual-claims-track.fxml")));
            vboxTracks.getChildren().clear();
            vboxTracks.getChildren().add(node);

            manualClaims.clear();
        } else {
            System.out.println("URL Empty");
        }
    }

    private Thread getThreadPreviousClaims(String id2) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
                    List<ManualClaimTrack> previousClaims = DatabasePostgres.checkPreviousClaimsNew(id2);

                    if (!previousClaims.isEmpty()) {
                        Platform.runLater(() -> AlertBuilder.sendInfoAlert("Previous Added Claims Found", "Previous Added Claims Found", "Previous claims found for this video"));

                        Node node = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/manual_claims/sidepanel-previous-claims.fxml")));

                        Label lblClaimCount = (Label) node.lookup("#lblClaimCount");
                        Label lblYouTubeID = (Label) node.lookup("#lblYouTubeID");
                        // Scene scene = node.getScene();
                        // VBox vBoxClaims = (VBox) node.lookup("#vBoxClaims");
                        ScrollPane scrollPaneClaims = (ScrollPane) node.lookup("#scrollPaneClaims");
                        VBox vbox = new VBox();
                        vbox.setSpacing(5);
                        Platform.runLater(() -> scrollPaneClaims.setContent(vbox));


                        Platform.runLater(() -> {
                            lblClaimCount.setText(previousClaims.size() + "");
                            lblYouTubeID.setText(id2);
                            UIController.sideVBoxStatic.getChildren().setAll(node);
                        });

                        for (ManualClaimTrack manualClaimTrack : previousClaims) {
                            String claimID = String.valueOf(manualClaimTrack.getId());
                            String claimName = manualClaimTrack.getTrackName();
                            String trimStart = manualClaimTrack.getTrimStart();
                            String trimEnd = manualClaimTrack.getTrimEnd();
                            String date = manualClaimTrack.getDate().toString();

                            Node node2 = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/manual_claims/sidepanel-previous-claim-entry.fxml")));
                            Label lblClaimID = (Label) node2.lookup("#lblClaimID");
                            Label lblClaimName = (Label) node2.lookup("#lblClaimName");
                            Label lblTrimStart = (Label) node2.lookup("#lblTrimStart");
                            Label lblTrimEnd = (Label) node2.lookup("#lblTrimEnd");
                            Label lblDate = (Label) node2.lookup("#lblDate");

                            Platform.runLater(() -> {
                                lblClaimID.setText(claimID);
                                lblClaimName.setText(claimName);
                                lblTrimStart.setText(trimStart);
                                lblTrimEnd.setText(trimEnd);
                                lblDate.setText(date);
                                vbox.getChildren().add(node2);
                            });
                        }
                    }
                } catch (Exception e) {
                    Platform.runLater(() -> AlertBuilder.sendErrorAlert("Error", "Error Loading URL", e.toString()));
                }
                return null;
            }
        };

        return new Thread(task);
    }

    private @NotNull Thread getThreadLoadVideo(String ID2) {
        Task<List<Songs>> taskLoadVideo = new Task<>() {
            @Override
            protected List<Songs> call() {
                Platform.runLater(() -> {
                    try {
                        // Clear the WebView content
                        ytPlayer.getEngine().loadContent("");

                        ytPlayer.getEngine().setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
                        ytPlayer.getEngine().setJavaScriptEnabled(true);

                        String htmlContent = loadHtmlFromFile();
                        ytPlayer.getEngine().loadContent(htmlContent);

                        ytPlayer.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
                            if (newValue == Worker.State.SUCCEEDED) {
                                ytPlayer.getEngine().executeScript(
                                        "var meta = document.createElement('meta'); " +
                                                "meta.httpEquiv = 'Content-Security-Policy'; " +
                                                "meta.content = \"default-src * 'unsafe-inline' 'unsafe-eval'\"; " +
                                                "document.getElementsByTagName('head')[0].appendChild(meta);"
                                );

                                // Load the video after the page has loaded
                                ytPlayer.getEngine().executeScript("loadVideo('" + ID2 + "')");
                            }
                        });
                    } catch (Exception e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error!");
                        alert.setHeaderText("Error Loading URL");
                        alert.setContentText(e.toString());
                        alert.showAndWait();
                    }
                });
                return null;
            }
        };

        return new Thread(taskLoadVideo);
    }

    // Method to load HTML from file
    private String loadHtmlFromFile() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("layouts/manual_claims/youtube_player.html")) {
            if (is != null) {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            } else {
                System.err.println("Could not find file: " + "layouts/manual_claims/youtube_player.html");
                return "";
            }
        }
    }

    public void onAddManualClaim() {
        ytPlayer.getEngine().executeScript("loadVideo()");
        Task<List<Songs>> taskAddClaim = new Task<>() {
            @Override
            protected java.util.List<Songs> call() {
                StringBuilder errorBuffer = new StringBuilder();

                for (ManualClaimTrack claim : manualClaims) {
                    try {
                        String songName = claim.getTrackName();

                        int status = DatabasePostgres.addManualClaim(claim);

                        Platform.runLater(() -> {
                            if (status < 1) {
                                errorBuffer.append("Error adding manual claim for ").append(songName).append("\n");
                            } else {
                                NotificationBuilder.displayTrayInfo("Manual Claim Added", "Your Claim for " + songName + " is successfully added");
                                /*Node node = FXMLLoader.load(Objects.requireNonNull(UIController.class.getResource("layouts/manual_claims/manual-claims.fxml")));
                                UIController.mainNodes[6] = node;
                                UIController.mainVBoxStatic.getChildren().setAll(node);*/
                            }
                        });
                    } catch (IOException | SQLException e) {
                        errorBuffer.append("Error adding claim for ").append(claim.getTrackName()).append(": ").append(e.getMessage()).append("\n");
                    }
                }

                manualClaims.clear();

                Platform.runLater(() -> {
                    btnAddClaim.setText("Add Manual Claim");
                    UIController.blankSidePanel();

                    if (!errorBuffer.isEmpty()) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Errors Occurred");
                        alert.setHeaderText("Some errors occurred while adding claims");
                        alert.setContentText(errorBuffer.toString());
                        alert.showAndWait();
                    } else {
                        try {
                            Node node = FXMLLoader.load(Objects.requireNonNull(UIController.class.getResource("layouts/manual_claims/manual-claims.fxml")));
                            UIController.mainNodes[6] = node;
                            UIController.mainVBoxStatic.getChildren().setAll(node);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
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

    public void urlOnAction() {
        try {
            loadURL();
        } catch (IOException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Loading URL", e.toString());
        }
    }

    @FXML
    private void onButtonClick() {
        WebEngine engine = ytPlayer.getEngine();
        String currentTime = (String) engine.executeScript("document.getElementById('current-time').innerHTML");
        System.out.println("Current time: " + currentTime);
    }
}
