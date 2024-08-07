package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Model.Songs;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import javax.sound.sampled.Clip;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static com.example.song_finder_fx.UIController.setPlayerInfo;

public class ControllerSearchSong {

    @FXML
    private Button btnCopy;

    @FXML
    private HBox hbox2;

    @FXML
    private HBox hboxSongSearch;

    @FXML
    private Label searchResultISRC;

    @FXML
    private Label songName;

    @FXML
    private Label songSinger;

    @FXML
    private VBox vboxSongDetails;

    @FXML
    private VBox vboxSongSearch;

    @FXML
    private ImageView imgLoading;

    private boolean toggle = false;

    Songs songDetails;

    @FXML
    void contextM(ActionEvent event) {
        System.out.println("ControllerSearch.contextM");
        String isrc = searchResultISRC.getText();
        try {
            songDetails = DatabasePostgres.searchSongDetails(isrc);
            System.out.println("Fetched song details!");
        } catch (SQLException | ClassNotFoundException e) {
            AlertBuilder.sendErrorAlert("Error!", "Error Occurred While Fetching Song", e.toString());
        }
    }

    @FXML
    void copyComposer(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();

        Songs song = new Songs("null", "null");
        try {
            song = DatabasePostgres.searchSongDetails(searchResultISRC.getText());
        } catch (SQLException | ClassNotFoundException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Fetching Song Details", e.toString());
        }

        content.putString(song.getComposer());
        boolean status = clipboard.setContent(content);

        System.out.println("status = " + status);

        if (status) {
            btnCopy.setText("Copied");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> btnCopy.setText("Copy")));
            timeline.play();
        }
    }

    @FXML
    void copyFeaturing(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();

        Songs song = new Songs("null", "null");
        try {
            song = DatabasePostgres.searchSongDetails(searchResultISRC.getText());
        } catch (SQLException | ClassNotFoundException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Fetching Song Details", e.toString());
        }

        content.putString(song.getFeaturing());
        boolean status = clipboard.setContent(content);
        System.out.println("status = " + status);
        if (status) {
            btnCopy.setText("Copied");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> btnCopy.setText("Copy")));
            timeline.play();
        }
    }

    @FXML
    void copyISRC(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();

        Songs song = new Songs("null", "null");
        try {
            song = DatabasePostgres.searchSongDetails(searchResultISRC.getText());
        } catch (SQLException | ClassNotFoundException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Fetching Song Details", e.toString());
        }

        content.putString(song.getISRC());
        boolean status = clipboard.setContent(content);
        System.out.println("status = " + status);
        if (status) {
            btnCopy.setText("Copied");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> btnCopy.setText("Copy")));
            timeline.play();
        }
    }

    @FXML
    void copyLyricist(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();

        Songs song = new Songs("null", "null");
        try {
            song = DatabasePostgres.searchSongDetails(searchResultISRC.getText());
        } catch (SQLException | ClassNotFoundException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Fetching Song Details", e.toString());
        }

        content.putString(song.getLyricist());
        boolean status = clipboard.setContent(content);
        System.out.println("status = " + status);
        if (status) {
            btnCopy.setText("Copied");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> btnCopy.setText("Copy")));
            timeline.play();
        }
    }

    @FXML
    void copyProductName(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();

        Songs song = new Songs("null", "null");
        try {
            song = DatabasePostgres.searchSongDetails(searchResultISRC.getText());
        } catch (SQLException | ClassNotFoundException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Fetching Song Details", e.toString());
        }

        content.putString(song.getProductName());
        boolean status = clipboard.setContent(content);
        System.out.println("status = " + status);
        if (status) {
            btnCopy.setText("Copied");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> btnCopy.setText("Copy")));
            timeline.play();
        }
    }

    @FXML
    void copySinger(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();

        Songs song = new Songs("null", "null");
        try {
            song = DatabasePostgres.searchSongDetails(searchResultISRC.getText());
        } catch (SQLException | ClassNotFoundException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Fetching Song Details", e.toString());
        }

        content.putString(song.getSinger());
        boolean status = clipboard.setContent(content);
        System.out.println("status = " + status);
        if (status) {
            btnCopy.setText("Copied");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> btnCopy.setText("Copy")));
            timeline.play();
        }
    }

    @FXML
    void copySongName(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();

        Songs song = new Songs("null", "null");
        try {
            song = DatabasePostgres.searchSongDetails(searchResultISRC.getText());
        } catch (SQLException | ClassNotFoundException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Fetching Song Details", e.toString());
        }

        content.putString(song.getTrackTitle());
        boolean status = clipboard.setContent(content);
        System.out.println("status = " + status);
        if (status) {
            btnCopy.setText("Copied");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> btnCopy.setText("Copy")));
            timeline.play();
        }
    }

    @FXML
    void copyUPC(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();

        Songs song = new Songs("null", "null");
        try {
            song = DatabasePostgres.searchSongDetails(searchResultISRC.getText());
        } catch (SQLException | ClassNotFoundException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Fetching Song Details", e.toString());
        }

        content.putString(song.getUPC());
        boolean status = clipboard.setContent(content);
        System.out.println("status = " + status);
        if (status) {
            btnCopy.setText("Copied");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> btnCopy.setText("Copy")));
            timeline.play();
        }
    }

    @FXML
    void onAddToListButtonClickedInSearchSong(MouseEvent event) {
        // Getting scene
        Node node = (Node) event.getSource();
        Scene scene = node.getScene();

        // Getting label from scene
        Label songListButtonSubtitle = (Label) scene.lookup("#lblSongListSub");

        // Adding songs to list
        String isrc = searchResultISRC.getText();
        Songs song = null;
        try {
            song = DatabasePostgres.searchSongDetails(isrc);
        } catch (SQLException | ClassNotFoundException e) {
            AlertBuilder.sendErrorAlert("Error!", "Error Occurred While Fetching Song", e.toString());
        }
        Main.addSongToList(song);

        List<Songs> songList = Main.getSongListNew();
        int songListLength = songList.size();

        if (songListLength > 1) {
            String text = songList.getFirst().getISRC() + " + " + (songListLength - 1) + " other songs added";
            songListButtonSubtitle.setText(text);
            System.out.println(text);
        } else {
            songListButtonSubtitle.setText(songList.getFirst().getISRC());
            System.out.println(songList.getFirst());
        }

        hbox2.setStyle("-fx-border-color: #6eb0e0");
    }

    @FXML
    void onBtnPlayClicked(MouseEvent mouseEvent) {
        Image imgTimer = new Image("com/example/song_finder_fx/images/icon _timer.png");
        String isrc;

        Main.directoryCheck();
        Songs song = new Songs();

        Node node = (Node) mouseEvent.getSource();
        Scene scene = node.getScene();

        Label lblPlayerSongName = (Label) scene.lookup("#lblPlayerSongName");
        Label lblPlayerArtist = (Label) scene.lookup("#lblPlayerSongArtst");
        ImageView imgMediaPico = (ImageView) scene.lookup("#imgMediaPico");

        song.setTrackTitle(songName.getText());
        song.setSinger(songSinger.getText());
        isrc = searchResultISRC.getText();

        Task<Void> task;
        Path start = Paths.get(Main.selectedDirectory.toURI());
        final boolean[] status = new boolean[1];

        lblPlayerSongName.setText("Loading audio");
        lblPlayerSongName.setStyle("-fx-text-fill: '#000000'");
        imgMediaPico.setImage(imgTimer);

        String finalIsrc = isrc;
        task = new Task<>() {
            @Override
            protected Void call() {
                Clip clip = Main.getClip();
                if (clip != null) {
                    clip.stop();
                }
                Platform.runLater(() -> System.out.println("Preparing to play audio..."));
                try {
                    status[0] = Main.playAudio(start, finalIsrc);
                } catch (IOException e) {
                    Platform.runLater(() -> AlertBuilder.sendErrorAlert("Error", "Error Playing Audio", "Cannot access audio database location: " + start + "\n" + e));
                }
                return null;
            }
        };

        task.setOnSucceeded(event -> setPlayerInfo(status, lblPlayerSongName, lblPlayerArtist, imgMediaPico, song));

        new Thread(task).start();
    }

    @FXML
    void onSearchedSongClick(MouseEvent event) {

        Duration duration = Duration.seconds(0.100);

        // Create a timeline for increasing heights
        Timeline timelineIncreaseHeight = new Timeline(
                new KeyFrame(duration, new KeyValue(vboxSongSearch.prefHeightProperty(), 190)),
                new KeyFrame(duration, new KeyValue(hboxSongSearch.prefHeightProperty(), 180))
        );

        HBox hbox = new HBox();
        Node node;
        try {
            node = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/search-song-expanded-view.fxml")));

            hbox.getChildren().add(node);

            if (!toggle) {
                imgLoading.setVisible(true);
                timelineIncreaseHeight.play();
                timelineIncreaseHeight.setOnFinished(event2 -> {
                    vboxSongDetails.getChildren().add(hbox);
                    Label lblFeaturing = (Label) node.lookup("#lblFeaturing");
                    Label lblProductName = (Label) node.lookup("#lblProductName");
                    Label lblUPC = (Label) node.lookup("#lblUPC");
                    Label lblShare = (Label) node.lookup("#lblShare");

                    String isrc = searchResultISRC.getText();

                    Task<Void> task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            try {
                                songDetails = DatabasePostgres.searchSongDetails(isrc);
                                // songDetails = DatabaseMySQL.searchSongDetails(isrc);
                            } catch (SQLException | ClassNotFoundException e) {
                                Platform.runLater(() -> {
                                    AlertBuilder.sendErrorAlert("Error!", "Error Loading Details", e.toString());
                                });
                            }

                            String percentage = "Unspecified";
                            try {
                                if (songDetails.composerAndLyricistCeyMusic()) {
                                    percentage = "100%";
                                } else if (songDetails.composerOrLyricistCeyMusic()) {
                                    percentage = "50%";
                                } else {
                                    percentage = "0%";
                                }
                            } catch (SQLException | ClassNotFoundException e) {
                                Platform.runLater(() -> {
                                    AlertBuilder.sendErrorAlert("Error!", "Error Loading Details", e.toString());
                                });
                            }
                            songDetails.setPercentage(percentage);

                            String finalPercentage = percentage;
                            Platform.runLater(() -> {
                                lblFeaturing.setText(songDetails.getFeaturing());
                                lblProductName.setText(songDetails.getProductName());
                                lblUPC.setText(songDetails.getUPC());
                                lblShare.setText(finalPercentage);
                                imgLoading.setVisible(false);
                            });
                            return null;
                        }
                    };

                    Thread thread = new Thread(task);
                    thread.start();
                });
                // Play the animation

                toggle = true;
            } else {
                // Create a timeline for increasing heights
                vboxSongDetails.getChildren().remove(3);
                Timeline timelineDecreaseHeigt = new Timeline(
                        new KeyFrame(duration, new KeyValue(vboxSongSearch.prefHeightProperty(), 100)),
                        new KeyFrame(duration, new KeyValue(hboxSongSearch.prefHeightProperty(), 90))
                );
                timelineDecreaseHeigt.play();
                toggle = false;
            }
        } catch (IOException e) {
            AlertBuilder.sendErrorAlert("Error!", "Error Initializing UI", e.toString());
        }
    }

}
