package com.example.song_finder_fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.Element;

import java.io.IOException;
import java.util.Objects;

public class ControllerManualClaims {

    public WebView ytPlayer;
    public VBox vboxTracks;

    @FXML
    private TextField txtURL;

    @FXML
    public void initialize() throws IOException {
        // Node node = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/manual-claims-track.fxml")));
        // vboxTracks.getChildren().setAll(node);
    }

    @FXML
    void onCheckBtnClicked(ActionEvent event) throws IOException {
        String URL = txtURL.getText();

        if (!Objects.equals(URL, "")) {
            System.out.println("URL = " + URL);

            if (URL.length() > 32) {
                String prefix = URL.substring(0, 32);

                if (prefix.equals("https://www.youtube.com/watch?v=")) {
                    String ID = URL.substring(32);
                    String embedID = "https://www.youtube.com/embed/" + ID;

                    System.out.println("embedID = " + embedID);

                    ytPlayer.getEngine().load(embedID);

                    Node node = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/manual-claims-track.fxml")));
                    vboxTracks.getChildren().setAll(node);
                } else {
                    System.out.println("No");
                    System.out.println("prefix = " + prefix);
                    System.out.println("URL = " + URL);
                }
            } else {
                System.out.println("Invalid URL");
            }
        } else {
            System.out.println("URL Empty");
        }
    }

    public void onAddManualClaim(ActionEvent event) {
        System.out.println("ControllerManualClaims.onAddManualClaim");
    }
}
