package com.example.song_finder_fx.Controller;

import com.example.song_finder_fx.DatabasePostgres;
import com.example.song_finder_fx.Model.VideoDetails;
import com.example.song_finder_fx.Model.YoutubeData;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.JsonNode;


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

    //Get urlList from database  Type 2 Urls
    //GET TV CHANNEL PROGRAM LIST
 public static void main(String[] args) {
     List<List<Map<String, String>>> result = new ArrayList<>();
     List<YoutubeData> list = new ArrayList<>();
     List<String> urlList = new ArrayList<>();
        list = getUrlList();
     List<String> lst = new ArrayList<>();
     for (YoutubeData yd : list) {
         urlList = Collections.singletonList(yd.getUrl());
//         System.out.println(result);
         getviData1(urlList);

     }
 }

 //GET YOUTUBE CHANNEL NOTIFICATION


    public static List<YoutubeData> getUrlList(){
        DatabasePostgres db = new DatabasePostgres();
        List<YoutubeData> list = new ArrayList<>();
        list =  db.getUrlList();

        return list;

    }

    public static List<List<Map<String, String>>> getviData1(List<String> list) {
        List<List<Map<String, String>>> result = new ArrayList<>();

        for (String lst : list) {
            List<Map<String, String>> maplist = new ArrayList<>();
            List<VideoDetails> vdList = getYou(lst);

            for (VideoDetails vd : vdList) {
                Map<String, String> map = new HashMap<>();
                map.put("Title", vd.getTitle());
                map.put("Url", vd.getUrl());
                map.put("Thumbnail", vd.getThumbnail());
                map.put("releaseDate", vd.getReleaseDate());
                map.put("channelName", vd.getChannalName());

                maplist.add(map);
            }

            result.add(maplist);
        }

        return result;
    }

    public static List<VideoDetails> getYou(String name) {
//		 String playlistUrl = "https://www.youtube.com/playlist?list=PLxlWBAWnGBcdKoHbqfALcO0mHvfMpzNF4";
        String playlistUrl = name;
//		 String playlistUrl = "https://www.youtube.com/@LakaiSikai/videos";

        List<VideoDetails> vList = new ArrayList<VideoDetails>();

        List<VideoDetails> playlistVideos = getPlaylistVideos(playlistUrl);

        for (VideoDetails video : playlistVideos) {
            int count = 0;
            VideoDetails vd = new VideoDetails();

            System.out.println("Title: " + video.getTitle());
            System.out.println("URL: " + video.getUrl());
            System.out.println("Thumbnail: " + video.getThumbnail());
            System.out.println("video" + video.getReleaseDate());
            System.out.println(name);
            video.setChannalName(name);
//            System.out.println();
            vList.add(video);
        }
        return vList;
    }

    public static List<VideoDetails> getPlaylistVideos(String playlistUrl) {
        List<VideoDetails> videoList = new ArrayList<>();
        try {
            String jsonResponse = getResponseFromUrl(playlistUrl);
            String cleanedJson = extractJsonData(jsonResponse);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(cleanedJson);

            JsonNode contents = rootNode.path("contents").path("twoColumnBrowseResultsRenderer").path("tabs").get(0)
                    .path("tabRenderer").path("content").path("sectionListRenderer").path("contents").get(0)
                    .path("itemSectionRenderer").path("contents").get(0).path("playlistVideoListRenderer")
                    .path("contents");

            if (contents.isArray()) {
                int count = 0;
                for (JsonNode videoNode : contents) {
                    if (count >= 10)
                        break;
                    JsonNode videoRenderer = videoNode.path("playlistVideoRenderer");

                    String title = videoRenderer.path("title").path("runs").get(0).path("text")
                            .asText("Title not found");
                    String videoId = videoRenderer.path("videoId").asText("Video ID not found");
                    String thumbnailUrl = videoRenderer.path("thumbnail").path("thumbnails").get(0).path("url")
                            .asText("Thumbnail not found");
                    // get date
//					String releaseDate = videoRenderer.path("publishedTimeText").path("simpleText")
//							.asText("Release date not found");
//					String s = cropdata(title);
                    String s = extractDate(title);
//                    System.out.println(s + " this is returning data");
//					String releaseDate = videoRenderer.path("publishedTimeText").path("simpleText")
//							.asText("Release date not found");
                    VideoDetails video = new VideoDetails();

                    video.setTitle(title);
                    video.setUrl("https://www.youtube.com/watch?v=" + videoId);
                    video.setThumbnail(thumbnailUrl);
                    // new
                    video.setReleaseDate(s);
//					video.setChannalName(playlistUrl);

                    videoList.add(video);
                    count++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error: " + e.getMessage());
        }
        return videoList;
    }


    private static String getResponseFromUrl(String urlString) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

//        System.out.println(response.toString());
        return response.toString();
    }

    private static String extractJsonData(String response) {
        int startIndex = response.indexOf("var ytInitialData = ") + "var ytInitialData = ".length();
        int endIndex = response.indexOf(";</script>", startIndex);

        if (startIndex >= 0 && endIndex >= startIndex) {
            return response.substring(startIndex, endIndex);
        }
        return "{}";
    }

    public static String extractDate(String input) {

        String regex = "\\d{4}\\s?-\\s?\\d{2}\\s?-\\s?\\d{2}";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(input);

        if (((Matcher) matcher).find()) {
            return matcher.group(0).replaceAll("\\s", "");
        } else {
            return "no date found";
        }
    }

}
