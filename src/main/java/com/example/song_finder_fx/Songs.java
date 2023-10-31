package com.example.song_finder_fx;

import java.util.ArrayList;
import java.util.List;

public class Songs {
    private String isrc;
    private String albumTitle;
    private String upc;
    private String songName;
    private String singer;
    private String featuringArtist;
    private String composer;
    private String lyricist;
    private String fileName;
    private String primaryArtist;
    private String albumFormat;
    private String trackVersion;

    public Songs() {
    }

    public Songs(String songName, String isrc, String singer) {
        this.songName = songName;
        this.isrc = isrc;
        this.singer = singer;
    }

    public void songDetails(String isrc, String albumTitle, String upc, String trackTitle, String singer, String featuringArtist, String Composer, String lyricist, String fileName) {
        this.isrc = isrc;
        this.albumTitle = albumTitle;
        this.upc = upc;
        songName = trackTitle;
        this.singer = singer;
        this.featuringArtist = featuringArtist;
        this.composer = Composer;
        this.lyricist = lyricist;
        this.fileName = fileName;
    }

    public List<String> getSongDetails() {
        List<String> songDetails = new ArrayList<>();

        songDetails.add(isrc);
        songDetails.add(albumTitle);
        songDetails.add(upc);
        songDetails.add(songName);
        songDetails.add(singer);
        songDetails.add(featuringArtist);
        songDetails.add(composer);
        songDetails.add(lyricist);
        songDetails.add(fileName);

        return songDetails;
    }

    public String getSongName() {
        return songName;
    }

    public String getISRC() {
        return isrc;
    }

    public String getSinger() {
        return singer;
    }
}
