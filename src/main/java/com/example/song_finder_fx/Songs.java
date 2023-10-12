package com.example.song_finder_fx;

public class Songs {
    private String songName;
    private String isrc;

    public Songs(String songName, String isrc) {
        this.songName = songName;
        this.isrc = isrc;
    }

    public String getSongName() {
        return songName;
    }

    public String getISRC() {
        return isrc;
    }
}
