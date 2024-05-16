package com.example.song_finder_fx.Model;

import java.util.Objects;

public class CoWriterShare {

    private String isrc;
    private String payee;
    private String share;
    private String songType;
    private String artistName;
    private double royalty;
    private String songName;
    private String contributor;
    private String composer;
    private String lyricist;

    public double getRoyalty() {
        return royalty;
    }

    public String getSongName() {
        return songName;
    }

    public String getContributor() {
        return contributor;
    }

    public String getComposer() {
        return composer;
    }

    public String getLyricist() {
        return lyricist;
    }

    public String getIsrc() {
        return isrc;
    }

    public void setIsrc(String isrc) {
        this.isrc = isrc;
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public String getShare() {
        return share;
    }

    public void setShare(String share) {
        this.share = share;
    }

    public String getSongType() {
        if (Objects.equals(songType, "C")) {
            return "PUB";
        } else if (Objects.equals(songType, "O")) {
            return "SR";
        }
        return "";
    }

    public void setSongType(String songType) {
        this.songType = songType;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setRoyalty(double royalty) {
        this.royalty = royalty;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public void setLyricist(String lyricist) {
        this.lyricist = lyricist;
    }
}
