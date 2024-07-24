package com.example.song_finder_fx;

import com.example.song_finder_fx.Constants.SearchType;
import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Model.Search;
import com.example.song_finder_fx.Model.Songs;
import com.example.song_finder_fx.Organizer.SongSearch;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import javax.sound.sampled.Clip;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static com.example.song_finder_fx.UIController.setPlayerInfo;

public class ControllerSearch {
    public HBox hboxSongSearch;
    public VBox vboxSongDetails;
    public HBox hbox2;

    @FXML
    private Label searchResultISRC;

    @FXML
    private Label songName;

    @FXML
    private Label songSinger;

    @FXML
    private VBox vboxSongSearch;

    public VBox mainVBox;
    @FXML
    private Label lblSearchType;

    @FXML
    private ScrollPane scrlpneSong;

    @FXML
    private TextField searchArea;

    @FXML
    private VBox vboxSong;

    @FXML
    private Button btnCopy;

    private Parent newContent;

    private final Search searchOld = new Search();

    private boolean toggle = false;

    Songs songDetails;

    private SongSearch search;

    private String searchType = SearchType.SONG_NAME;

    public ControllerSearch() {

    }

    @FXML
    void initialize() {
        // DatabaseHandler databaseHandler = new DatabaseHandler();
        search = new SongSearch();
    }

    @FXML
    void btnSetSearchTypeComposer() {
        searchOld.setType("artist");
        searchType = SearchType.COMPOSER;

        /*searchArea.setPromptText("Enter Composer Name");
        lblSearchType.setText("Composer");*/

        Timeline timeline = new Timeline();

        // Fade out
        KeyFrame fadeOut = new KeyFrame(Duration.millis(300),
                new KeyValue(searchArea.opacityProperty(), 0),
                new KeyValue(lblSearchType.opacityProperty(), 0)
        );

        // Change text
        KeyFrame changeText = new KeyFrame(Duration.millis(301),
                e -> {
                    searchArea.setPromptText("Enter Composer Name");
                    lblSearchType.setText("Composer");
                }
        );

        // Fade in
        KeyFrame fadeIn = new KeyFrame(Duration.millis(600),
                new KeyValue(searchArea.opacityProperty(), 1),
                new KeyValue(lblSearchType.opacityProperty(), 1)
        );

        timeline.getKeyFrames().addAll(fadeOut, changeText, fadeIn);
        timeline.play();
    }

    @FXML
    void btnSetSearchTypeISRC() {
        searchOld.setType("isrc");
        searchType = SearchType.ISRC;

        /*searchArea.setPromptText("Enter ISRC");
        lblSearchType.setText("ISRC");*/

        Timeline timeline = new Timeline();

        // Fade out
        KeyFrame fadeOut = new KeyFrame(Duration.millis(300),
                new KeyValue(searchArea.opacityProperty(), 0),
                new KeyValue(lblSearchType.opacityProperty(), 0)
        );

        // Change text
        KeyFrame changeText = new KeyFrame(Duration.millis(301),
                e -> {
                    searchArea.setPromptText("Enter ISRC");
                    lblSearchType.setText("ISRC");
                }
        );

        // Fade in
        KeyFrame fadeIn = new KeyFrame(Duration.millis(600),
                new KeyValue(searchArea.opacityProperty(), 1),
                new KeyValue(lblSearchType.opacityProperty(), 1)
        );

        timeline.getKeyFrames().addAll(fadeOut, changeText, fadeIn);
        timeline.play();
    }

    @FXML
    void btnSetSearchTypeLyricist() {
        searchOld.setType("artist");
        searchType = SearchType.LYRICIST;

        /*searchArea.setPromptText("Enter Lyricist Name");
        lblSearchType.setText("Lyricist");*/

        Timeline timeline = new Timeline();

        // Fade out
        KeyFrame fadeOut = new KeyFrame(Duration.millis(300),
                new KeyValue(searchArea.opacityProperty(), 0),
                new KeyValue(lblSearchType.opacityProperty(), 0)
        );

        // Change text
        KeyFrame changeText = new KeyFrame(Duration.millis(301),
                e -> {
                    searchArea.setPromptText("Enter Lyricist Name");
                    lblSearchType.setText("Lyricist");
                }
        );

        // Fade in
        KeyFrame fadeIn = new KeyFrame(Duration.millis(600),
                new KeyValue(searchArea.opacityProperty(), 1),
                new KeyValue(lblSearchType.opacityProperty(), 1)
        );

        timeline.getKeyFrames().addAll(fadeOut, changeText, fadeIn);
        timeline.play();
    }

