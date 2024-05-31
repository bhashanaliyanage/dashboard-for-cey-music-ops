package com.example.song_finder_fx.Model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ProductVersion {
    private double updateVersion;
    private String location;
    private String details;
    private final double currentVersion;

    public ProductVersion(Double productVersion) {
        currentVersion = productVersion;
    }

    public void setServerVersion(double serverVersion, String updateLocation, String details) {
        updateVersion = serverVersion;
        location = updateLocation;
        this.details = details;
    }

    public String getCurrentVersionInfo() {
        return "Build " + currentVersion;
    }

    public double getCurrentVersionNumber() {
        return currentVersion;
    }

    public boolean updateAvailable() {
        return currentVersion < updateVersion;
    }

    public String getUpdateVersionInfo() {
        return "Version " + updateVersion;
    }

    public File getUpdate() throws URISyntaxException, IOException {
        Path tempDir = Files.createTempDirectory("CeyMusic_Dashboard_UpdateTemp");
        InputStream in = new URI(location).toURL().openStream();
        Path tempFile = Files.createTempFile(tempDir, "update_cey_dash", ".msi");
        Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
        return new File(tempFile.toUri());
    }

    public String getDetails() {
        return details;
    }
}
