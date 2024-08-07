package com.example.song_finder_fx.Controller;

import com.example.song_finder_fx.Main;
import javafx.application.Platform;

import java.awt.*;
import java.awt.TrayIcon.MessageType;

public class NotificationBuilder {
    public static void displayTrayInfo(String caption, String message) {
        Platform.runLater(() -> {
            try {
                Main.trayIcon.displayMessage(caption, message, MessageType.INFO);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void displayTrayError(String caption, String message) {
        Platform.runLater(() -> {
            try {
                Main.trayIcon.displayMessage(caption, message, MessageType.ERROR);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
