package com.example.song_finder_fx.Controller;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.nio.file.Path;

public class YoutubeDownload {

    public static void downloadAudio(String url, String fileLocation, String fileName) {
        String file = fileLocation + "\\" + fileName;
        try {
            downloadAudioOnly(url, file);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void downloadAudioOnly(String url, String file) {
        try {

            String nodeScriptPath = "src/main/resources/com/example/song_finder_fx/libs/downloadAudio.js";

            System.out.println("url = " + url);
            System.out.println("file = " + file);

            ProcessBuilder processBuilder = new ProcessBuilder("node", nodeScriptPath, url, file);
            Process process = processBuilder.start();

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
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


    }


    public static void main(String[] args) {
        String filePath = "C:\\Users\\bhash\\AppData\\Local\\Temp\\ceymusic_dashboard_audio7170319083164155368\\YIAzSvDzwLE.flac";
        String outputFilePath = "C:\\Users\\bhash\\Documents\\Test\\TestIngestFolders\\123";
        String result = "fail";
        String startTime = "00:01:00";
        String endTime = "00:01:26";

        // System.out.println("method in11");

        try {
            trimAudio(filePath, outputFilePath, startTime, endTime);
            result = "done";
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("result = " + result);
    }

    public static void trimAudio(String filePath, String outputPath, String startTime, String EndTime) throws IOException, InterruptedException {
        String nodeScriptPPath = "src/main/resources/com/example/song_finder_fx/libs/cutAud.js";

        Platform.runLater(() -> {
            System.out.println("filePath = " + filePath);
            System.out.println("outputPath = " + outputPath);
            System.out.println("startTime = " + startTime);
            System.out.println("EndTime = " + EndTime);
        });


        ProcessBuilder processBuilder = new ProcessBuilder("node", nodeScriptPPath, filePath, outputPath, startTime, EndTime);
        Process process = processBuilder.start();

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
                        
                        Output Path: '%s'
                        
                        Trim Start: '%s'
                        
                        Trim End: '%s'
                        """, filePath, outputPath, startTime, EndTime);
                alert.setContentText(message);
                Platform.runLater(alert::showAndWait);
            });
        }
    }


    public static void convertAudio(Path sourcePath, Path destinationPath) throws IOException, InterruptedException {
        String nodeScriptPPath = "src/main/resources/com/example/song_finder_fx/libs/convertAudio.js";

        System.out.println("sourcePath = " + sourcePath);
        System.out.println("destinationPath = " + destinationPath);

        ProcessBuilder processBuilder = new ProcessBuilder("node", nodeScriptPPath, sourcePath.toString(), destinationPath.toString());
        Process process = processBuilder.start();

        int exitCode = process.waitFor();

        if (exitCode == 0) {
            System.out.println("Node.js script executed successfully.");
        }
    }
}
