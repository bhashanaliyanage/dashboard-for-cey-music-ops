package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.ImageProcessor;
import com.example.song_finder_fx.Controller.NotificationBuilder;
import com.example.song_finder_fx.Controller.TextFormatter;
import com.example.song_finder_fx.Model.ManualClaimTrack;
import com.example.song_finder_fx.Model.Songs;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static com.example.song_finder_fx.Controller.ManualClaims.manualClaims;

public class ControllerManualClaims {

    @FXML
    public WebView ytPlayer;

    @FXML
    public VBox vboxTracks;

    @FXML
    private TextField txtURL;

    @FXML
    private ComboBox<String> comboClaimType;

    @FXML
    private Button btnAddClaim;

    @FXML
    public void initialize() {
        comboClaimType.getItems().addAll("Unspecified", "TV Programs", "Manual Claim", "Single SR");
    }

    @FXML
    void onCheckBtnClicked() throws IOException, SQLException {
        loadURL();
    }

    private void loadURL() throws SQLException, IOException {
        String URL = txtURL.getText();

        if (!Objects.equals(URL, "")) {
            String ID2 = TextFormatter.extractYoutubeID(URL);

            Task<List<Songs>> taskLoadVideo = new Task<>() {
                @Override
                protected java.util.List<Songs> call() {
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

    public void onAddManualClaim() {
        Task<List<Songs>> taskAddClaim = new Task<>() {
            @Override
            protected java.util.List<Songs> call() {
                for (ManualClaimTrack claim : manualClaims) {
                    try {
                        String songName = claim.getTrackName();

                        // Fetching Thumbnail
                        Platform.runLater(() -> {
                            btnAddClaim.setText("Fetching Artwork For: " + songName);
                        });
                        String youtubeID = claim.getYoutubeID();
                        String thumbnailURL = "https://i.ytimg.com/vi/" + youtubeID + "/maxresdefault.jpg";
                        BufferedImage image = ImageProcessor.getDownloadedImage(thumbnailURL);
                        image = ImageProcessor.cropImage(image);

                        // Setting Thumbnail and Preview Images to the model
                        claim.setPreviewImage(image);
                        image = ImageProcessor.resizeImage(1400, 1400, image);
                        claim.setImage(image);

                        int status = DatabasePostgres.addManualClaim(claim);

                        Platform.runLater(() -> {
                            try {
                                if (status < 1) {
                                    NotificationBuilder.displayTrayError("Error!", "Error Adding Manual Claim");
                                } else {
                                    NotificationBuilder.displayTrayInfo("Manual Claim Added", "Your Claim for " + songName + " is successfully added");
                                    Node node = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/manual_claims/manual-claims-track.fxml")));
                                    vboxTracks.getChildren().clear();
                                    vboxTracks.getChildren().add(node);
                                }
                            } catch (AWTException | IOException e) {
                                Platform.runLater(() -> {
                                    throw new RuntimeException(e);
                                });
                            }
                        });
                    } catch (URISyntaxException e) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("Error Downloading Artwork");
                            alert.setContentText(e.toString());
                            alert.showAndWait();
                        });
                    } catch (IOException | SQLException e) {
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

    public void urlOnAction(ActionEvent actionEvent) throws SQLException, IOException {
        loadURL();
    }
}
