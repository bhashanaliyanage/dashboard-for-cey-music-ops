package com.example.song_finder_fx.Organizer;

import java.time.LocalDate;

public class MCTrackNew {
    public String getUpc() {
        return upc;
    }

    public String getTrackTitle() {
        return trackTitle;
    }

    public String getIsrc() {
        return isrc;
    }

    public String getComposer() {
        return composer;
    }

    public String getLyricist() {
        return lyricist;
    }

    public String getFileName() {
        return fileName;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getYoutubeID() {
        return youtubeID;
    }

    public String getTrimStart() {
        return trimStart;
    }

    public String getTrimEnd() {
        return trimEnd;
    }

    public String getLocation() {
        return location;
    }

    private String upc;
    private String catNo;
    private String trackTitle;
    private String isrc;
    private String composer;
    private String lyricist;
    private String fileName;
    private LocalDate date;
    private String youtubeID;
    private String trimStart;
    private String trimEnd;
    private String location;

    public MCTrackNew(String upc, String catNo, String trackTitle, String isrc, String composer, String lyricist, String fileName, LocalDate date, String youtubeID, String trimStart, String trimEnd, String location) {
        this.upc = upc;
        this.catNo = catNo;
        this.trackTitle = trackTitle;
        this.isrc = isrc;
        this.composer = composer;
        this.lyricist = lyricist;
        this.fileName = fileName;
        this.date = date;
        this.youtubeID = youtubeID;
        this.trimStart = trimStart;
        this.trimEnd = trimEnd;
        this.location = location;
    }
}
