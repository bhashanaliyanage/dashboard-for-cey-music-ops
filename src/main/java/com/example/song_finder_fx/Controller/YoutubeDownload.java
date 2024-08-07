package com.example.song_finder_fx.Controller;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class YoutubeDownload {

    public static void downloadAudio(String url, String fileLocation, String fileName) throws IOException, InterruptedException {
        String file = fileLocation + "\\" + fileName;
        downloadAudioOnly(url, file);

    }

    public static void downloadAudioOnly(String url, String file) throws IOException, InterruptedException {
            String nodeScriptPath = "libs/jdown.js";

            System.out.println("url = " + url);
            System.out.println("file = " + file);

            ProcessBuilder processBuilder = new ProcessBuilder("node", nodeScriptPath, url, file);
            Process process = processBuilder.start();

            // Read and print output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                String finalLine = line;
                Platform.runLater(() -> System.out.println(finalLine));
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Audio download script executed successfully.");
            } else {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("An error occurred");
                    alert.setContentText("Error Downloading Audio");
                    Platform.runLater(alert::showAndWait);
                });
            }
    }

    public static void trimAudio(String filePath, String outputPath, String startTime, String EndTime) throws IOException, InterruptedException {
        String nodeScriptPPath = "libs/cutAud.js";

        Platform.runLater(() -> {
            System.out.println("filePath = " + filePath);
            System.out.println("outputPath = " + outputPath);
            System.out.println("startTime = " + startTime);
            System.out.println("EndTime = " + EndTime);
        });

        String[] cmdArray = {
                "node",
                nodeScriptPPath,
                filePath,
                outputPath,
                startTime,
                EndTime
        };

        // ProcessBuilder processBuilder = new ProcessBuilder("node", nodeScriptPPath, filePath, outputPath, startTime, EndTime);
        ProcessBuilder processBuilder = new ProcessBuilder(cmdArray);
        Process process = processBuilder.start();

        // Read and print output
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            String finalLine = line;
            Platform.runLater(() -> System.out.println(finalLine));
        }

        int exitCode = process.waitFor();

        if (exitCode == 0) {
            System.out.println("Node.js script executed successfully.");
        } else {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Trimming Audio");
                alert.setHeaderText("An error occurred while trimming audio");
                String message = String.format("""
                         File Path: '%s'
                                                \s
                         Output Path: '%s'
                                                \s
                         Trim Start: '%s'
                                                \s
                         Trim End: '%s'
                        \s""", filePath, outputPath, startTime, EndTime);
                alert.setContentText(message);
                Platform.runLater(alert::showAndWait);
            });
        }
    }


    public static void convertAudio(Path sourcePath, Path destinationPath) throws IOException, InterruptedException {
        String nodeScriptPPath = "libs/convertAudio.js";

        System.out.println("sourcePath = " + sourcePath);
        System.out.println("destinationPath = " + destinationPath);

        String[] cmdArray = {
                "node",
                nodeScriptPPath,
                sourcePath.toString(),
                destinationPath.toString()
        };

        ProcessBuilder processBuilder = new ProcessBuilder(cmdArray);
        Process process = processBuilder.start();

        int exitCode = process.waitFor();

        if (exitCode == 0) {
            System.out.println("Node.js script executed successfully.");
        }
    }
}
