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
    public static void main(String[] args) {
        try {
            // Load the original image
            String imageUrl = "https://i.ytimg.com/vi/J7m_ut7Lb3Y/maxresdefault.jpg";
            String outputPath = "C:\\Users\\bhash\\Downloads\\cropped_image.jpg";
            BufferedImage downloadedImage = getDownloadedImage(imageUrl);

            // Assuming you already have the croppedImage from the previous step
            int targetWidth = 1400;
            int targetHeight = 1400;

            BufferedImage croppedImage = cropImage(downloadedImage);

            BufferedImage resizedImage = resizeImage(targetWidth, targetHeight, croppedImage);

            // Save the resized image
            ImageIO.write(resizedImage, "jpg", new File(outputPath));

            System.out.println("Image resized successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage getDownloadedImage(String imageUrl) throws URISyntaxException, IOException {
        URL url = new URI(imageUrl).toURL();
        return ImageIO.read(url);
    }

@NotNull
public static BufferedImage resizeImage(int targetWidth, int targetHeight, BufferedImage image) {
    BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics2D = resizedImage.createGraphics();

    // graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    // graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    // graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // Create a new resized image with the calculated dimensions
    Image scaledImage = image.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
    graphics2D.drawImage(scaledImage, 0, 0, null);
    graphics2D.dispose();

    // graphics2D.drawImage(image, 0, 0, targetWidth, targetHeight, null);
    // graphics2D.dispose();
    return resizedImage;
}

    public static BufferedImage cropImage(BufferedImage image) throws IOException {
        // BufferedImage originalImage = ImageIO.read(new File(imageLocation));

        // Calculate the square dimensions (1:1 ratio)
        int size = Math.min(image.getWidth(), image.getHeight());

        // Crop the center square
        int x = (image.getWidth() - size) / 2;
        int y = (image.getHeight() - size) / 2;
        return image.getSubimage(x, y, size, size);
    }
}
