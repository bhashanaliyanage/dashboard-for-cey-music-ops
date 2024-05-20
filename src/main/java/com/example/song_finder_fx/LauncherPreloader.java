package com.example.song_finder_fx;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class LauncherPreloader extends Preloader {
    private Stage preloaderStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.preloaderStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("layouts/initPreloader.fxml"));
        Scene scene = new Scene(root);



        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        if (info.getType() == StateChangeNotification.Type.BEFORE_START) {
            if (InitPreloader.starting) {
                preloaderStage.hide();
            }
        }
    }
}
