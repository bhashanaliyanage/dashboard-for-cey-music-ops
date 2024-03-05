package com.example.song_finder_fx.Controller;

import java.io.IOException;

public class YoutubeDownload {


    public static void main(String[] args) {
        System.out.println("test done");
//        String url = "https://www.youtube.com/watch?v=Z8X_1R1s4as";
        String url = "https://www.youtube.com/watch?v=m5Lgc1upeOw";

        String file = "test22";
        dwnloadAudio(url, file);
    }

    public static void dwnloadAudio(String url, String file) {
        String s = "done";
        System.out.println("audio method");
        String fileLo = "F:\\FTP\\Downloads";
        String file1 = fileLo + file;
        try {
            System.out.println("file locaton+" + file1);
            downloadAudioOnly(url, file1);
            System.out.println("inside method");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void downloadAudioOnly(String url, String file) {

        System.out.println(url);
        System.out.println(file);
        String s = "test";

        try {

            String nodeScriptPath = "src/main/resources/com/example/song_finder_fx/libs/downloadAudio.js";
//            String nodeScriptPath = "D:\\Sudesh\\JavaScript Project\\nn/downloadAudio.js";
//            String nodeScriptPath = "D:\\code/downloadAudio.js";

//			String videoURL = "https://www.youtube.com/watch?v=O5O3yK8DJCc";
            String videoURL = url;
//			String filePath = "C:\\Users\\Public\\Documents\\downFile\\test3.mp4";

//			String encodedFile = URLEncoder.encode(file, "UTF-8");
            String filePath = file;
            System.out.println(url + "url of downld");
            System.out.println(file + " file location");
            System.out.println(nodeScriptPath + "node path");

            ProcessBuilder processBuilder = new ProcessBuilder("node", nodeScriptPath, videoURL, filePath);
            Process process = processBuilder.start();

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Node.js script executed successfully.");
            } else {
                System.out.println("Error executing Node.js script. Exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


    }

}
