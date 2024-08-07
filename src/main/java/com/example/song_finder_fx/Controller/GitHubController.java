package com.example.song_finder_fx.Controller;

import com.example.song_finder_fx.Main;
import com.example.song_finder_fx.Model.ReleaseInfo;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GitHubController {

    private final String owner;
    private final String repo;

    public GitHubController(String owner, String repo) {
        this.owner = owner;
        this.repo = repo;
    }

public File downloadUpdate(String assetName, String savePath, Button button, Label label, Task<File> task) {
    String apiUrl = String.format("https://api.github.com/repos/%s/%s/releases/latest", owner, repo);
    File downloadedFile = null;

    // Store the original email text
    final String originalEmail = label.getText();
    final boolean[] updatedBefore = {false};

    try {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/vnd.github.v3+json");

        String downloadUrl = getDownloadUrl(assetName, conn);

        // Download the file
        url = new URL(downloadUrl);
        conn = (HttpURLConnection) url.openConnection();
        int contentLength = conn.getContentLength();

        downloadedFile = new File(savePath);
        try (InputStream in = conn.getInputStream();
             FileOutputStream out = new FileOutputStream(downloadedFile)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalBytesRead = 0;

            // Create fade-out transition
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), label);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            // Create fade-in transition
            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), label);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            /*fadeIn.setOnFinished(e -> {

            });*/

            // Play fade-out, then change text and fade-in
            fadeOut.setOnFinished(e -> {
                label.setText("Downloading Update: 0%");
                updatedBefore[0] = true;
                fadeIn.play();
            });

            Platform.runLater(fadeOut::play);


            while ((bytesRead = in.read(buffer)) != -1) {
                if (task.isCancelled()) {
                    // Clean up and exit if cancelled
                    out.close();
                    in.close();
                    if (downloadedFile.exists()) {
                        downloadedFile.delete();
                    }

                    Platform.runLater(() -> {
                        FadeTransition fadeOutCancel = new FadeTransition(Duration.millis(500), label);
                        fadeOutCancel.setFromValue(1.0);
                        fadeOutCancel.setToValue(0.0);
                        fadeOutCancel.setOnFinished(e -> {
                            label.setText(originalEmail);
                            FadeTransition fadeInCancel = new FadeTransition(Duration.millis(500), label);
                            fadeInCancel.setFromValue(0.0);
                            fadeInCancel.setToValue(1.0);
                            fadeInCancel.play();
                        });
                        fadeOutCancel.play();
                    });

                    return null;
                }

                out.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                double progress = (double) totalBytesRead / contentLength;

                int percentCompleted = (int) (progress * 100);

                if (updatedBefore[0]) {
                    Platform.runLater(() -> {
                        button.setText(percentCompleted + "%");
                        label.setText("Downloading Update: " + percentCompleted + "%");
                    });
                }
                // task.updateProgress(progress, 1.0);

                System.out.print("\rDownload progress: " + percentCompleted + "%");
            }

            System.out.println("\nFile downloaded successfully.");

            // Fade out the download text and fade in the original email
            Platform.runLater(() -> {
                FadeTransition fadeOutFinal = new FadeTransition(Duration.millis(500), label);
                fadeOutFinal.setFromValue(1.0);
                fadeOutFinal.setToValue(0.0);
                fadeOutFinal.setOnFinished(e -> {
                    label.setText(Main.userSession.getEmail());
                    label.setStyle("");
                    FadeTransition fadeInFinal = new FadeTransition(Duration.millis(500), label);
                    fadeInFinal.setFromValue(0.0);
                    fadeInFinal.setToValue(1.0);
                    fadeInFinal.play();
                });
                fadeOutFinal.play();
            });

            return downloadedFile;
        }

    } catch (Exception e) {
        if (downloadedFile != null && downloadedFile.exists()) {
            downloadedFile.delete();
        }
        e.printStackTrace();
    }

    return downloadedFile;
}

    private static @NotNull String getDownloadUrl(String assetName, HttpURLConnection conn) throws IOException {
        if (conn.getResponseCode() != 200) {
            throw new IOException("HTTP Error Code: " + conn.getResponseCode() + ", Message: " + conn.getResponseMessage());
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        JSONObject jsonResponse = new JSONObject(response.toString());
        JSONArray assets = jsonResponse.getJSONArray("assets");
        String downloadUrl = null;

        for (int i = 0; i < assets.length(); i++) {
            JSONObject asset = assets.getJSONObject(i);
            if (asset.getString("name").equals(assetName)) {
                downloadUrl = asset.getString("browser_download_url");
                break;
            }
        }

        if (downloadUrl == null) {
            throw new IOException("Asset not found in the latest release");
        }
        return downloadUrl;
    }

    public ReleaseInfo getLatestVersion() {
        String apiUrl = String.format("https://api.github.com/repos/%s/%s/releases/latest", owner, repo);

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json");

            if (conn.getResponseCode() != 200) {
                System.out.println("HTTP Error Code: " + conn.getResponseCode());
                System.out.println("Error Message: " + conn.getResponseMessage());
                return null;
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }

            JSONObject jsonResponse = new JSONObject(response.toString());
            String version = jsonResponse.getString("tag_name");
            String releaseNotes = jsonResponse.getString("body");

            // return jsonResponse.getString("tag_name");
            return new ReleaseInfo(version, releaseNotes);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isNewVersionAvailable(String currentVersion, String latestVersion) {
        // return !currentVersion.equals(latestVersion);

        // Remove the 'v' prefix if it exists
        String gitTest = "Test";

        currentVersion = currentVersion.startsWith("v") ? currentVersion.substring(1) : currentVersion;
        latestVersion = latestVersion.startsWith("v") ? latestVersion.substring(1) : latestVersion;

        String[] currentParts = currentVersion.split("\\.");
        String[] latestParts = latestVersion.split("\\.");

        // Compare each part of the version number
        for (int i = 0; i < Math.max(currentParts.length, latestParts.length); i++) {
            int currentPart = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;
            int latestPart = i < latestParts.length ? Integer.parseInt(latestParts[i]) : 0;

            if (latestPart > currentPart) {
                return true;
            } else if (latestPart < currentPart) {
                return false;
            }
        }

        // If we've gotten here, the versions are identical
        return false;
    }
}
