package com.example.song_finder_fx.Controller;

import com.example.song_finder_fx.DatabasePostgres;
import com.example.song_finder_fx.Model.VideoDetails;
import com.example.song_finder_fx.Model.YouDownload;
import com.example.song_finder_fx.Model.YoutubeData;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.text.SimpleDateFormat;

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

    //Get urlList from database  Type 1 Urls
    //GET YOUTUBE CHANNEL LIST
 public static void main(String[] args) {
     List<List<Map<String, String>>> li = new ArrayList<List<Map<String, String>>>();

    List<YoutubeData> youList =  new ArrayList<>();
    youList = youList();
    List<String> urlList = new ArrayList<>();
    for (YoutubeData yd : youList) {
        urlList = Collections.singletonList(yd.getUrl());
        li = getvd(urlList);
        System.out.println(li);
    }

 }

 public List<List<Map<String, String>>> getProgramListByChannel(){
     List<List<Map<String, String>>> li = new ArrayList<List<Map<String, String>>>();
     List<YoutubeData> youList =  new ArrayList<>();
     youList = youList();
     List<String> urlList = new ArrayList<>();
     for (YoutubeData yd : youList) {
         urlList = Collections.singletonList(yd.getUrl());
         li = getvd(urlList);
         System.out.println(li);
     }

     return li;
 }
 public static  List<YoutubeData> youList(){
     DatabasePostgres db = new DatabasePostgres();
     List<YoutubeData> list = new ArrayList<>();
     list =  db.getUrlList1();

     return list;
 }

 //GET youtube VIDEO LIST  | GET TYPE 2 LIST | ONLY CHANNLE NAME LIST
 public static List<List<Map<String, String>>> getvd(List<String> list) {
     List<List<Map<String, String>>> li = new ArrayList<List<Map<String, String>>>();
     List<VideoDetails> vdlist = new ArrayList<VideoDetails>();

     for (String s : list) {
         List<VideoDetails> vd = new ArrayList<VideoDetails>();
         System.out.println(s);
         vd = dataList(s);
         List<Map<String, String>> maplist = new ArrayList<>();

         for (VideoDetails v : vd) {

             Map<String, String> map = new HashMap<>();
             map.put("Title", v.getTitle());
             map.put("Url", v.getUrl());
             map.put("Thumbnail", v.getThumbnail());
             map.put("releaseDate", v.getReleaseDate());
             maplist.add(map);
         }
         li.add(maplist);
     }
     return li;
 }

    public static List<VideoDetails> dataList(String list) {
        YouDownload you = new YouDownload();
        Map<String, List<Map<String, String>>> channelMap = new HashMap<>();
        List<VideoDetails> vList = new ArrayList<VideoDetails>();

        you.setUrl(list);
        List<VideoDetails> ss = new ArrayList<VideoDetails>();

        ss = getRes(you);
        System.out.println(ss + " this is ss");

        for (VideoDetails video : ss) {
            int count = 0;
            VideoDetails vd = new VideoDetails();

            System.out.println("Title: " + video.getTitle());
            System.out.println("URL: " + video.getUrl());
            System.out.println("Thumbnail: " + video.getThumbnail());
            System.out.println("video" + video.getReleaseDate());
            System.out.println();
            vList.add(video);
        }

        return vList;
    }

    public static List<VideoDetails> getRes( YouDownload you) {
        StringBuilder result = new StringBuilder();
        List<String> result1 = new ArrayList<>();
        List<VideoDetails> vd = new ArrayList<VideoDetails>();
        try {
            String urlString = you.getUrl();
            String jsonResponse = getResponseFromUrl(urlString);
            String cleanedJson = extractJsonData(jsonResponse);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(cleanedJson);

            JsonNode contents = rootNode.path("contents").path("twoColumnBrowseResultsRenderer").path("tabs").get(1)
                    .path("tabRenderer").path("content").path("richGridRenderer").path("contents");

            if (contents.isArray() && contents.size() > 0) {
                for (JsonNode videoNode : contents) {
                    JsonNode videoRenderer = videoNode.path("richItemRenderer").path("content").path("videoRenderer");

                    String publishedTimeText = videoRenderer.path("publishedTimeText").path("simpleText")
                            .asText("Field not found");

                    String url = videoRenderer.path("navigationEndpoint").path("commandMetadata")
                            .path("webCommandMetadata").path("url").asText("URL not found");

                    String YoutubeChannel = you.getUrl().substring(25);

//                    String dataOrMonth = extractSinhalaSubstring(publishedTimeText);
                    System.out.println(publishedTimeText);
                    String dataOrMonth = publishedTimeText.substring(0,9);
                    byte[] bytes1 = dataOrMonth.getBytes(StandardCharsets.UTF_8);
                    System.out.println(dataOrMonth+"data or month");
//                    String dataOrMonth = new String(publishedTimeText.substring(0, 2).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
//                    System.out.println("Extracted: " + dataOrMonth);
//                  //test area
//                    System.out.println("Full string: " + publishedTimeText);
//                    System.out.println("Length: " + publishedTimeText.length());
//                    for (int i = 0; i < publishedTimeText.length(); i++) {
//                        System.out.println("Character at " + i + ": " + publishedTimeText.charAt(i) + " (Unicode: " + (int)publishedTimeText.charAt(i) + ")");
//                    }



//                    byte[] bytes = publishedTimeText.getBytes(StandardCharsets.UTF_8);
//                    byte[] bytes = {-61, -96, -62, -74, -62, -81, -61, -96, -62, -73, -30, -128, -103, -61, -96, -62, -74, -62, -79, 32, 51,};
                    byte[] bytes = {-61, -96, -62, -74, -62, -81, -61, -96, -62, -73, -30, -128, -103, -61, -96, -62, -74, -62, -79,};
                    System.out.println("Bytes: " + java.util.Arrays.toString(bytes));
                    String reconstructed = new String(bytes, StandardCharsets.UTF_8);
                    System.out.println("Reconstructed: " + reconstructed);




                    String datenumber = "";
                    Pattern pattern = Pattern.compile("\\d+");
                    Matcher matcher = pattern.matcher(publishedTimeText);
                    System.out.println(dataOrMonth+"date or month");

                    // Check if a number is found and extract it
                    if (matcher.find()) {
                         datenumber = matcher.group();  // Extract the first number found
                        System.out.println(datenumber + " - Extracted number");
                    } else {
                        System.out.println("No number found in the text.");
                    }

                    VideoDetails vd1 = new VideoDetails();

//                    if (dataOrMonth.equals("දින") || dataOrMonth.equals("පැය")) {
                        if (Arrays.equals(bytes, bytes1)) {
                        System.out.println("This will return");

                        Date uploadDate = calculateUploadDate(publishedTimeText);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String formattedUploadDate = (uploadDate != null) ? sdf.format(uploadDate)
                                : "Unable to calculate";

                        result.append("publishedTimeText: ").append(publishedTimeText).append(", ");
                        result.append("URL: https://www.youtube.com").append(url).append(", ");
                        result.append("YouTube Channel: ").append(YoutubeChannel).append(", ");
//						result.append("Calculated Upload Date: ").append(formattedUploadDate).append(" ");
                        result.append("Calculated Upload Date1: ").append(dateCalulator(datenumber)).append(" ");
                        vd1.setUrl("https://www.youtube.com" + url);
//						vd1.setReleaseDate(formattedUploadDate);
                        vd1.setTitle(YoutubeChannel);
                        vd1.setReleaseDate(dateCalulator(datenumber));
//						vd1.set
//						result1.add(result.toString());
                        vd.add(vd1);
                    } else {
                        System.out.println("This will return another");
                    }

                    System.out.println(dataOrMonth);

                }
            } else {
                result.append("No videos found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.append("Error: ").append(e.getMessage());
        }
        return vd;
    }

    private static String extractSinhalaSubstring(String dt) {
        if (dt.length() >= 2) {
            return dt.substring(1, 5);
        } else {
            return dt;
        }
    }

    public static String dateCalulator(String dt) {
        int i = Integer.parseInt(dt);
        LocalDate lcdate = LocalDate.now().minusDays(i);
        return lcdate.toString();
    }


    private static Date calculateUploadDate(String publishedTimeText) {
        Calendar cal = Calendar.getInstance();
        try {
            if (publishedTimeText.contains("hour") || publishedTimeText.contains("minute")) {
                return cal.getTime(); // Today
            } else if (publishedTimeText.contains("day")) {
                int days = Integer.parseInt(publishedTimeText.split(" ")[0]);
                cal.add(Calendar.DAY_OF_YEAR, -days);
            } else if (publishedTimeText.contains("week")) {
                int weeks = Integer.parseInt(publishedTimeText.split(" ")[0]);
                cal.add(Calendar.WEEK_OF_YEAR, -weeks);
            } else if (publishedTimeText.contains("month")) {
                int months = Integer.parseInt(publishedTimeText.split(" ")[0]);
                cal.add(Calendar.MONTH, -months);
            } else if (publishedTimeText.contains("year")) {
                int years = Integer.parseInt(publishedTimeText.split(" ")[0]);
                cal.add(Calendar.YEAR, -years);
            } else {
                return null; // Unable to parse
            }
            return cal.getTime();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }


    //GET YOUTUBE TV PROGRAM LIST| GET TYPE 2 LIST
    public static List<List<Map<String, String>>> getTypeTvProgramLlist() {
        List<List<Map<String, String>>> result = new ArrayList<>();
        List<YoutubeData> list = new ArrayList<>();
        List<String> urlList = new ArrayList<>();
        list = getUrlList();
        List<String> lst = new ArrayList<>();
        for (YoutubeData yd : list) {
            urlList = Collections.singletonList(yd.getUrl());
//         System.out.println(result);
          result =   getviData1(urlList);

        }
        return result;
    }

 //GET YOUTUBE CHANNEL url list from database

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
