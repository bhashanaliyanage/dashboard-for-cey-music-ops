package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.UserSettingsManager;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

public class ControllerSettings {
    private UIController mainUIController = null;
    public Button btnAudioDatabase;
    public Button btnSave;
    public Label lblVersion;
    public Label lblVersionInfoAboutPage;

    public ControllerSettings() {

    }

    public ControllerSettings(UIController MainUIController) {
        this.mainUIController = MainUIController;
    }

    public void loadThings() throws IOException, SQLException, ClassNotFoundException {
        FXMLLoader loader = new FXMLLoader(ControllerSettings.class.getResource("layouts/settings.fxml"));
        loader.setController(this);
        Parent newContent = loader.load();

        mainUIController.mainVBox.getChildren().clear();
        mainUIController.mainVBox.getChildren().add(newContent);

        String directorySQLite = Main.getAudioDatabaseLocation();

        if (directorySQLite == null) {
            System.out.println("Directory Null!");
        } else {
            System.out.println("Audio Database Directory: " + directorySQLite);
            btnAudioDatabase.setText("   " + directorySQLite);
        }
    }

    public void onBrowseAudioButtonClick() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select a directory");
        // File selectedDirectory = chooser.showDialog(btnAudioDatabase.getScene().getWindow());

        UIController.directory = chooser.showDialog(btnAudioDatabase.getScene().getWindow());
        Main.selectedDirectory = UIController.directory;

        if (UIController.directory != null) {
            String shortenedString = UIController.directory.getAbsolutePath().substring(0, Math.min(UIController.directory.getAbsolutePath().length(), 73));
            btnAudioDatabase.setText("   Database: " + shortenedString + "...");
        } else {
            System.out.println("No directory specified");
        }
    }


    public void onSaveButtonClicked() throws SQLException, ClassNotFoundException {
        // System.out.println("Save Button Clicked");
        File directory = Main.getSelectedDirectory();

        if (Main.selectedDirectory.exists()) { // check if the directory exists
            if (Main.selectedDirectory.isDirectory()) { // check if the directory is a directory
                if (Main.selectedDirectory.canWrite()) {
                    String directoryString = directory.getAbsolutePath();
                    Properties settings = new Properties();
                    settings.setProperty("adb", directoryString);
                    UserSettingsManager.saveUserSettings(settings);
                    btnSave.setText("Saved");
                }
            }
        }

    }

    public void loadAbout(Node aboutView) throws IOException {
        mainUIController.mainVBox.getChildren().clear();
        mainUIController.mainVBox.getChildren().add(aboutView);

        Scene scene = aboutView.getScene();
        lblVersionInfoAboutPage = (Label) scene.lookup("#lblVersionInfoAboutPage");
        lblVersionInfoAboutPage.setText(Main.versionInfo.getCurrentVersionInfo());

        if (Main.versionInfo.updateAvailable()) {
            loadUpdate();
        }
    }

    private void loadUpdate() throws IOException {
        FXMLLoader loader = new FXMLLoader(ControllerSettings.class.getResource("layouts/sidepanel-update.fxml"));
        // loader.setController(this);
        Parent sidepanelContent = loader.load();

        mainUIController.sideVBox.getChildren().clear();
        mainUIController.sideVBox.getChildren().add(sidepanelContent);

        // lblVersion.setText(Main.versionInfo.getUpdateVersionInfo());
    }

    @FXML
    void onTestWebAP(ActionEvent event) {
        WebView webView = new WebView();

        webView.getEngine().setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        webView.getEngine().setJavaScriptEnabled(true);
        webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                webView.getEngine().executeScript(
                        "var meta = document.createElement('meta'); " +
                                "meta.httpEquiv = 'Content-Security-Policy'; " +
                                "meta.content = \"default-src * 'unsafe-inline' 'unsafe-eval'\"; " +
                                "document.getElementsByTagName('head')[0].appendChild(meta);"
                );
            }
        });

        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>YouTube Audio Player</title>
                </head>
                <body>
                    <div id="player"></div>
                    <button onclick="toggleAudio()">Play/Pause Audio</button>

                    <script src="https://www.youtube.com/iframe_api"></script>
                    <script>
                        var player;
                        function onYouTubeIframeAPIReady() {
                            player = new YT.Player('player', {
                                height: '0',
                                width: '0',
                                videoId: 'MjDaa-L8_5o',
                                playerVars: {
                                    'autoplay': 0,
                                    'controls': 0,
                                },
                                events: {
                                    'onReady': onPlayerReady
                                }
                            });
                        }

                        function onPlayerReady(event) {
                            event.target.setPlaybackQuality('small');
                        }

                        function toggleAudio() {
                            if (player.getPlayerState() == 1) {
                                player.pauseVideo();
                            } else {
                                player.playVideo();
                            }
                        }
                    </script>
                </body>
                </html>
                """;

        webView.getEngine().loadContent(html);

        UIController.mainVBoxStatic.getChildren().clear();
        UIController.mainVBoxStatic.getChildren().add(webView);
    }

}