    @FXML
    void btnSetSearchTypeName() {
        searchOld.setType("song_name");
        searchType = SearchType.SONG_NAME;

        /*searchArea.setPromptText("Enter Song Name");
        lblSearchType.setText("Name");*/

        Timeline timeline = new Timeline();

        // Fade out
        KeyFrame fadeOut = new KeyFrame(Duration.millis(300),
                new KeyValue(searchArea.opacityProperty(), 0),
                new KeyValue(lblSearchType.opacityProperty(), 0)
        );

        // Change text
        KeyFrame changeText = new KeyFrame(Duration.millis(301),
                e -> {
                    searchArea.setPromptText("Enter Song Name");
                    lblSearchType.setText("Name");
                }
        );

        // Fade in
        KeyFrame fadeIn = new KeyFrame(Duration.millis(600),
                new KeyValue(searchArea.opacityProperty(), 1),
                new KeyValue(lblSearchType.opacityProperty(), 1)
        );

        timeline.getKeyFrames().addAll(fadeOut, changeText, fadeIn);
        timeline.play();
    }

    @FXML
    void btnSetSearchTypeSinger() {
        searchOld.setType("artist");
        searchType = SearchType.SINGER;

        /*searchArea.setPromptText("Enter Singer Name");
        lblSearchType.setText("Singer");*/

        Timeline timeline = new Timeline();

        // Fade out
        KeyFrame fadeOut = new KeyFrame(Duration.millis(300),
                new KeyValue(searchArea.opacityProperty(), 0),
                new KeyValue(lblSearchType.opacityProperty(), 0)
        );

        // Change text
        KeyFrame changeText = new KeyFrame(Duration.millis(301),
                e -> {
                    searchArea.setPromptText("Enter Singer Name");
                    lblSearchType.setText("Singer");
                }
        );

        // Fade in
        KeyFrame fadeIn = new KeyFrame(Duration.millis(600),
                new KeyValue(searchArea.opacityProperty(), 1),
                new KeyValue(lblSearchType.opacityProperty(), 1)
        );

        timeline.getKeyFrames().addAll(fadeOut, changeText, fadeIn);
        timeline.play();
    }

    @FXML
    void getText() {
        // Getting search keywords
        String text = searchArea.getText();

        scrlpneSong.setVisible(true);
        scrlpneSong.setContent(vboxSong);

        Task<List<Songs>> task = new Task<>() {
            @Override
            protected java.util.List<Songs> call() {
                return search.searchSong(text, searchType);
                // return searchOld.search(text);
            }
        };

        task.setOnSucceeded(e -> {
            List<Songs> songList = task.getValue();
            Node[] nodes;
            nodes = new Node[songList.size()];
            vboxSong.getChildren().clear();
            for (int i = 0; i < nodes.length; i++) {
                try {
                    nodes[i] = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("layouts/search-song.fxml")));
                    Label lblSongName = (Label) nodes[i].lookup("#songName");
                    Label lblISRC = (Label) nodes[i].lookup("#searchResultISRC");
                    Label lblArtist = (Label) nodes[i].lookup("#songSinger");
                    Label lblComposer = (Label) nodes[i].lookup("#searchResultComposer");
                    Label lblLyricist = (Label) nodes[i].lookup("#searchResultLyricist");
                    Label songType = (Label) nodes[i].lookup("#songType");
                    HBox hbox2 = (HBox) nodes[i].lookup("#hbox2");
                    lblSongName.setText(songList.get(i).getTrackTitle());
                    lblISRC.setText(songList.get(i).getISRC().trim());
                    lblArtist.setText(songList.get(i).getSinger().trim());
                    lblComposer.setText(songList.get(i).getComposer().trim());
                    lblLyricist.setText(songList.get(i).getLyricist().trim());

                    if (songList.get(i).isOriginal()) {
                        songType.setVisible(true);
                    }

                    if (songList.get(i).isInList()) {
                        hbox2.setStyle("-fx-border-color: #6eb0e0");
                    }

                    vboxSong.getChildren().add(nodes[i]);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Thread thread = new Thread(task);
        thread.start();
    }

    @FXML
    void getText2() {
        // Getting search keywords
        String text = searchArea.getText();

        scrlpneSong.setVisible(true);
        scrlpneSong.setContent(vboxSong);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                List<Songs> songList = search.searchSong(text, searchType);
                // return searchOld.search(text);

                try {

                    Platform.runLater(() -> vboxSong.getChildren().clear());

                    for (Songs song : songList) {
                        Node node = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("layouts/search-song.fxml")));

                        Label lblSongName = (Label) node.lookup("#songName");
                        Label lblISRC = (Label) node.lookup("#searchResultISRC");
                        Label lblArtist = (Label) node.lookup("#songSinger");
                        Label lblComposer = (Label) node.lookup("#searchResultComposer");
                        Label lblLyricist = (Label) node.lookup("#searchResultLyricist");
                        Label songType = (Label) node.lookup("#songType");
                        HBox hbox2 = (HBox) node.lookup("#hbox2");

                        Platform.runLater(() -> {
                            lblSongName.setText(song.getTrackTitle());
                            lblISRC.setText(song.getISRC());
                            lblArtist.setText(getSinger(song.getSinger()));
                            lblComposer.setText(song.getComposer());
                            lblLyricist.setText(song.getLyricist());

                            if (song.isOriginal()) {
                                songType.setVisible(true);
                            }

                            if (song.isInList()) {
                                hbox2.setStyle("-fx-border-color: #6eb0e0");
                            }

                            vboxSong.getChildren().add(node);
                        });
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> AlertBuilder.sendErrorAlert("Error", "Error occurred when loading song", e.toString()));
                }

                /*Node[] nodes;
                nodes = new Node[songList.size()];
                // System.out.println("songList.size() = " + songList.size());
                for (int i = 0; i < nodes.length; i++) {
                    try {
                        nodes[i] = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("layouts/search-song.fxml")));
                        Label lblSongName = (Label) nodes[i].lookup("#songName");
                        Label lblISRC = (Label) nodes[i].lookup("#searchResultISRC");
                        Label lblArtist = (Label) nodes[i].lookup("#songSinger");
                        Label lblComposer = (Label) nodes[i].lookup("#searchResultComposer");
                        Label lblLyricist = (Label) nodes[i].lookup("#searchResultLyricist");
                        Label songType = (Label) nodes[i].lookup("#songType");
                        HBox hbox2 = (HBox) nodes[i].lookup("#hbox2");

                        lblSongName.setText(songList.get(i).getTrackTitle());
                        lblISRC.setText(songList.get(i).getISRC().trim());
                        lblArtist.setText(songList.get(i).getSinger().trim());
                        lblComposer.setText(songList.get(i).getComposer().trim());
                        lblLyricist.setText(songList.get(i).getLyricist().trim());

                        if (songList.get(i).isOriginal()) {
                            songType.setVisible(true);
                        }

                        if (songList.get(i).isInList()) {
                            hbox2.setStyle("-fx-border-color: #6eb0e0");
                        }

                        int finalI = i;
                        Platform.runLater(() -> vboxSong.getChildren().add(nodes[finalI]));
                    } catch (IOException ex) {
                        Platform.runLater(() -> {
                            AlertBuilder.sendErrorAlert("Error", "Error occurred when loading song", ex.toString());
                        });
                    }
                }*/
                // return songList;
                return null;
            }

