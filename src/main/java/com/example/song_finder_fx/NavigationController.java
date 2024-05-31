package com.example.song_finder_fx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

public class NavigationController {

    @FXML
    private HBox btnArtistReports;

    @FXML
    private HBox btnArtistReports1;

    @FXML
    private HBox btnCollectSongs;

    @FXML
    private VBox btnDatabaseCheck;

    @FXML
    private VBox btnDatabaseCheck2;

    @FXML
    private HBox btnRevenueAnalysis;

    @FXML
    private HBox btnSeachSongs;

    @FXML
    private HBox btnSongList;

    @FXML
    private ImageView imgMediaPico;

    @FXML
    private Label lblDatabaseStatus;

    @FXML
    private Label lblDatabaseStatus1;

    @FXML
    private Label lblDatabaseStatus11;

    @FXML
    private Label lblDatabaseStatus112;

    @FXML
    private Label lblDatabaseStatus1121;

    @FXML
    private Label lblDatabaseStatus11211;

    @FXML
    private Label lblPlayerSongArtst;

    @FXML
    private Label lblPlayerSongName;

    @FXML
    private Label lblSongListSub;

    @FXML
    private Label lblUser;

    @FXML
    private Label lblUserEmailAndUpdate;

    @FXML
    private Rectangle rctArtistReports;

    @FXML
    private Rectangle rctArtistReports1;

    @FXML
    private Rectangle rctCollectSongs;

    @FXML
    private Rectangle rctRevenue;

    @FXML
    private Rectangle rctSearchSongs;

    private Node aboutNode;

    public NavigationController() throws IOException {
        aboutNode = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/about.fxml")));
    }

    @FXML
    void hideLeft(MouseEvent event) {

    }

    @FXML
    void onAboutButtonClicked(MouseEvent event) throws IOException {
        Scene scene = ((Node) event.getSource()).getScene();
        VBox mainVBox = (VBox) scene.lookup("#mainVBox");

        mainVBox.getChildren().clear();
        mainVBox.getChildren().add(aboutNode);

        Label lblVersionInfoAboutPage = (Label) scene.lookup("#lblVersionInfoAboutPage");
        lblVersionInfoAboutPage.setText(Main.versionInfo.getCurrentVersionInfo());

        if (Main.versionInfo.updateAvailable()) {
            loadUpdate(scene);
        }
    }

    private void loadUpdate(Scene scene) throws IOException {
        VBox sideVBox = (VBox) scene.lookup("#sideVBox");
        Label lblVersion = (Label) scene.lookup("#lblVersion");
        FXMLLoader loader = new FXMLLoader(ControllerSettings.class.getResource("layouts/sidepanel-update.fxml"));
        loader.setController(this);
        Parent sidepanelContent = loader.load();

        sideVBox.getChildren().clear();
        sideVBox.getChildren().add(sidepanelContent);

        lblVersion.setText(Main.versionInfo.getUpdateVersionInfo());
    }

    public void onUpdateBtnClick() throws IOException, URISyntaxException {
        File updateFile = Main.versionInfo.getUpdate();
        Desktop.getDesktop().open(updateFile);
    }

    @FXML
    void onArtistReportsBtnClick(MouseEvent event) {
        changeSelectorTo(rctArtistReports);
    }

    @FXML
    void onCollectSongsButtonClick(MouseEvent event) {
        changeSelectorTo(rctCollectSongs);
    }

    @FXML
    void onDatabaseConnectionBtnClick(MouseEvent event) {

    }

    @FXML
    void onMusicPlayerBtnClick(MouseEvent event) {

    }

    @FXML
    void onRevenueAnalysisBtnClick(MouseEvent event) {
        changeSelectorTo(rctRevenue);
    }

    @FXML
    void onSearchDetailsButtonClick(MouseEvent event) {
        changeSelectorTo(rctSearchSongs);
    }

    private void changeSelectorTo(Rectangle selector) {
        rctSearchSongs.setVisible(false);
        rctCollectSongs.setVisible(false);
        rctRevenue.setVisible(false);
        rctArtistReports.setVisible(false);

        selector.setVisible(true);
    }

    @FXML
    void onSettingsButtonClicked(MouseEvent event) {

    }

    @FXML
    void onSongListButtonClicked(MouseEvent event) {

    }

}