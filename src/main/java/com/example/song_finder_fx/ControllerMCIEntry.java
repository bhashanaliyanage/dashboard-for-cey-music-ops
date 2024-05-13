package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.NotificationBuilder;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.awt.*;

public class ControllerMCIEntry {

    @FXML
    private TextField claimCNumber;

    @FXML
    private Label claimID;

    @FXML
    private TextField claimISRC;

    @FXML
    private Label claimName;

    @FXML
    private TextField claimUPC;

    @FXML
    private Label lblComposer;

    @FXML
    private Label lblLyricist;

    public void onComposerClicked() throws AWTException {
        String composer = lblComposer.getText();
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(composer);
        boolean status = clipboard.setContent(content);
        if (status) {
            NotificationBuilder.displayTrayInfo("Copied to clipboard", "Composer Copied to Clipboard");
        }
    }

    public void onLyricistClicked() throws AWTException {
        String lyricist = lblLyricist.getText();
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(lyricist);
        boolean status = clipboard.setContent(content);
        if (status) {
            NotificationBuilder.displayTrayInfo("Copied to clipboard", "Lyricist Copied to Clipboard");
        }
    }
}
