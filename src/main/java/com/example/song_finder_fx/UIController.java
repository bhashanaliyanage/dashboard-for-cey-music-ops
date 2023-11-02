package com.example.song_finder_fx;

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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.sound.sampled.Clip;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class UIController {
    public TextArea textArea;
    public Button btnDestination;
    @FXML
    public Button btnProceed;
    public ImageView ProgressView;
    public HBox searchAndCollect;
    public HBox searchAndCollect1;
    public VBox textAreaVbox;
    public VBox mainVBox;
    @FXML
    public VBox vboxSong;
    public VBox btnDatabaseCheck;
    public Label lblDatabaseStatus;
    static File directory;
    public TextField searchArea;
    public ScrollPane scrlpneSong;
    public Label srchRsArtist;
    public Label lblPlayerSongName;
    public ImageView imgMediaPico;
    public Label lblPlayerSongArtst;
    public Label lblSongListSub;
    public Label srchRsSongName;
    public Label srchRsISRC;
    public ImageView imgDeleteSong;
    public ImageView imgPlaySong;
    File destination;
    public Button btnAudioDatabase;
    public Label searchResultSongName;
    public Label searchResultISRC;
    public VBox vBoxInSearchSong;
    private final NotificationBuilder nb = new NotificationBuilder();

    //<editor-fold desc="Search">
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
            String text = songList.get(0) + " + " + (songListLength - 1) + " other songs added";
            songListButtonSubtitle.setText(text);
            System.out.println(text);
        } else {
            songListButtonSubtitle.setText(songList.get(0));
            System.out.println(songList.get(0));
        }
    }

    public void onOpenFileLocationButtonClicked(MouseEvent mouseEvent) throws IOException {
        Main.directoryCheck();

        Node node = (Node) mouseEvent.getSource();
        Scene scene = node.getScene();
        Label lblISRC = (Label) scene.lookup("#songISRC");

        String isrc = lblISRC.getText();

        Path start = Paths.get(Main.selectedDirectory.toURI());
        try (Stream<Path> stream = Files.walk(start)) {
            // Get file name to search for location from database
            String fileName = DatabaseMySQL.searchFileName(isrc);
            // Copy the code from SearchSongsFromDB method in DatabaseMySQL.java
            Path file = stream
                    .filter(path -> path.toFile().isFile())
                    .filter(path -> path.getFileName().toString().equals(fileName))
                    .findFirst()
                    .orElse(null);

            if (file != null) {
                Path filePath = file.getParent();
                Desktop.getDesktop().open(filePath.toFile());
                System.out.println(filePath);
            } else {
                Button btnOpenFileLocation = (Button) scene.lookup("#btnOpenLocation");
                btnOpenFileLocation.setText("File not found on audio database");
                btnOpenFileLocation.setStyle("-fx-text-fill: '#F4442E'");
                System.out.println("File not found on audio database");
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void onCopyToButtonClicked(MouseEvent mouseEvent) throws IOException {
        Main.directoryCheck();

        Node node = (Node) mouseEvent.getSource();
        Scene scene = node.getScene();
        Label lblISRC = (Label) scene.lookup("#songISRC");
        String isrc = lblISRC.getText();

        Path start = Paths.get(Main.selectedDirectory.toURI());
        destination = Main.browseDestination();

        try (Stream<Path> stream = Files.walk(start)) {
            String fileName = DatabaseMySQL.searchFileName(isrc);
            Path file = stream
                    .filter(path -> path.toFile().isFile())
                    .filter(path -> path.getFileName().toString().equals(fileName))
                    .findFirst()
                    .orElse(null);

            if (file != null) {
                System.out.println("Executing file: " + file.getFileName());
                Path targetDir = destination.toPath();
                Path targetFile = targetDir.resolve(fileName);
                Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("File copied to: " + targetFile);
            } else {
                Button btnCopyTo = (Button) scene.lookup("#btnCopyTo");
                btnCopyTo.setText("File not found on audio database");
                btnCopyTo.setStyle("-fx-text-fill: '#F4442E'");
                System.out.println("File not found on audio database");
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void onBtnPlayClicked(MouseEvent mouseEvent) {
        Image img = new Image("com/example/song_finder_fx/images/icon _timer.png");

        Main.directoryCheck();

        Node node = (Node) mouseEvent.getSource();
        Scene scene = node.getScene();

        Label lblISRC = (Label) scene.lookup("#songISRC");
        Label lblSongName = (Label) scene.lookup("#songName");
        Label lblArtist = (Label) scene.lookup("#songSinger");
        Label lblPlayerSongName = (Label) scene.lookup("#lblPlayerSongName");
        Label lblPlayerArtist = (Label) scene.lookup("#lblPlayerSongArtst");
        ImageView imgMediaPico = (ImageView) scene.lookup("#imgMediaPico");

        String isrc = lblISRC.getText();

        Task<Void> task;
        Path start = Paths.get(Main.selectedDirectory.toURI());
        final boolean[] status = new boolean[1];

        System.out.println(isrc);

        lblPlayerSongName.setText("Loading audio");
        lblPlayerSongName.setStyle("-fx-text-fill: '#000000'");
        imgMediaPico.setImage(img);

        task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Clip clip = Main.getClip();
                if (clip != null) {
                    clip.stop();
                    status[0] = Main.playAudio(start, isrc);
                } else {
                    status[0] = Main.playAudio(start, isrc);
                }
                return null;
            }
        };

        task.setOnSucceeded(event -> setPlayerInfo(status, lblPlayerSongName, lblSongName, lblPlayerArtist, lblArtist, imgMediaPico));

        new Thread(task).start();
    }

    @FXML
    protected void onSearchDetailsButtonClick() throws ClassNotFoundException {
        /*ControllerSearch controllerSearch = new ControllerSearch(this);
        controllerSearch.loadThingsTempForSongName();*/
        Connection con = checkDatabaseConnection();

        if (con != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("layouts/search-details.fxml"));
                // loader.setController(this);
                Parent newContent = loader.load();
                mainVBox.getChildren().clear();
                mainVBox.getChildren().add(newContent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("onSearchDetailsButtonClick");
        } else {
            UIController.showErrorDialog("Database Connection Error!", "Error Connecting to Database", "Please check your XAMPP server up and running");
        }
    }

    public void onSearchByISRCButtonClick() throws ClassNotFoundException {
        ControllerSearch controllerSearch = new ControllerSearch(this);
        controllerSearch.loadThingsTempForISRC();
    }

    public void onSearchedSongClick(MouseEvent mouseEvent) throws SQLException, ClassNotFoundException, IOException {
        String name = searchResultSongName.getText();
        String isrc = searchResultISRC.getText();

        DatabaseMySQL db = new DatabaseMySQL();
        List<String> songDetails = db.searchSongDetails(isrc);

        // System.out.println(songDetails.size());

        // For reference
        System.out.println("Song name: " + name);
        System.out.println("ISRC: " + isrc);

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
        Label songComposer = (Label) scene.lookup("#songComposer");
        Label songLyricist = (Label) scene.lookup("#songLyricist");
        Label songUPC = (Label) scene.lookup("#songUPC");
        Label songProductName = (Label) scene.lookup("#songProductName");
        Label songShare = (Label) scene.lookup("#songShare");
        songISRC.setText(songDetails.get(0));
        songProductName.setText(songDetails.get(1));
        songUPC.setText(songDetails.get(2));
        songName.setText(songDetails.get(3));
        songNameViewTitle.setText(songDetails.get(3));
        songSinger.setText(songDetails.get(4));
        songComposer.setText(songDetails.get(6));
        songLyricist.setText(songDetails.get(7));
        songShare.setText("No Detail");
    }

    public void getText() throws IOException {
        // Getting search keywords
        String text = searchArea.getText();

        // Connecting to database
        DatabaseMySQL db = new DatabaseMySQL();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("layouts/song-view.fxml"));
        loader.load();

        scrlpneSong.setVisible(true);
        scrlpneSong.setContent(vboxSong);

        Task<List<Songs>> task = new Task<>() {
            @Override
            protected java.util.List<Songs> call() throws Exception {
                return db.searchSongDetailsBySongName(text);
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
                    Label lblSongName = (Label) nodes[i].lookup("#searchResultSongName");
                    Label lblISRC = (Label) nodes[i].lookup("#searchResultISRC");
                    Label lblArtist = (Label) nodes[i].lookup("#searchResultArtist");
                    lblSongName.setText(songList.get(i).getSongName());
                    lblISRC.setText(songList.get(i).getISRC().trim());
                    lblArtist.setText(songList.get(i).getSinger().trim());
                    vboxSong.getChildren().add(nodes[i]);
                } catch (NullPointerException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Thread thread = new Thread(task);
        thread.start();
    }
    //</editor-fold>

    //<editor-fold desc="Database Things">
    public void onDatabaseConnectionBtnClick() throws ClassNotFoundException, AWTException {
        // TODO: Do this on a separate thread
        Connection con = checkDatabaseConnection();
        if (con != null) {
            nb.displayTrayInfo("Database Connected", "Database Connection Success");
        } else {
            nb.displayTrayError("Error", "Error connecting database");
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
            lblDatabaseStatus.setText("Database offline");
            lblDatabaseStatus.setStyle("-fx-text-fill: '#931621'");
            return con;
        }

        if (con != null) {
            lblDatabaseStatus.setText("Database online");
            lblDatabaseStatus.setStyle("-fx-text-fill: '#32746D'");
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

            int listSize = Main.getSongList().size();
            Label lblListCount = (Label) scene.lookup("#lblListCount");
            lblListCount.setText("Total: " + listSize);
        }
    }

    public void onPlaySongClicked(MouseEvent mouseEvent) {
        Image img = new Image("com/example/song_finder_fx/images/icon _timer.png");

        Main.directoryCheck();

        Node node = (Node) mouseEvent.getSource();
        Scene scene = node.getScene();

        Label lblPlayerSongName = (Label) scene.lookup("#lblPlayerSongName");
        Label lblPlayerArtist = (Label) scene.lookup("#lblPlayerSongArtst");
        ImageView imgMediaPico = (ImageView) scene.lookup("#imgMediaPico");

        String isrc = srchRsISRC.getText();

        System.out.println("Song Name: " + srchRsSongName.getText());
        System.out.println("ISRC: " + isrc);
        System.out.println("Artist: " + srchRsArtist.getText());

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
                    status[0] = Main.playAudio(start, isrc);
                } else {
                    status[0] = Main.playAudio(start, isrc);
                }
                return null;
            }
        };

        task.setOnSucceeded(event -> UIController.setPlayerInfo(status, lblPlayerSongName, srchRsSongName, lblPlayerArtist, srchRsArtist, imgMediaPico));

        new Thread(task).start();
    }
    //</editor-fold>

    //<editor-fold desc="Music Player Stuff">
    public static void setPlayerInfo(boolean[] status, Label lblPlayerSongName, Label lblSongName, Label lblPlayerArtist, Label lblArtist, ImageView imgMediaPico) {
        Image pauseImg = new Image("com/example/song_finder_fx/images/icon _pause circle.png");
        Image errorImg = new Image("com/example/song_finder_fx/images/icon _error (xrp)_.png");

        if (status[0]) {
            imgMediaPico.setImage(pauseImg);
            lblPlayerSongName.setStyle("-fx-text-fill: '#000000'");
            lblPlayerSongName.setText(lblSongName.getText());
            lblPlayerArtist.setText(lblArtist.getText());
        } else {
            imgMediaPico.setImage(errorImg);
            lblPlayerSongName.setStyle("-fx-text-fill: '#000000'");
            lblPlayerSongName.setText("Error Loading Audio");
            lblPlayerSongName.setStyle("-fx-text-fill: '#931621'");
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
        checkDatabaseConnection();
        /*Task<Void> task;*/

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("layouts/collect-songs.fxml"));
            Parent newContent = loader.load();

            mainVBox.getChildren().clear();
            mainVBox.getChildren().add(newContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String directoryString = Main.getDirectoryFromDB();
        Node node = (Node) event.getSource();
        Scene scene = node.getScene();
        Button btnAudioDatabase = (Button) scene.lookup("#btnAudioDatabase");
        btnAudioDatabase.setText("   Audio Database: " + directoryString);
    }

    @FXML
    protected void onBrowseAudioButtonClick() {
        directory = Main.browseLocation();
        String shortenedString = directory.getAbsolutePath().substring(0, Math.min(directory.getAbsolutePath().length(), 73));
        btnAudioDatabase.setText("   Database: " + shortenedString + "...");
    }

    public void onBrowseDestinationButtonClick() {
        destination = Main.browseDestination();
        String shortenedString = destination.getAbsolutePath().substring(0, Math.min(destination.getAbsolutePath().length(), 73));
        btnDestination.setText("   Destination: " + shortenedString + "...");
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

    //<editor-fold desc="Testing">
    public void onTestButtonClick() throws GeneralSecurityException, IOException {
        // SheetsForJava.getSheetTest();
    }

    public void onImportToBaseButtonClick() throws SQLException, ClassNotFoundException, IOException {
        DatabaseMySQL dbmsql = new DatabaseMySQL();
        Main main = new Main();

        dbmsql.CreateBase();
        File file = main.browseFile();
        if (file != null) {
            dbmsql.ImportToBase(file);
        } else {
            System.out.println("Error! No file selected to import into Database");
        }
    }

    public void backButtonImplementationForSearchSong(MouseEvent event) throws IOException {
        Node node = (Node) event.getSource();
        Scene scene = node.getScene();
        VBox mainVBox = (VBox) scene.lookup("#mainVBox");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("layouts/search-details.fxml"));
        // loader.setController(this);
        Parent newContent = loader.load();
        mainVBox.getChildren().clear();
        mainVBox.getChildren().add(newContent);
    }
    //</editor-fold>


}