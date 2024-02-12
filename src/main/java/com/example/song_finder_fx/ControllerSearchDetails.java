package com.example.song_finder_fx;

import com.example.song_finder_fx.ControllerSettings;
import com.example.song_finder_fx.Model.Songs;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.Objects;

public class ControllerSearchDetails {
    @FXML
    private Label lblFeaturing;

    @FXML
    private Label lblProductName;

    @FXML
    private Label lblShare;

    @FXML
    private Label lblUPC;

    public ControllerSearchDetails() {
        setValues();
    }

    private void setValues() {
        lblFeaturing.setText("Test");
        lblProductName.setText("Test");
        lblUPC.setText("Test");
        lblShare.setText("Test");
    }

    public Node getView() throws IOException {
        HBox hbox = new HBox();
        Node node = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/search-song-expanded-view.fxml")));
        hbox.getChildren().add(node);

        return hbox;
    }

    public void setValues(Songs songDetails) {
        lblFeaturing.setText(songDetails.getFeaturing());
        lblProductName.setText(songDetails.getProductName());
        lblUPC.setText(songDetails.getUPC());
        lblShare.setText(songDetails.getControl());
    }


}
