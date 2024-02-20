package com.example.song_finder_fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Objects;

public class ControllerManualClaims {

    public WebView ytPlayer;
    @FXML
    private Button btnAddToList;

    @FXML
    private ScrollPane scrlpneMain;

    @FXML
    private TextField txtURL;

    @FXML
    private VBox vboxIngest;

@FXML
void onCheckBtnClicked(ActionEvent event) throws IOException {
    System.out.println("ControllerManualClaims.onCheckBtnClicked");
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
                /*Document doc = Jsoup.connect(URL).get();
                Elements durationElement = doc.select("#metadata-line span:nth-child(1)");

                if (!durationElement.isEmpty()) {
                    String durationText = durationElement.text().trim();
                    System.out.println("Video Duration: " + durationText);
                } else {
                    System.out.println("Duration not found.");
                }*/
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

}
