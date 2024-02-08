package com.example.song_finder_fx;

import com.example.song_finder_fx.Model.Search;
import com.example.song_finder_fx.Model.Songs;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyEvent;
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

public class ControllerSearch {
    public HBox hboxSongSearch;
    public VBox vboxSongDetails;
    public HBox hbox2;
    @FXML
    private ImageView btnPlay;

    @FXML
    private ImageView btnPlay1;

    @FXML
    private Label searchResultComposer;

    @FXML
    private Label searchResultISRC;

    @FXML
    private Label searchResultLyricist;

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
    private Label lblFeaturing;

    @FXML
    private Label lblProductName;

    @FXML
    private Label lblShare;

    @FXML
    private Label lblUPC;
    @FXML
    private Button btnCopy;
    private Parent newContent;
    private final Search search = new Search();
    private boolean toggle = false;
    Songs songDetails;

    public ControllerSearch() {

    }

    @FXML
    void btnSetSearchTypeComposer() {
        lblSearchType.setText("Composer");
        search.setType("COMPOSER");
    }

    @FXML
    void btnSetSearchTypeISRC() {
        lblSearchType.setText("ISRC");
        search.setType("ISRC");
    }

    @FXML
    void btnSetSearchTypeLyricist() {
        lblSearchType.setText("Lyricist");
        search.setType("LYRICIST");
    }

    @FXML
    void btnSetSearchTypeName() {
        lblSearchType.setText("Name");
        search.setType("TRACK_TITLE");
    }

    @FXML
    void btnSetSearchTypeSinger() {
        lblSearchType.setText("Singer");
        search.setType("SINGER");
    }

    @FXML
    void getText(KeyEvent event) throws IOException {
        // Getting search keywords
        String text = searchArea.getText();

        scrlpneSong.setVisible(true);
        scrlpneSong.setContent(vboxSong);

        Task<List<Songs>> task = new Task<>() {
            @Override
            protected java.util.List<Songs> call() throws Exception {
                return search.search(text);
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
                    lblSongName.setText(songList.get(i).getTrackTitle());
                    lblISRC.setText(songList.get(i).getISRC().trim());
                    lblArtist.setText(songList.get(i).getSinger().trim());
                    lblComposer.setText(songList.get(i).getComposer().trim());
                    lblLyricist.setText(songList.get(i).getLyricist().trim());
                    vboxSong.getChildren().add(nodes[i]);
                } catch (NullPointerException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Thread thread = new Thread(task);
        thread.start();
    }

    public void testSlide() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("layouts/sidepanel-song-view.fxml"));
        newContent = loader.load();

        mainVBox.getChildren().set(1, newContent);
    }

    public void testClose() {
        Scene scene = newContent.getScene();
        VBox mainVBox = (VBox) scene.lookup("#mainVBox");
        mainVBox.getChildren().remove(1);
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

        List<String> songList = Main.getSongList();
        int songListLength = songList.size();

        if (songListLength > 1) {
            String text = songList.getFirst() + " + " + (songListLength - 1) + " other songs added";
            songListButtonSubtitle.setText(text);
            System.out.println(text);
        } else {
            songListButtonSubtitle.setText(songList.getFirst());
            System.out.println(songList.getFirst());
        }
    }

    @FXML
    void onBtnPlayClicked(MouseEvent mouseEvent) {
        Image img = new Image("com/example/song_finder_fx/images/icon _timer.png");
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

        try {
            isrc = searchResultISRC.getText();
        } catch (Exception e) {
            isrc = searchResultISRC.getText();
        }

        Task<Void> task;
        Path start = Paths.get(Main.selectedDirectory.toURI());
        final boolean[] status = new boolean[1];

        lblPlayerSongName.setText("Loading audio");
        lblPlayerSongName.setStyle("-fx-text-fill: '#000000'");
        imgMediaPico.setImage(img);

        String finalIsrc = isrc;
        task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Clip clip = Main.getClip();
                if (clip != null) {
                    clip.stop();
                }
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
                    songDetails = DatabaseMySQL.searchSongDetails(isrc);
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
        songDetails = DatabaseMySQL.searchSongDetails(isrc);
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
        content.putString(songDetails.getControl());
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
}
