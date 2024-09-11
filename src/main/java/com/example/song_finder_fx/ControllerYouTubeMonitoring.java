package com.example.song_finder_fx;

import javafx.fxml.FXML;

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
    }
}
