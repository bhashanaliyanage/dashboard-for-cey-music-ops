package com.example.song_finder_fx.Model;

import com.example.song_finder_fx.DatabaseMySQL;
import com.example.song_finder_fx.Main;

import java.sql.SQLException;
import java.util.Objects;

public class Songs {
    private String isrc = "";
    private String trackTitle = "";
    private String singer = "";
    private String composer = "";
    private String lyricist = "";
    private String productTitle = "";
    private String upc = "";
    private String featuringArtist = "";
    private String percentage = "";
    private String copyrightOwner = "";

    public void setTrackTitle(String trackTitle) {
        this.trackTitle = trackTitle;
    }

    public Songs() {
    }

    public void setSongDetails(String isrcFromDatabase, String albumTitle, String upc, String trackTitle, String singer,
                               String featuringArtist, String composer, String lyricist) {
        isrc = isrcFromDatabase;
        this.productTitle = albumTitle;
        this.upc = upc;
        this.trackTitle = trackTitle;
        this.singer = singer;
        this.featuringArtist = featuringArtist;
        this.composer = composer;
        this.lyricist = lyricist;

    }

    public Songs(String songName, String isrc, String singer) {
        this.trackTitle = songName;
        this.isrc = isrc;
        this.singer = singer;
    }

    public Songs(String songName, String isrc, String singer, String composer, String lyricist) {
        this.trackTitle = songName;
        this.isrc = isrc;
        this.singer = singer;
        this.composer = composer;
        this.lyricist = lyricist;
    }

    public void setIsrc(String isrc) {
        this.isrc = isrc;
    }

    public String getTrackTitle() {
        return trackTitle;
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

    public boolean composerAndLyricistCeyMusic() throws SQLException, ClassNotFoundException {
        boolean composerCeyMusic = DatabaseMySQL.searchArtistTable(composer);
        boolean lyricistCeyMusic = DatabaseMySQL.searchArtistTable(lyricist);

        return composerCeyMusic && lyricistCeyMusic;
    }

    public boolean composerOrLyricistCeyMusic() throws SQLException, ClassNotFoundException {
        boolean composerCeyMusic = DatabaseMySQL.searchArtistTable(composer);
        boolean lyricistCeyMusic = DatabaseMySQL.searchArtistTable(lyricist);

        return composerCeyMusic || lyricistCeyMusic;
    }

    public boolean composerCeyMusic() throws SQLException, ClassNotFoundException {
        return DatabaseMySQL.searchArtistTable(composer);
    }

    public String getProductName() {
        return productTitle;
    }

    public String getUPC() {
        return upc;
    }

    public String getFeaturing() {
        return featuringArtist;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public void setCopyrightOwner(String copyrightOwner) {
        this.copyrightOwner = copyrightOwner;
    }

    public String getControl() {
        return percentage;
    }

    public String getCopyrightOwner() {
        return copyrightOwner;
    }

    public boolean isOriginal() {
        return !Objects.equals(singer, "");
    }

    public boolean isInList() {
        boolean isIsrcPresent = false;
        String targetIsrc = isrc.trim().replaceAll("\\p{C}", "");


        for (String isrc2 : Main.getSongList()) {
            String trimmedSong = isrc2.trim().replaceAll("\\p{C}", "");

            if (trimmedSong.equalsIgnoreCase(targetIsrc)) {
                isIsrcPresent = true;
                break;
            }
        }

        return isIsrcPresent;
    }
}
