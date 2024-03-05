package com.example.song_finder_fx.Model;

public class ManualClaimTrack {
    private final String trackName;
    private final String lyricist;
    private final String composer;
    private final String youtubeID;

    public ManualClaimTrack(String trackName, String lyricist, String composer, String url) {
        this.trackName = trackName;
        this.lyricist = lyricist;
        this.composer = composer;
        this.youtubeID = url.substring(32);
    }

    public String getTrackName() {
        return trackName;
    }

    public String getLyricist() {
        return lyricist;
    }

    public String getComposer() {
        return composer;
    }

    public String getYoutubeID() {
        return youtubeID;
    }
}
