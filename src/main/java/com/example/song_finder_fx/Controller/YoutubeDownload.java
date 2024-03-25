package com.example.song_finder_fx.Controller;

import java.io.IOException;

public class YoutubeDownload {

    public static void downloadAudio(String url, String fileLocation, String fileName) {
        // System.out.println("YoutubeDownload.downloadAudio");
        String file = fileLocation + "\\" + fileName;
        // String file = "C:\\Users\\bhash\\Documents\\Test\\TestIngestFolders" + "\\" + fileName;
        // System.out.println("fileName = " + fileName);
        try {
            // System.out.println("file = " + file);
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
                System.out.println("Error executing downloader. Exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


    }


    public static void main(String[] args) {
        String filePath = "C:\\Users\\bhash\\Documents\\Test\\TestIngestFolders\\123\\WfgaHNFEFyk.flac";
        String outputFilePath = "C:\\Users\\bhash\\Documents\\Test\\TestIngestFolders\\123\\test1cut.flac";
        String result = "fail";
        String startTime = "00:01:00";
        String endTime = "00:01:26";

        // System.out.println("method in11");

        try {
            cutAudio(filePath, outputFilePath, startTime, endTime);
            result = "done";
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("result = " + result);
    }

    public static String cutAudio(String filePath, String outputPath, String startTime, String EndTime) {
        String status = "done";

        try {
            String nodeScriptPPath = "src/main/resources/com/example/song_finder_fx/libs/cutAud.js";
            System.out.println("filePath = " + filePath);
            System.out.println("outputPath = " + outputPath);
            System.out.println("startTime = " + startTime);

            ProcessBuilder processBuilder = new ProcessBuilder("node", nodeScriptPPath, filePath, outputPath, startTime, EndTime);
            Process process = processBuilder.start();

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Node.js script executed successfully.");
            } else {
                System.out.println("Error executing audio trimmer. Exit code: " + exitCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return status;
    }


}
