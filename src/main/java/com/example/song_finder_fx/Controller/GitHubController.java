package com.example.song_finder_fx.Controller;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    public void downloadUpdate(String assetName, String savePath) {
        String apiUrl = String.format("https://api.github.com/repos/%s/%s/releases/latest", owner, repo);

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
                    out.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                    int percentCompleted = (int) (totalBytesRead * 100 / contentLength);
                    System.out.print("\rDownload progress: " + percentCompleted + "%");
                }
                System.out.println("\nFile downloaded successfully.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getLatestVersion() {
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
            return jsonResponse.getString("tag_name");

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isNewVersionAvailable(String currentVersion, String latestVersion) {
        return !currentVersion.equals(latestVersion);
    }
}
