package com.example.song_finder_fx.Controller;

import com.example.song_finder_fx.Main;

import java.awt.*;
import java.awt.TrayIcon.MessageType;

public class NotificationBuilder {
    public static void displayTrayInfo(String caption, String message) throws AWTException {
        Main.trayIcon.displayMessage(caption, message, MessageType.INFO);
    }

    public static void displayTrayError(String caption, String message) throws AWTException {
        Main.trayIcon.displayMessage(caption, message, MessageType.ERROR);
    }
}
