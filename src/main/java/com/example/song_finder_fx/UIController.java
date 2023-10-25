package com.example.song_finder_fx;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.sound.sampled.Clip;
import java.awt.*;
import java.io.File;
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
import java.util.stream.Stream;

public class UIController {
    public TextArea textArea;
    public Button btnDestination;
    public Button btnProceed;
    public ImageView ProgressView;
    public HBox searchAndCollect;
    public VBox textAreaVbox;
    public VBox mainVBox;
    public VBox vboxSong;
    public VBox btnDatabaseCheck;
    public Label lblDatabaseStatus;
    File directory;
    File destination;
    public Button btnAudioDatabase;
    @FXML
    public TextField searchArea;
    public ScrollPane scrlpneSong;
    public Label srchRsSongName;
    public Label srchRsISRC;
    public VBox vBoxInSearchSong;

    private final NotificationBuilder nb = new NotificationBuilder();

    // Search Items
    public void getText() throws IOException {
        // Getting search keywords
        String text = searchArea.getText();

        // Connecting to database
        DatabaseMySQL db = new DatabaseMySQL();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("layouts/song-view.fxml"));
        loader.load();

        scrlpneSong.setVisible(true);
        scrlpneSong.setContent(vboxSong);

        Task<java.util.List<Songs>> task = new Task<>() {
            @Override
            protected java.util.List<Songs> call() throws Exception {
                return db.searchSongNames(text);
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
                    Label lblSongName = (Label) nodes[i].lookup("#srchRsSongName");
                    Label lblISRC = (Label) nodes[i].lookup("#srchRsISRC");
                    lblSongName.setText(songList.get(i).getSongName());
                    lblISRC.setText(songList.get(i).getISRC().trim());
                    vboxSong.getChildren().add(nodes[i]);
                } catch (NullPointerException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Thread thread = new Thread(task);
        thread.start();
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
            String text = songList.get(0) + " + " + (songListLength - 1) + " other songs added";
            songListButtonSubtitle.setText(text);
            System.out.println(text);
        } else {
            songListButtonSubtitle.setText(songList.get(0));
            System.out.println(songList.get(0));
        }
    }

    @FXML
    public void onSearchedSongClick(MouseEvent mouseEvent) throws IOException, SQLException, ClassNotFoundException {
        String name = srchRsSongName.getText();
        String isrc = srchRsISRC.getText();

        DatabaseMySQL db = new DatabaseMySQL();
        List<String> songDetails = db.searchSongDetails(isrc);

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

    // Primary UI Buttons
    @FXML
    protected void onSearchDetailsButtonClick() throws ClassNotFoundException {
        Connection con = checkDatabaseConnection();

        if (con != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("layouts/search-details.fxml"));
                Parent newContent = loader.load();
                mainVBox.getChildren().clear();
                mainVBox.getChildren().add(newContent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("onSearchDetailsButtonClick");
        } else {
            showErrorDialog("Database Connection Error!", "Error Connecting to Database", "Please check your XAMPP server up and running");
        }
    }

    public void onCollectSongsButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("layouts/collect-songs.fxml"));
            Parent newContent = loader.load();

