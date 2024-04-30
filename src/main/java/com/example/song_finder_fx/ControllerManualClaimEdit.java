package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.ImageProcessor;
import com.example.song_finder_fx.Controller.NotificationBuilder;
import com.example.song_finder_fx.Controller.SceneController;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class ControllerManualClaimEdit {

    @FXML
    private Label lblClaimID;

    @FXML
    private TextField txtComposer;

    @FXML
    private TextField txtLyricist;

    @FXML
    private TextField txtSongName;

    @FXML
    private ImageView imgPreview;

    @FXML
    void onSave() throws SQLException {
        String songID = lblClaimID.getText();
        String trackName = txtSongName.getText();
        String composer = txtComposer.getText();
        String lyricist = txtLyricist.getText();

        for (int i = 0; i < ControllerMCList.labelsSongNo.size(); i++) {
            if (Objects.equals(ControllerMCList.labelsSongNo.get(i).getText(), songID)) {
                DatabasePostgres.editManualClaim(songID, trackName, composer, lyricist);

                ControllerMCList.labelsSongName.get(i).setText(trackName);
                ControllerMCList.labelsComposer.get(i).setText(composer);
                ControllerMCList.labelsLyricist.get(i).setText(lyricist);
            }
        }
    }

    /*@FXML
    void onChangeImageClicked(MouseEvent event) throws IOException {
        Scene scene = SceneController.getSceneFromEvent(event);
        File file = Main.browseForImage(scene.getWindow());
        if (file != null) {
            // Covert user input to a Java BufferedImage
            BufferedImage bufferedImage = ImageIO.read(file);
            // Resize user input to preview size
            BufferedImage previewImage = ImageProcessor.resizeImage(210, 210, bufferedImage);
            // Convert BufferedImage to JavaFX image and set it into user interface
            imgPreview.setImage(SwingFXUtils.toFXImage(previewImage, null));
        }
    }*/

@FXML
public void onChangeImageClicked(MouseEvent event) throws IOException, SQLException, AWTException {
    Scene scene = SceneController.getSceneFromEvent(event);
    File file = Main.browseForImage(scene.getWindow());
    if (file != null) {
        // Covert user input to a Java BufferedImage
        BufferedImage biArtwork = ImageIO.read(file);
        System.out.println("biArtwork = " + biArtwork.getColorModel());

        // Check image dimensions
        int imageWidth = biArtwork.getWidth();
        int imageHeight = biArtwork.getHeight();

        if (imageWidth > 1400 || imageHeight > 1400) {
            // Getting Claim ID
            String claimID = lblClaimID.getText();

            // Resize user input to preview size
            BufferedImage previewImage = ImageProcessor.resizeImage(210, 210, biArtwork);

            // Updating Database
            int status = DatabasePostgres.updateClaimArtwork(claimID, biArtwork, previewImage);

            if (status > 0) {
                // Convert BufferedImage to JavaFX image and set it into user interface
                Image image = SwingFXUtils.toFXImage(previewImage, null);
                imgPreview.setImage(image);

                for (int i = 0; i < ControllerMCList.labelsSongNo.size(); i++) {
                    if (Objects.equals(ControllerMCList.labelsSongNo.get(i).getText(), claimID)) {
                        ControllerMCList.ivArtworks.get(i).setImage(image);
                    }
                }
            } else {
                // TODO: 4/3/2024 Error Updating Database
                NotificationBuilder.displayTrayError("Error Updating Artwork", "Database Malfunction");
            }
        } else {
            // TODO: 4/3/2024 Execute default functionality for smaller images
            NotificationBuilder.displayTrayError("Invalid Dimensions", "Image Dimensions are below 1400px");
        }

    }
}
}