            private String getSinger(String singer) {
                if (singer == null) {
                    return "Unspecified";
                } else {
                    if (singer.equals("null")) {
                        return "Unspecified";
                    } else {
                        return singer;
                    }
                }
            }
        };

        Thread thread = new Thread(task);
        thread.start();
    }

    public void testSlide() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("layouts/sidepanel-song-view.fxml"));
        newContent = loader.load();

        mainVBox.getChildren().set(1, newContent);
    }

    @FXML
    void onAddToListButtonClickedInSearchSong(MouseEvent event) {
        // Getting scene
        Node node = (Node) event.getSource();
        Scene scene = node.getScene();

        // Getting label from scene
        Label songListButtonSubtitle = (Label) scene.lookup("#lblSongListSub");

        // Adding songs to list
        Main.addSongToList(searchResultISRC.getText());

        // List<Songs> songList = Main.getSongList();
        List<Songs> songListNew = Main.getSongList();
        int songListLength = songListNew.size();

        if (songListLength > 1) {
            String text = songListNew.getFirst().getISRC() + " + " + (songListLength - 1) + " other songs added";
            songListButtonSubtitle.setText(text);
            // System.out.println(text);
        } else {
            songListButtonSubtitle.setText(songListNew.getFirst().getISRC());
            // System.out.println(songList.getFirst());
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
            protected Void call() throws Exception {
                Clip clip = Main.getClip();
                if (clip != null) {
                    clip.stop();
                }
                Platform.runLater(() -> System.out.println("Preparing to play audio..."));
                status[0] = Main.playAudio(start, finalIsrc);
                return null;
            }
        };

        task.setOnSucceeded(event -> setPlayerInfo(status, lblPlayerSongName, lblPlayerArtist, imgMediaPico, song));

        new Thread(task).start();
    }

    @FXML
    void onSearchedSongClick() throws IOException, SQLException, ClassNotFoundException {
        Duration duration = Duration.seconds(0.100);

        // Create a timeline for increasing heights
        Timeline timelineIncreaseHeight = new Timeline(
                new KeyFrame(duration, new KeyValue(vboxSongSearch.prefHeightProperty(), 190)),
                new KeyFrame(duration, new KeyValue(hboxSongSearch.prefHeightProperty(), 180))
        );

        HBox hbox = new HBox();
        Node node = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/search-song-expanded-view.fxml")));
        Scene scene = vboxSongSearch.getScene();
        hbox.getChildren().add(node);

        if (!toggle) {
            timelineIncreaseHeight.play();
            timelineIncreaseHeight.setOnFinished(event -> {
                vboxSongDetails.getChildren().add(hbox);
                Label lblFeaturing = (Label) scene.lookup("#lblFeaturing");
                Label lblProductName = (Label) scene.lookup("#lblProductName");
                Label lblUPC = (Label) scene.lookup("#lblUPC");
                Label lblShare = (Label) scene.lookup("#lblShare");

                String isrc = searchResultISRC.getText();
                try {
                    // songDetails = DatabaseMySQL.searchSongDetails(isrc);
                    songDetails = DatabasePostgres.searchSongDetails(isrc);
                } catch (SQLException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

                String percentage;
                try {
                    if (songDetails.composerAndLyricistCeyMusic()) {
                        percentage = "100%";
                    } else if (songDetails.composerOrLyricistCeyMusic()) {
                        percentage = "50%";
                    } else {
                        percentage = "0%";
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                songDetails.setPercentage(percentage);

                lblFeaturing.setText(songDetails.getFeaturing());
                lblProductName.setText(songDetails.getProductName());
                lblUPC.setText(songDetails.getUPC());
                lblShare.setText(percentage);
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
    }

    @FXML
    void onSearchedSongPress2() {

    }

    @FXML
    void contextM() throws SQLException, ClassNotFoundException {
        System.out.println("ControllerSearch.contextM");
        String isrc = searchResultISRC.getText();
        // songDetails = DatabaseMySQL.searchSongDetails(isrc);
        songDetails = DatabasePostgres.searchSongDetails(isrc);
    }

    @FXML
    public void copyISRC() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(songDetails.getISRC());
        boolean status = clipboard.setContent(content);
        System.out.println("status = " + status);
        if (status) {
            btnCopy.setText("Copied");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> btnCopy.setText("Copy")));
            timeline.play();
        }
    }

    @FXML
    void copySinger() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(songDetails.getSinger());
        boolean status = clipboard.setContent(content);
        System.out.println("status = " + status);
        if (status) {
            btnCopy.setText("Copied");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> btnCopy.setText("Copy")));
            timeline.play();
        }
    }

    @FXML
    void copyComposer() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();

        content.putString(songDetails.getComposer());
        boolean status = clipboard.setContent(content);

        System.out.println("status = " + status);

        if (status) {
            btnCopy.setText("Copied");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> btnCopy.setText("Copy")));
            timeline.play();
        }
    }

    @FXML
    void copyLyricist() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(songDetails.getLyricist());
        boolean status = clipboard.setContent(content);
        System.out.println("status = " + status);
        if (status) {
            btnCopy.setText("Copied");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> btnCopy.setText("Copy")));
            timeline.play();
        }
    }

    @FXML
    void copyFeaturing() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(songDetails.getFeaturing());
        boolean status = clipboard.setContent(content);
        System.out.println("status = " + status);
        if (status) {
            btnCopy.setText("Copied");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> btnCopy.setText("Copy")));
            timeline.play();
        }
    }

    @FXML
    void copyUPC() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(songDetails.getUPC());
        boolean status = clipboard.setContent(content);
        System.out.println("status = " + status);
        if (status) {
            btnCopy.setText("Copied");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> btnCopy.setText("Copy")));
            timeline.play();
        }
    }

    @FXML
    void copyProductName() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(songDetails.getProductName());
        boolean status = clipboard.setContent(content);
        System.out.println("status = " + status);
        if (status) {
            btnCopy.setText("Copied");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> btnCopy.setText("Copy")));
            timeline.play();
        }
    }

    public void copySongName() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(songDetails.getTrackTitle());
        boolean status = clipboard.setContent(content);
        System.out.println("status = " + status);
        if (status) {
            btnCopy.setText("Copied");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> btnCopy.setText("Copy")));
            timeline.play();
        }
    }

    public void onSearchOnYoutubeBtnClicked(MouseEvent mouseEvent) {
        String query = searchArea.getText();
        query = query.replace(" ", "+");
        query = "https://www.youtube.com/results?search_query=" + query;
        System.out.println("query = " + query);

        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                URI uri = new URI(query);
                Desktop.getDesktop().browse(uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onSearchOnGoogleBtnClicked(MouseEvent mouseEvent) {
        String query = searchArea.getText();
        query = query.replace(" ", "+");
        query = "https://www.google.com/search?q=" + query + "+lyrics";
        // System.out.println("query = " + query);

        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                URI uri = new URI(query);
                Desktop.getDesktop().browse(uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
