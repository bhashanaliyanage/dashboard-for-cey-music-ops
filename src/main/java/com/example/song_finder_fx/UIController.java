package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.NotificationBuilder;
import com.example.song_finder_fx.Model.Search;
import com.example.song_finder_fx.Model.Songs;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;

import javax.sound.sampled.Clip;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class UIController {
    public VBox sideVBox;
    private final NotificationBuilder nb = new NotificationBuilder();
    public static final Node[] mainNodes = new Node[7];
    private final Search search = new Search();


    //<editor-fold desc="Variables">
    //<editor-fold desc="TextArea">
    public TextArea textArea;
    //</editor-fold>

    //<editor-fold desc="Button">
    public Button btnDestination;
    @FXML
    public Button btnProceed;
    public Button btnOpenLocation;
    public Button btnCopyTo;
    public Button btnAudioDatabase;
    //</editor-fold>

    //<editor-fold desc="ImageView">
    public ImageView btnPercentageChange;
    public ImageView ProgressView;
    public ImageView imgMediaPico;
    public ImageView imgDeleteSong;
    public ImageView imgPlaySong;
    //</editor-fold>

    //<editor-fold desc="HBox">
    public HBox hboxInvoiceSong;
    public HBox btnSeachSongs;
    public HBox btnCollectSongs;
    public HBox btnRevenueAnalysis;
    public HBox searchAndCollect;
    public HBox searchAndCollect1;
    //</editor-fold>

    //<editor-fold desc="VBox">
    public VBox textAreaVbox;
    public VBox mainVBox;
    @FXML
    public VBox vboxSong;
    public VBox btnDatabaseCheck;
    public VBox vBoxInSearchSong;
    //</editor-fold>

    //<editor-fold desc="Label">
    public Label songShare;
    public Label lblDatabaseStatus;
    public Label srchRsArtist;
    public Label lblPlayerSongName;
    public Label lblPlayerSongArtst;
    public Label lblSongListSub;
    public Label srchRsSongName;
    public Label srchRsISRC;
    public Label songProductName;
    public Label songUPC;
    public Label songLyricist;
    public Label songComposer;
    public Label songSinger;
    public Label songISRC;
    public Label songISRCCopied;
    public Label songSingerCopied;
    public Label songComposerCopied;
    public Label songLyricistCopied;
    public Label songUPCCopied;
    public Label songAlbumNameCopied;
    public Label songName;
    public Label lblSearchType;
    public Label searchResultISRC;
    public Label songFeaturing;
    public Label songFeaturingCopied;
    //</editor-fold>

    //<editor-fold desc="ScrollPane">
    public ScrollPane scrlpneSong;
    //</editor-fold>

    //<editor-fold desc="TextField">
    public TextField searchArea;
    //</editor-fold>

    //<editor-fold desc="Rectangle">
    static File directory;
    public BorderPane borderpane;
    public Rectangle rctIngests;
    File destination;
    public Rectangle rctManualClaims;
    public Rectangle rctSearchSongs;
    public Rectangle rctCollectSongs;
    public Rectangle rctRevenue;
    public Rectangle rctArtistReports;

    //</editor-fold>
    //</editor-fold>
    public UIController() throws IOException {
    }

    public static void setAllScenes() throws IOException {
        // About
        mainNodes[1] = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/about.fxml")));
        // Search
        mainNodes[2] = FXMLLoader.load(Objects.requireNonNull(UIController.class.getResource("layouts/search-details.fxml")));
        // Search and collect songs
        mainNodes[3] = FXMLLoader.load(Objects.requireNonNull(UIController.class.getResource("layouts/collect-songs.fxml")));
        // NavBar
        mainNodes[4] = FXMLLoader.load(Objects.requireNonNull(UIController.class.getResource("layouts/navigationbar.fxml")));
        // NavBar Collapsed
        mainNodes[5] = FXMLLoader.load(Objects.requireNonNull(UIController.class.getResource("layouts/navigationbar-small.fxml")));
        // Edit
    }

    public void backButtonImplementationForSearchSong(MouseEvent event) {
        Node node = (Node) event.getSource();
        Scene scene = node.getScene();
        VBox mainVBox = (VBox) scene.lookup("#mainVBox");

        mainVBox.getChildren().clear();
        mainVBox.getChildren().add(mainNodes[2]);
    }

    public void onAddToListButtonClicked(ActionEvent actionEvent) {
        // Getting scene
        Node node = (Node) actionEvent.getSource();
        Scene scene = node.getScene();

        // Getting label from scene
        Label isrc = (Label) scene.lookup("#songISRC");
        Label songListButtonSubtitle = (Label) scene.lookup("#lblSongListSub");

        // Adding songs to list
        Main.addSongToList(isrc.getText());

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

    public void onAddToListButtonClickedInSearchSong(MouseEvent mouseEvent) {
        // Getting scene
        Node node = (Node) mouseEvent.getSource();
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

    public void onOpenFileLocationButtonClicked(MouseEvent mouseEvent) {
        boolean directoryStatus = Main.directoryCheckNew();

        if (!directoryStatus) {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Audio Database Location");
            Main.selectedDirectory = chooser.showDialog(btnOpenLocation.getScene().getWindow());
        }

        Node node = (Node) mouseEvent.getSource();
        Scene scene = node.getScene();
        Label lblISRC = (Label) scene.lookup("#songISRC");

        String isrc = lblISRC.getText();

        if (Main.selectedDirectory.exists()) { // check if the directory exists
            if (Main.selectedDirectory.isDirectory()) { // check if the directory is a directory
                if (Main.selectedDirectory.canWrite()) { // check if the directory is writable
                    System.out.println("The directory is accessible and writable.");
                    Path start = Paths.get(Main.selectedDirectory.toURI());
                    Thread t = threadToOpenFileLocation(start, isrc, scene);
                    t.start();

                    Platform.runLater(() -> {
                        Button btnOpenFileLocation = (Button) scene.lookup("#btnOpenLocation");
                        btnOpenFileLocation.setDisable(true);
                        btnOpenFileLocation.setText("Searching...");
                    });
                }
            }
        } else {
            System.out.println("The directory does not exist.");
            Button btnOpenFileLocation = (Button) scene.lookup("#btnOpenLocation");
            btnOpenFileLocation.setText("Error in selected audio database directory");
            btnOpenFileLocation.setStyle("-fx-text-fill: '#F4442E'");
        }
    }

    private static Thread threadToOpenFileLocation(Path start, String isrc, Scene scene) {
        final Path[] file = new Path[1];
        Task<Void> task;
        task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try (Stream<Path> stream = Files.walk(start)) {
                    // Get file name to search for location from database
                    // String fileName = DatabaseMySQL.searchFileName(isrc);
                    String fileName = DatabasePostgres.searchFileName(isrc);
                    // Copy the code from SearchSongsFromDB method in DatabaseMySQL.java
                    file[0] = stream
                            .filter(path -> path.toFile().isFile())
                            .filter(path -> path.getFileName().toString().equals(fileName))
                            .findFirst()
                            .orElse(null);

                    if (file[0] != null) {
                        Path filePath = file[0].getParent();
                        Desktop.getDesktop().open(filePath.toFile());
                        System.out.println(filePath);

                        Platform.runLater(() -> {
                            Button btnOpenFileLocation = (Button) scene.lookup("#btnOpenLocation");
                            btnOpenFileLocation.setText("Open File Location");
                            btnOpenFileLocation.setDisable(false);
                        });
                    } else {
                        Platform.runLater(() -> {
                            Button btnOpenFileLocation = (Button) scene.lookup("#btnOpenLocation");
                            btnOpenFileLocation.setText("File not found on audio database");
                            btnOpenFileLocation.setStyle("-fx-text-fill: '#F4442E'");
                        });
                        System.out.println("File not found on audio database");
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return null;
            }
        };

        return new Thread(task);
    }

    public void onCopyToButtonClicked(MouseEvent mouseEvent) {
        boolean directoryStatus = Main.directoryCheckNew();
        DirectoryChooser chooser = new DirectoryChooser();

        if (!directoryStatus) {
            chooser.setTitle("Audio Database Location");
            Main.selectedDirectory = chooser.showDialog(btnCopyTo.getScene().getWindow());
        }

        Node node = (Node) mouseEvent.getSource();
        Scene scene = node.getScene();
        Label lblISRC = (Label) scene.lookup("#songISRC");
        String isrc = lblISRC.getText();

        Path start = Paths.get(Main.selectedDirectory.toURI());
        chooser.setTitle("Select a directory");
        destination = chooser.showDialog(btnCopyTo.getScene().getWindow());

        if (destination != null) {
            if (Main.selectedDirectory.exists()) { // check if the directory exists
                if (Main.selectedDirectory.isDirectory()) { // check if the directory is a directory
                    if (Main.selectedDirectory.canWrite()) { // check if the directory is writable
                        System.out.println("The directory is accessible and writable.");
                        Task<Void> task = threadToCopyFile(start, isrc, scene);

                        Thread t = new Thread(task);
                        t.start();

                        Platform.runLater(() -> {
                            Button btnCopyTo = (Button) scene.lookup("#btnCopyTo");
                            btnCopyTo.setDisable(true);
                            btnCopyTo.setText("Searching");
                        });
                    } else {
                        System.out.println("The directory is accessible but not writable.");
                        Button btnCopyTo = (Button) scene.lookup("#btnCopyTo");
                        btnCopyTo.setText("Cannot copy to selected destination");
                        btnCopyTo.setStyle("-fx-text-fill: '#F4442E'");
                    }
                }
            }
        }

    }

    private Task<Void> threadToCopyFile(Path start, String isrc, Scene scene) {
        Task<Void> task;
        task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try (Stream<Path> stream = Files.walk(start)) {
                    // String fileName = DatabaseMySQL.searchFileName(isrc);
                    String fileName = DatabasePostgres.searchFileName(isrc);
                    Path file = stream
                            .filter(path -> path.toFile().isFile())
                            .filter(path -> path.getFileName().toString().equals(fileName))
                            .findFirst()
                            .orElse(null);

                    if (file != null) {
                        System.out.println("Executing file: " + file.getFileName());
                        Path targetDir = destination.toPath();
                        Path targetFile = targetDir.resolve(fileName);
                        Platform.runLater(() -> {
                            Button btnCopyTo = (Button) scene.lookup("#btnCopyTo");
                            btnCopyTo.setText("Copying...");
                        });
                        Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                        Platform.runLater(() -> {
                            Button btnCopyTo = (Button) scene.lookup("#btnCopyTo");
                            btnCopyTo.setDisable(false);
                            btnCopyTo.setText("Copy to");
                        });
                        System.out.println("File copied to: " + targetFile);
                    } else {
                        Platform.runLater(() -> {
                            Button btnCopyTo = (Button) scene.lookup("#btnCopyTo");
                            btnCopyTo.setText("File not found on audio database");
                            btnCopyTo.setStyle("-fx-text-fill: '#F4442E'");
                        });
                        System.out.println("File not found on audio database");
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                return null;
            }
        };
        return task;
    }

    public void onBtnPlayClicked(MouseEvent mouseEvent) {
        Image img = new Image("com/example/song_finder_fx/images/icon _timer.png");

        Main.directoryCheck();
        Songs song = new Songs();

        Node node = (Node) mouseEvent.getSource();
        Scene scene = node.getScene();

        Label lblISRC = (Label) scene.lookup("#songISRC");
        Label lblSongName = (Label) scene.lookup("#songName");
        Label lblArtist = (Label) scene.lookup("#songSinger");
        Label lblPlayerSongName = (Label) scene.lookup("#lblPlayerSongName");
        Label lblPlayerArtist = (Label) scene.lookup("#lblPlayerSongArtst");
        ImageView imgMediaPico = (ImageView) scene.lookup("#imgMediaPico");

        song.setTrackTitle(lblSongName.getText());
        song.setSinger(lblArtist.getText());

        String isrc;
        try {
            isrc = lblISRC.getText();
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
    protected void onSearchDetailsButtonClick() throws ClassNotFoundException, IOException {
        changeSelectorTo(rctSearchSongs);
        Connection con = checkDatabaseConnection();

        if (con != null) {
            FXMLLoader sidepanelLoader = new FXMLLoader(getClass().getResource("layouts/sidepanel-recent-songs.fxml"));
            Parent sidepanelNewContent = sidepanelLoader.load();
            mainVBox.getChildren().clear();
            mainVBox.getChildren().add(mainNodes[2]);
            sideVBox.getChildren().clear();
            sideVBox.getChildren().add(sidepanelNewContent);
        } else {
            UIController.showErrorDialog("Database Connection Error!", "Error Connecting to Database", "Please check your XAMPP server up and running");
        }
    }

    private void changeSelectorTo(Rectangle selector) {
        rctSearchSongs.setVisible(false);
        rctCollectSongs.setVisible(false);
        rctRevenue.setVisible(false);
        rctArtistReports.setVisible(false);
        rctIngests.setVisible(false);
        rctManualClaims.setVisible(false);

        selector.setVisible(true);
    }

    public void onSearchedSongClick(MouseEvent mouseEvent) throws SQLException, ClassNotFoundException, IOException {
        String isrc = searchResultISRC.getText();

        DatabaseMySQL db = new DatabaseMySQL();
        Songs songDetails = db.searchSongDetails(isrc);

        String percentage;
        if (songDetails.composerAndLyricistCeyMusic()) {
            percentage = "100%";
        } else if (songDetails.composerOrLyricistCeyMusic()) {
            percentage = "50%";
        } else {
            percentage = "0%";
        }

        // Getting the current parent layout
        FXMLLoader loader = new FXMLLoader(getClass().getResource("layouts/song-view.fxml"));
        Parent newContent = loader.load();
        Node node = (Node) mouseEvent.getSource();
        Scene scene = node.getScene();
        VBox mainVBox = (VBox) scene.lookup("#mainVBox");
        mainVBox.getChildren().clear();


        // Setting the new layout
        mainVBox.getChildren().add(newContent);

        // Setting values for labels
        Label songNameViewTitle = (Label) scene.lookup("#songNameViewTitle");
        Label songName = (Label) scene.lookup("#songName");
        Label songISRC = (Label) scene.lookup("#songISRC");
        Label songSinger = (Label) scene.lookup("#songSinger");
        Label songFeaturing = (Label) scene.lookup("#songFeaturing");
        Label songComposer = (Label) scene.lookup("#songComposer");
        Label songLyricist = (Label) scene.lookup("#songLyricist");
        Label songUPC = (Label) scene.lookup("#songUPC");
        Label songProductName = (Label) scene.lookup("#songProductName");
        Label songShare = (Label) scene.lookup("#songShare");
        songISRC.setText(songDetails.getISRC());
        songProductName.setText(songDetails.getProductName());
        songUPC.setText(songDetails.getUPC());
        songName.setText(songDetails.getTrackTitle());
        songNameViewTitle.setText(songDetails.getTrackTitle());
        songSinger.setText(songDetails.getSinger());
        songFeaturing.setText(songDetails.getFeaturing());
        songComposer.setText(songDetails.getComposer());
        songLyricist.setText(songDetails.getLyricist());
        songShare.setText(percentage);
    }

    public void getText() throws IOException {
        // Getting search keywords
        String text = searchArea.getText();

        // Connecting to database
        FXMLLoader loader = new FXMLLoader(getClass().getResource("layouts/song-view.fxml"));
        loader.load();

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
//                    lblSongName.setText("test art");
                    lblISRC.setText(songList.get(i).getISRC().trim());

                    lblArtist.setText(songList.get(i).getSinger().trim());
//                        lblArtist.setText("test name");
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

    public void copyISRC() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(songISRC.getText());
        boolean status = clipboard.setContent(content);
        if (status) {
            songISRCCopied.setVisible(true);
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> songISRCCopied.setVisible(false)));
            timeline.play();
        }
    }

    public void copySinger() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(songSinger.getText());
        boolean status = clipboard.setContent(content);
        if (status) {
            songSingerCopied.setVisible(true);
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> songSingerCopied.setVisible(false)));
            timeline.play();
        }
    }

    public void copyComposer() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(songComposer.getText());
        boolean status = clipboard.setContent(content);
        if (status) {
            songComposerCopied.setVisible(true);
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> songComposerCopied.setVisible(false)));
            timeline.play();
        }
    }

    public void copyLyricist() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(songLyricist.getText());
        boolean status = clipboard.setContent(content);
        if (status) {
            songLyricistCopied.setVisible(true);
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> songLyricistCopied.setVisible(false)));
            timeline.play();
        }
    }

    public void copyFeaturing() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(songFeaturing.getText());
        boolean status = clipboard.setContent(content);
        if (status) {
            songFeaturingCopied.setVisible(true);
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> songFeaturingCopied.setVisible(false)));
            timeline.play();
        }
    }

    public void copyUPC() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(songUPC.getText());
        boolean status = clipboard.setContent(content);
        if (status) {
            songUPCCopied.setVisible(true);
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> songUPCCopied.setVisible(false)));
            timeline.play();
        }
    }

    public void copyProductName() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(songProductName.getText());
        boolean status = clipboard.setContent(content);
        if (status) {
            songAlbumNameCopied.setVisible(true);
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> songAlbumNameCopied.setVisible(false)));
            timeline.play();
        }
    }
    //</editor-fold>

    //<editor-fold desc="Database Things">
    public void onDatabaseConnectionBtnClick() throws ClassNotFoundException, AWTException {
        // TODO: Do this on a separate thread
        Connection con = checkDatabaseConnection();
        if (con != null) {
            nb.displayTrayInfo("Database Connected", "Database Connection Success");
        } else {
            NotificationBuilder.displayTrayError("Error", "Error connecting database");
        }
    }

    Connection checkDatabaseConnection() throws ClassNotFoundException {
        lblDatabaseStatus.setText("Connecting");

        Connection con = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://192.168.1.200/songData";
            String username = "ceymusic";
            String password = "ceymusic";
            con = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            lblDatabaseStatus.setText("Offline");
            lblDatabaseStatus.setStyle("-fx-text-fill: '#931621'");
            return con;
        }

        if (con != null) {
            lblDatabaseStatus.setText("Online");
            lblDatabaseStatus.setStyle("-fx-text-fill: '#00864E'");
        }

        return con;
    }
    //</editor-fold>

    //<editor-fold desc="Song List">
    public void onSongListButtonClicked() throws ClassNotFoundException {
        ControllerSongList controllerSongList = new ControllerSongList(this);
        controllerSongList.loadThings();
        // songListTestButton();
    }

    public void onDeleteSongClicked(MouseEvent event) {
        String isrc = srchRsISRC.getText();
        System.out.println(isrc);

        boolean status = Main.deleteSongFromList(isrc);

        if (status) {
            srchRsISRC.setText("-");
            srchRsISRC.setDisable(true);
            srchRsArtist.setText("-");
            srchRsArtist.setDisable(true);
            srchRsSongName.setText(isrc + " Removed from the list");
            srchRsSongName.setDisable(true);
            imgDeleteSong.setVisible(false);
            imgPlaySong.setVisible(false);

            Node node = (Node) event.getSource();
            Scene scene = node.getScene();

            List<String> songList = Main.getSongList();
            int listSize = songList.size();
            Label lblListCount = (Label) scene.lookup("#lblListCount");
            Label songListButtonSubtitle = (Label) scene.lookup("#lblSongListSub");
            lblListCount.setText("Total: " + listSize);

            try {
                if (listSize > 1) {
                    String text = songList.getFirst() + " + " + (listSize - 1) + " other songs added";
                    songListButtonSubtitle.setText(text);
                    System.out.println(text);
                } else {
                    songListButtonSubtitle.setText(songList.getFirst());
//                    System.out.println(songList.getFirst());
                }
            } catch (Exception e) {
                songListButtonSubtitle.setText("Click Here to Add Songs");
            }
        }
    }

    public void onPlaySongClicked(MouseEvent mouseEvent) {
        System.out.println("Test");
        Image img = new Image("com/example/song_finder_fx/images/icon _timer.png");

        Main.directoryCheck();

        Node node = (Node) mouseEvent.getSource();
        Scene scene = node.getScene();

        Label lblPlayerSongName = (Label) scene.lookup("#lblPlayerSongName");
        Label lblPlayerArtist = (Label) scene.lookup("#lblPlayerSongArtst");
        ImageView imgMediaPico = (ImageView) scene.lookup("#imgMediaPico");

        String isrc = srchRsISRC.getText();

        Platform.runLater(() -> {
            System.out.println("Song Name: " + srchRsSongName.getText());
            System.out.println("ISRC: " + isrc);
            System.out.println("Artist: " + srchRsArtist.getText());
        });

        Task<Void> task;
        Path start = Paths.get(Main.selectedDirectory.toURI());
        final boolean[] status = new boolean[1];

        lblPlayerSongName.setText("Loading audio");
        lblPlayerSongName.setStyle("-fx-text-fill: '#000000'");
        imgMediaPico.setImage(img);

        task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Clip clip = Main.getClip();
                if (clip != null) {
                    clip.stop();
                }
                status[0] = Main.playAudio(start, isrc);
                return null;
            }
        };

        Songs song = new Songs();

        task.setOnSucceeded(event -> UIController.setPlayerInfo(status, lblPlayerSongName, lblPlayerArtist, imgMediaPico, song));

        new Thread(task).start();
    }
    //</editor-fold>

    //<editor-fold desc="Music Player Stuff">
    public static void setPlayerInfo(boolean[] status, Label lblPlayerSongName, Label lblPlayerArtist, ImageView imgMediaPico, Songs sng) {
        Image pauseImg = new Image("com/example/song_finder_fx/images/icon _pause circle.png");
        Image imgPlay = new Image("com/example/song_finder_fx/images/icon _play circle_.png");

        if (status[0]) {
            imgMediaPico.setImage(pauseImg);
            lblPlayerSongName.setStyle("-fx-text-fill: '#000000'");
            lblPlayerSongName.setText(sng.getTrackTitle());
            lblPlayerArtist.setText(sng.getSinger());
            // Platform.runLater(() -> System.out.println("lblSongName = " + lblSongName.getText()));
        } else {
            imgMediaPico.setImage(imgPlay);
            lblPlayerSongName.setStyle("-fx-text-fill: '#000000'");
            lblPlayerSongName.setText("N/A");
        }
    }

    public void onMusicPlayerBtnClick(MouseEvent mouseEvent) {
        Clip clip = Main.getClip();
        Node node = (Node) mouseEvent.getSource();
        Scene scene = node.getScene();
        ImageView imgMediaPico = (ImageView) scene.lookup("#imgMediaPico");
        Image imgPlay = new Image("com/example/song_finder_fx/images/icon _play circle_.png");
        Image imgPause = new Image("com/example/song_finder_fx/images/icon _pause circle.png");

        if (clip != null) {
            if (clip.isRunning()) {
                // System.out.println("Clip is running");
                clip.stop();
                imgMediaPico.setImage(imgPlay);
            } else {
                clip.start();
                imgMediaPico.setImage(imgPause);
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="Settings">
    public void onSettingsButtonClicked() throws IOException, SQLException, ClassNotFoundException {
        ControllerSettings cs = new ControllerSettings(this);
        cs.loadThings();
    }
    //</editor-fold>

    //<editor-fold desc="Collect Songs">
    private void updateButtonProceed(String isrcCode) {
        if (btnProceed != null) {
            btnProceed.setText(isrcCode);
        } else {
            System.out.println("Proceed button variable is null");
        }
    }

    public void onCollectSongsButtonClick(MouseEvent event) throws ClassNotFoundException, SQLException {
        changeSelectorTo(rctCollectSongs);
        checkDatabaseConnection();

        mainVBox.getChildren().clear();
        mainVBox.getChildren().add(mainNodes[3]);

        String directoryString = Main.getAudioDatabaseLocation();
        Node node = (Node) event.getSource();
        Scene scene = node.getScene();
        Button btnAudioDatabase = (Button) scene.lookup("#btnAudioDatabase");
        btnAudioDatabase.setText("   Audio Database: " + directoryString);
    }

    @FXML
    protected void onBrowseAudioButtonClick() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select audio database directory");
        directory = chooser.showDialog(btnAudioDatabase.getScene().getWindow());

        if (directory != null) {
            String shortenedString = directory.getAbsolutePath().substring(0, Math.min(directory.getAbsolutePath().length(), 73));
            btnAudioDatabase.setText("   Database: " + shortenedString + "...");
        } else {
            System.out.println("No directory selected");
        }
    }

    public void onBrowseDestinationButtonClick() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select your destination");
        destination = chooser.showDialog(btnAudioDatabase.getScene().getWindow());

        if (destination != null) {
            String shortenedString = destination.getAbsolutePath().substring(0, Math.min(destination.getAbsolutePath().length(), 73));
            btnDestination.setText("   Destination: " + shortenedString + "...");
        } else {
            System.out.println("No destination selected");
        }
    }

    public void onProceedButtonClick() throws ClassNotFoundException, SQLException {
        Connection con = DatabaseMySQL.getConn();
        directory = Main.getSelectedDirectory();

        if (con != null) {
            // Connection Checked
            searchAndCollect1.setStyle("-fx-background-color: #eeefee; -fx-border-color: '#c0c1c2';");
            ProgressView.setVisible(true);
            btnAudioDatabase.setDisable(true);
            btnDestination.setDisable(true);
            textArea.setDisable(true);
            btnProceed.setDisable(true);
            textAreaVbox.setDisable(true);
            Task<Void> task = null;
            btnProceed.setText("Processing");
            System.out.println("Directory: " + directory.getAbsolutePath());

            if (directory == null || destination == null) {
                // Directories Checked
                showErrorDialog("Empty Location Entry", "Please browse for Audio Database and Destination Location", "Use the location section for this");
                btnProceed.setText("Proceed");
                searchAndCollect1.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: '#e9ebee';");
                ProgressView.setVisible(false);
                btnAudioDatabase.setDisable(false);
                btnDestination.setDisable(false);
                textArea.setDisable(false);
                btnProceed.setDisable(false);
                textAreaVbox.setDisable(false);
            } else {
                // If directories available
                String text = textArea.getText();
                System.out.println(text);
                if (!text.isEmpty()) {
                    // If text area is not empty
                    task = new Task<>() {
                        @Override
                        protected Void call() throws Exception {
                            String[] isrcCodes = text.split("\\n");

                            int length = isrcCodes.length;

                            // Looping through ISRCs
                            for (int i = 0; i < length; i++) {
                                String isrc = isrcCodes[i];
                                if (isrc.length() != 12) {
                                    // If there are any wrong ISRCs
                                    showErrorDialog("Invalid ISRC Code", "Invalid or empty ISRC Code", isrc);
                                } else {
                                    // If ISRC number is correct
                                    int finalI = i;
                                    Platform.runLater(() -> updateButtonProceed("Processing " + (finalI + 1) + " of " + length));
                                    Main.copyAudio(isrc, directory, destination);
                                }
                            }
                            return null;
                        }
                    };
                } else {
                    // If text area is empty
                    showErrorDialog("Invalid ISRC Code", "Empty ISRC Code", "Please enter ISRC codes in the text area");
                    btnProceed.setText("Proceed");
                    searchAndCollect1.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: '#e9ebee';");
                    ProgressView.setVisible(false);
                    btnAudioDatabase.setDisable(false);
                    btnDestination.setDisable(false);
                    textArea.setDisable(false);
                    btnProceed.setDisable(false);
                    textAreaVbox.setDisable(false);
                }
            }

            if (task != null) {
                task.setOnSucceeded(e -> {
                    // After task is succeeded
                    btnProceed.setText("Proceed");
                    searchAndCollect1.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: '#e9ebee';");
                    ProgressView.setVisible(false);
                    btnAudioDatabase.setDisable(false);
                    btnDestination.setDisable(false);
                    textArea.setDisable(false);
                    btnProceed.setDisable(false);
                    textAreaVbox.setDisable(false);

                    // If there are any missing files
                    if (!DatabaseMySQL.errorBuffer.isEmpty()) {
                        try {
                            showErrorDialogWithLog("File Not Found Error", "Error! Some files are missing in your audio database", DatabaseMySQL.errorBuffer.toString());
                        } catch (IOException exception) {
                            throw new RuntimeException(exception);
                        }
                    }

                    // Send a notification when task is completed
                    NotificationBuilder nb = new NotificationBuilder();

                    try {
                        nb.displayTrayInfo("Execution Completed", "Please check your destination folder for the copied audio files");
                    } catch (AWTException exception) {
                        throw new RuntimeException(exception);
                    }
                });
            }

            // Start thread
            new Thread(task).start();
        } else {
            // If database not working
            showErrorDialog("Database Connection Error!", "Error Connecting to Database", "Please check your XAMPP server up and running");
        }
    }

    public void onOpenDestinationButtonClick() throws IOException {
        if (destination != null) {
            Path path = destination.toPath();
            Desktop.getDesktop().open(path.toFile());
        }
    }
    //</editor-fold>

    //<editor-fold desc="Error Dialogs">
    static void showErrorDialog(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
    }

    private static void showErrorDialogWithLog(String title, String headerText, String contentText) throws IOException {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText + "\nPress ok to save log, or click close");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                System.out.println("Ok button");
                String fileName = "error_log.txt";
                File destination = Main.browseLocation();
                File file = new File(destination, fileName);

                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(contentText);
                } catch (IOException e) {
                    System.err.println("An error occurred: " + e.getMessage());
                }
            }
        }
    }
    //</editor-fold>

    public void onAboutButtonClicked() throws IOException {
        ControllerSettings cs = new ControllerSettings(this);
        cs.loadAbout(mainNodes[1]);
    }

    public void onRevenueAnalysisBtnClick() throws IOException {
        changeSelectorTo(rctRevenue);
        ControllerRevenueGenerator revenueGenerator = new ControllerRevenueGenerator(this);
        revenueGenerator.loadRevenueGenerator();
    }

    //<editor-fold desc="Invoice">
    public void onPercentageChangeButtonClicked() throws SQLException, ClassNotFoundException {
        String song_share = songShare.getText();
        String song_name = songName.getText();
        Image plusImg = new Image("com/example/song_finder_fx/images/icon_plus.png");
        Image minusImg = new Image("com/example/song_finder_fx/images/icon_minus.png");

        if (Objects.equals(song_share, "100%")) {
            boolean status = Database.changePercentage(song_name, "50%");
            if (status) {
                songShare.setText("50%");
                btnPercentageChange.setImage(plusImg);
            }
        } else {
            boolean status = Database.changePercentage(song_name, "100%");
            if (status) {
                songShare.setText("100%");
                btnPercentageChange.setImage(minusImg);
            }
        }
    }

    public void onDeleteBtnClicked() {
        System.out.println("UIController.onDeleteBtnClicked");
    }
    //</editor-fold>

    public void onArtistReportsBtnClick() throws IOException {
        changeSelectorTo(rctArtistReports);
        ControllerRevenueGenerator revenueGenerator = new ControllerRevenueGenerator(this);
        revenueGenerator.loadArtistReports();
    }

    public void hideLeft() {
        borderpane.setLeft(null);
    }

    public void onIngestsBtnClick() throws IOException {
        changeSelectorTo(rctIngests);

        Node node = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/ingests-chooser.fxml")));
        mainVBox.getChildren().setAll(node);
    }

    public void onManualClaimsBtnClick(MouseEvent mouseEvent) throws IOException {
        changeSelectorTo(rctManualClaims);

        Node node = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/manual_claims/manual-claims-main.fxml")));
        mainVBox.getChildren().setAll(node);
    }
}