            mainVBox.getChildren().clear();
            mainVBox.getChildren().add(newContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public void onProceedButtonClick() throws ClassNotFoundException {
        Connection con = checkDatabaseConnection();
        if (con != null) {

            searchAndCollect.setStyle("-fx-background-color: #eeefee; -fx-border-color: '#c0c1c2';");
            ProgressView.setVisible(true);
            btnAudioDatabase.setDisable(true);
            btnDestination.setDisable(true);
            textArea.setDisable(true);
            btnProceed.setDisable(true);
            textAreaVbox.setDisable(true);
            Task<Void> task = null;
            btnProceed.setText("Processing");

            if (directory == null || destination == null) {
                showErrorDialog("Empty Location Entry", "Please browse for Audio Database and Destination Location", "Use the location section for this");
                btnProceed.setText("Proceed");
                searchAndCollect.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: '#e9ebee';");
                ProgressView.setVisible(false);
                btnAudioDatabase.setDisable(false);
                btnDestination.setDisable(false);
                textArea.setDisable(false);
                btnProceed.setDisable(false);
                textAreaVbox.setDisable(false);
            } else {
                String text = textArea.getText();
                System.out.println(text);
                // System.out.println("Here");
                if (!text.isEmpty()) {
                    task = new Task<>() {
                        @Override
                        protected Void call() throws Exception {
                            Main main = new Main();

                            String[] ISRCCodes = text.split("\\n");

                            for (String ISRCCode : ISRCCodes) {
                                if (ISRCCode.length() == 12) {
                                    String done = main.searchAudios(text, directory, destination);
                                    if (done.equals("Done")) {
                                        Platform.runLater(() -> {
                                            // Code that updates the UI goes here
                                            btnProceed.setText("Proceed");
                                            searchAndCollect.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: '#e9ebee';");
                                            ProgressView.setVisible(false);
                                            btnAudioDatabase.setDisable(false);
                                            btnDestination.setDisable(false);
                                            textArea.setDisable(false);
                                            btnProceed.setDisable(false);
                                            textAreaVbox.setDisable(false);
                                        });
                                    }
                                } else {
                                    showErrorDialog("Invalid ISRC Code", "Invalid or empty ISRC Code", ISRCCode);
                                }
                            }
                            return null;
                        }
                    };
                } else {
                    showErrorDialog("Invalid ISRC Code", "Empty ISRC Code", "Please enter ISRC codes in the text area");
                    btnProceed.setText("Proceed");
                    searchAndCollect.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: '#e9ebee';");
                    ProgressView.setVisible(false);
                    btnAudioDatabase.setDisable(false);
                    btnDestination.setDisable(false);
                    textArea.setDisable(false);
                    btnProceed.setDisable(false);
                    textAreaVbox.setDisable(false);
                }

            }
            assert task != null;
            task.setOnSucceeded(e -> btnProceed.setText("Proceed"));

            new Thread(task).start();
        } else {
            showErrorDialog("Database Connection Error!", "Error Connecting to Database", "Please check your XAMPP server up and running");
        }

    }

    // Error Dialog
    private static void showErrorDialog(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }

    // Testing
    public void onTestNotifyButtonClick() throws AWTException {
        System.out.println("Test Notification Sent");
        NotificationBuilder nb = new NotificationBuilder();
        // nb.displayTrayInfo();
        nb.displayTrayError("Error", "Test Error Notification");
    }

    // Database Things
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

    public void onDatabaseConnectionBtnClick() throws ClassNotFoundException, AWTException {
        // TODO: Do this on a separate thread
        Connection con = checkDatabaseConnection();
        if (con != null) {
            nb.displayTrayInfo("Database Connected", "Database Connection Success");
        } else {
            nb.displayTrayError("Error", "Error connecting database");
        }
    }

    private Connection checkDatabaseConnection() throws ClassNotFoundException {
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

    public void onSongListButtonClicked(MouseEvent mouseEvent) {
        // TODO: Make song list
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

    private void directoryCheck() {
        Main.directoryCheck();
        if (directory != null) {
            System.out.println(directory.getAbsolutePath());
        } else {
            System.out.println("No audio database directory specified");
            directory = Main.browseLocation();
            System.out.println(directory.getAbsolutePath());
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

    public void onBtnPlayClicked(MouseEvent mouseEvent) throws IOException {
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

        Task<Void> task = null;
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

        task.setOnSucceeded(event -> {
            setPlayerInfo(status, lblPlayerSongName, lblSongName, lblPlayerArtist, lblArtist, imgMediaPico);
        });

        new Thread(task).start();
    }

    private static void setPlayerInfo(boolean[] status, Label lblPlayerSongName, Label lblSongName, Label lblPlayerArtist, Label lblArtist, ImageView imgMediaPico) {
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