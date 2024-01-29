package com.example.song_finder_fx.Model;

public class Songs {
    private String isrc;
    private String songName;
    private String singer;
    private String composer;
    private String lyricist;

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public Songs() {
    }

    public Songs(String songName, String isrc, String singer) {
        this.songName = songName;
        this.isrc = isrc;
        this.singer = singer;
    }

    public Songs(String songName, String isrc, String singer, String composer, String lyricist) {
        this.songName = songName;
        this.isrc = isrc;
        this.singer = singer;
        this.composer = composer;
        this.lyricist = lyricist;
    }

    public void setIsrc(String isrc) {
        this.isrc = isrc;
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

    public String getComposer() {
        return composer;
    }

    public String getLyricist() {
        return lyricist;
    }

    public void setSinger(String text) {
        this.singer = text;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public void setLyricist(String lyricist) {
        this.lyricist = lyricist;
    }
}
