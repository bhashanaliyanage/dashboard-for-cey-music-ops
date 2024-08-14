package com.example.song_finder_fx.Controller;

import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ImageProcessor {

    public static BufferedImage getDownloadedImage(String imageUrl) throws URISyntaxException, IOException {
        URL url = new URI(imageUrl).toURL();
        File uploadArtwork = new File("src/main/resources/com/example/song_finder_fx/images/manual_claims/upload_artwork.jpg");

        try {
            return ImageIO.read(url);
        } catch (IOException e) {
            // Returns a default image if the thumbnail is not downloadable
            return ImageIO.read(uploadArtwork);
        }
    }

    @NotNull
    public static BufferedImage resizeImage(int targetWidth, int targetHeight, BufferedImage image) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();

        // Create a new resized image with the calculated dimensions
        // Image scaledImage = image.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        // graphics2D.drawImage(scaledImage, 0, 0, null);
        graphics2D.drawImage(image, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

    public static BufferedImage cropImage(BufferedImage image) throws IOException {
        // Calculate the square dimensions (1:1 ratio)
        int size = Math.min(image.getWidth(), image.getHeight());

        // Crop the center square
        int x = (image.getWidth() - size) / 2;
        int y = (image.getHeight() - size) / 2;
        return image.getSubimage(x, y, size, size);
    }

}
