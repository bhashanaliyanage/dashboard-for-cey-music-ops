package com.example.song_finder_fx.Model;

public class CsvSong {

    private String songTitle;
    private String fileName;
    private String singer;
    private String lyrics;
    private String Composer;

    private String trackPrimaryArtist;



    public CsvSong(String songTitle, String fileName, String singer, String lyrics, String composer) {
        this.songTitle = songTitle;
        this.fileName = fileName;
        this.singer = singer;
        this.lyrics = lyrics;
        Composer = composer;
    }

    public CsvSong() {

    }

    public String getTrackPrimaryArtist() {
        return trackPrimaryArtist;
    }

    public void setTrackPrimaryArtist(String trackPrimaryArtist) {
        this.trackPrimaryArtist = trackPrimaryArtist;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public String getFileName() {
        return fileName;
    }

    public String getSinger() {
        return singer;
    }

    public String getLyrics() {
        return lyrics;
    }

    public String getComposer() {
        return Composer;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public void setComposer(String composer) {
        Composer = composer;
    }
}


