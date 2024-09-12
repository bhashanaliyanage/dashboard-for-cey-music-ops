package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.YoutubeDownload;
import javafx.fxml.FXML;

import java.util.List;
import java.util.Map;

public class ControllerYouTubeMonitoring {
    @FXML
    void initialize() {
        listChannels();
    }

    // For Testing Purposes
    public static void main(String[] args) {
        listChannels();
    }

    private static void listChannels() {
        // Sample Pseudo Code
        /*
        list<YouTubeChannel> youTubeChannelList = Database.getYouTubeChannelList();

        for (YouTubeChannel channel : youTubeChannelList) {
            int channelType = channel.getType();

            if (channelType == 1) {
                String playlistID = channel.getPlaylistID();
                channel.appendVideo(fetchFromPlaylist(playlistID));
            } else if (channelType == 2) {
                String channelID = channel.getChannelID();
                channel.appendVideo(fetchFromChannelID(channelID));
            }
        }

        for (YouTubeChannel channel : youTubeChannelList) {
            list<YouTubeVideo> youTubeVideoList = channel.getVideoList();

            for (YouTubeVideo video : youTubeVideoList) {
                //  Show in UI
            }
        }
        */

        List<List<Map<String, String>>> list = YoutubeDownload.getTypeTvProgramLlist();

        for (List<Map<String, String>> list1 : list) {
            for (Map<String, String> map : list1) {
                String title = map.get("Title");
                String url = map.get("Url");
                String thumbnail = map.get("Thumbnail");
                String releaseDate = map.get("releaseDate");
                String channelName = map.get("channelName");
                System.out.println("\n");
                System.out.println(title + "\n" + url + "\n" + thumbnail + "\n" + releaseDate + "\n" + channelName);
            }
        }
    }
}
