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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UIController {
    public TextArea textArea;
    public Button btnDestination;
    public Button btnProceed;
    public ImageView ProgressView;
    public HBox searchAndCollect;
    public VBox textAreaVbox;
    public VBox mainVBox;
    public VBox vboxSong;
    public Label searchResult;
    public Label searchResult2;
    public Label searchResult3;
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
    private List<String> songList = new ArrayList<>();

    private NotificationBuilder nb = new NotificationBuilder();

    // Search Items
    public void getText() throws IOException {
        // Getting search keywords
        String text = searchArea.getText();

        // Connecting to database
        DatabaseMySQL db = new DatabaseMySQL();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("layouts/song-view.fxml"));
        Parent newContent = loader.load();

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
                    int finalI = i;
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
        Node node = (Node) actionEvent.getSource();
        Scene scene = node.getScene();
        Label isrc = (Label) scene.lookup("#songISRC");
        songList.add(isrc.getText());
        System.out.println("========================");
        for (String isrcs : songList) {
            System.out.println(isrcs);
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
    protected void onSearchDetailsButtonClick(ActionEvent event) throws ClassNotFoundException, AWTException, IOException {
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

    public void onCollectSongsButtonClick(ActionEvent event) {
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
        Main main = new Main();
        directory = main.browseLocation();
        String shortenedString = directory.getAbsolutePath().substring(0, Math.min(directory.getAbsolutePath().length(), 73));
        btnAudioDatabase.setText("   Database: " + shortenedString + "...");
    }

    public void onBrowseDestinationButtonClick() {
        Main main = new Main();
        destination = main.browseDestination();
        String shortenedString = destination.getAbsolutePath().substring(0, Math.min(destination.getAbsolutePath().length(), 73));
        btnDestination.setText("   Destination: " + shortenedString + "...");
    }

    @FXML
    protected void onUpdateDatabaseButtonClick() throws SQLException, IOException, ClassNotFoundException {
        Main main = new Main();
        File file = main.browseFile();
        DatabaseMySQL.updateBase(file);
    }

    public void onProceedButtonClick(ActionEvent event) throws SQLException, ClassNotFoundException, IOException, AWTException {
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
                System.out.println("Here");
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
            task.setOnSucceeded(e -> {
                btnProceed.setText("Proceed");
            });

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
    public void onTestNotifyButtonClick(ActionEvent event) throws AWTException {
        System.out.println("Test Notification Sent");
        NotificationBuilder nb = new NotificationBuilder();
        // nb.displayTrayInfo();
        nb.displayTrayError("Error", "Test Error Notification");
    }

    // Database Things
    public void onImportToBaseButtonClick(ActionEvent event) throws SQLException, ClassNotFoundException, GeneralSecurityException, IOException {
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

    public void onDatabaseConnectionBtnClick(MouseEvent mouseEvent) throws SQLException, ClassNotFoundException, AWTException, IOException {
        Connection con = checkDatabaseConnection();
        if (con != null) {
            nb.displayTrayInfo("Database Connected", "Database Connection Success");
        } else {
            nb.displayTrayError("Error", "Error connecting database");
        }
    }

    private Connection checkDatabaseConnection() throws ClassNotFoundException, AWTException, IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("layouts/song-view.fxml"));
        Parent newContent = loader.load();
        mainVBox.getChildren().clear();
        mainVBox.getChildren().add(newContent);
        Connection con = null;
        NotificationBuilder nb = new NotificationBuilder();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/songData";
            String username = "ceymusic";
            String password = "ceymusic";
            con = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            lblDatabaseStatus.setText("Error connecting database");
            lblDatabaseStatus.setStyle("-fx-text-fill: '#931621'");

            return con;
        }
        if (con != null) {
            lblDatabaseStatus.setText("Database connected");
            lblDatabaseStatus.setStyle("-fx-text-fill: '#32746D'");
        }
        return con;
    }


}