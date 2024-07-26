package com.example.song_finder_fx.Model;

import com.example.song_finder_fx.Controller.GitHubController;
import javafx.concurrent.Task;
import javafx.scene.control.Button;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProductVersion {
    private String updateVersion;
    private String details;
    private final String currentVersion;
    private GitHubController controller;

    public ProductVersion(String productVersion) {
        currentVersion = productVersion;
    }

    public void setServerVersion(String serverVersion, String details) {
        updateVersion = serverVersion;
        this.details = details;
    }

    public String getCurrentVersionInfo() {
        return "Build " + currentVersion;
    }

    public String getCurrentVersionNumber() {
        return currentVersion;
    }

    public boolean updateAvailable() {
        String owner = "bhashanaliyanage";
        String repo = "dashboard-for-cey-music-ops";
        controller = new GitHubController(owner, repo);

        // Check Update
        ReleaseInfo releaseInfo = controller.getLatestVersion();

        if (releaseInfo != null) {
            System.out.println("Latest version: " + releaseInfo.version);
            System.out.println("Current version: " + currentVersion);

            return controller.isNewVersionAvailable(currentVersion, releaseInfo.version);
        } else {
            System.out.println("Updates unavailable");
        }

        return false;
    }

    public File getUpdate(Button button, Task<File> task) throws IOException {
        // Old code
        /*Path tempDir = Files.createTempDirectory("CeyMusic_Dashboard_UpdateTemp");
        // InputStream in = new URI(location).toURL().openStream();
        Path tempFile = Files.createTempFile(tempDir, "update_cey_dash", ".msi");
        // System.out.println("ProductVersion.getUpdate");

        System.out.println("Temporary Update Directory: " + tempFile.toString());

        String assetName = "build1.msi";
        controller.downloadUpdate(assetName, tempFile.toString(), button, task);
        // Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
        return new File(tempFile.toUri());*/

        // Modification
        Path tempDir = Files.createTempDirectory("CeyMusic_Dashboard_UpdateTemp");
        Path tempFile = Files.createTempFile(tempDir, "update_cey_dash", ".msi");

        System.out.println("Temporary Update Directory: " + tempFile.toString());

        String assetName = "build1.msi";
        return controller.downloadUpdate(assetName, tempFile.toString(), button, task);
    }

    public String getUpdateVersionInfo() {
        return updateVersion;
    }

    public String getDetails() {
        return details;
    }
}
