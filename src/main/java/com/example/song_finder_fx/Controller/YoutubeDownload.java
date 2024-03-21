package com.example.song_finder_fx.Controller;

import com.mysql.cj.x.protobuf.MysqlxPrepare;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;

public class YoutubeDownload {

    public static void downloadAudio(String url, String fileLocation, String fileName) {
        System.out.println("YoutubeDownload.downloadAudio");
        String file = fileLocation + fileName;
        try {
            System.out.println("file = " + file);
            downloadAudioOnly(url, file);
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


    public static void main(String[] args) {
        String filePath = "C:\\Users\\Public\\Documents\\downFile\\test";
        String outputFilePath = "C:\\Users\\Public\\Documents\\downFile\\test1cut.mp3";
        String result = "fail";
        String  startTime = "00:01:00";
        String endTime = "00:01:26";
        System.out.println("method in11");
        try {
            cutAudio(filePath,outputFilePath,startTime,endTime);
            result = "done";
            System.out.println(result);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static String cutAudio(String filePath,String outputPath,String startTime,String EndTime){
        String s = "done";
//        String startTime =
        try {

            String nodeScriptPPath = "D:\\code/cutAud.js";
            System.out.println(filePath+" File path111111");
            System.out.println(outputPath+" out path11111111");
            System.out.println(startTime+" time2222");
//            String nodeScriptPPath = "D:\\code/cutAud.js";

            ProcessBuilder processBuilder =  new ProcessBuilder("node",nodeScriptPPath,filePath,outputPath,startTime,EndTime);
            Process process = processBuilder.start();

            int exitCode =  process.waitFor();


            if (exitCode == 0) {
                System.out.println("Node.js script executed successfully.");
            } else {
                System.out.println("Error executing Node.js script. Exit code: " + exitCode+" Check selected File Name is correct??");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return  s;
    }





}
