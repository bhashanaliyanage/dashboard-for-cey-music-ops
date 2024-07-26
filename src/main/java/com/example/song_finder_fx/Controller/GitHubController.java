package com.example.song_finder_fx.Controller;

import com.example.song_finder_fx.Model.ReleaseInfo;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
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

    public File downloadUpdate(String assetName, String savePath, Button button, Task<File> task) {
        // Old code
        /*String apiUrl = String.format("https://api.github.com/repos/%s/%s/releases/latest", owner, repo);

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json");

            if (conn.getResponseCode() != 200) {
                System.out.println("HTTP Error Code: " + conn.getResponseCode());
                System.out.println("Error Message: " + conn.getResponseMessage());
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                        System.out.println(errorLine);
                    }
                }
                return;
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
                System.out.println("Asset not found in the latest release");
                return;
            }

            // Download the file
            url = new URL(downloadUrl);
            conn = (HttpURLConnection) url.openConnection();
            int contentLength = conn.getContentLength();

            try (InputStream in = conn.getInputStream();
                 FileOutputStream out = new FileOutputStream(savePath)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                long totalBytesRead = 0;

                while ((bytesRead = in.read(buffer)) != -1) {
                    if (task.isCancelled()) {
                        // Clean up and exit if cancelled
                        out.close();
                        in.close();
                    } else {
                        out.write(buffer, 0, bytesRead);
                        totalBytesRead += bytesRead;
                        int percentCompleted = (int) (totalBytesRead * 100 / contentLength);

                        Platform.runLater(() -> button.setText(percentCompleted + "%"));

                        System.out.print("\rDownload progress: " + percentCompleted + "%");
                    }
                }

                System.out.println("\nFile downloaded successfully.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }*/

        String apiUrl = String.format("https://api.github.com/repos/%s/%s/releases/latest", owner, repo);
        File downloadedFile = null;

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

                while ((bytesRead = in.read(buffer)) != -1) {
                    if (task.isCancelled()) {
                        // Clean up and exit if cancelled
                        out.close();
                        in.close();
                        if (downloadedFile.exists()) {
                            downloadedFile.delete();
                        }
                        return null;
                    }

                    out.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                    double progress = (double) totalBytesRead / contentLength;

                    int percentCompleted = (int) (progress * 100);

                    Platform.runLater(() -> button.setText(percentCompleted + "%"));
                    // task.updateProgress(progress, 1.0);

                    System.out.print("\rDownload progress: " + percentCompleted + "%");
                }

                System.out.println("\nFile downloaded successfully.");
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
        return !currentVersion.equals(latestVersion);
    }
}
