package com.example.song_finder_fx.Model;

import com.example.song_finder_fx.DatabasePostgres;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.time.LocalDate;

public class ManualClaimTrack {
    private final int id;
    private String trackName;
    private String lyricist;
    private String composer;
    private final String youtubeID;
    private final LocalDate date;
    private boolean status = false;
    private String trimStart;
    private String trimEnd;
    private BufferedImage image;
    private BufferedImage previewImage;
    private final int claimType;

    public ManualClaimTrack(int id, String trackName, String lyricist, String composer, String youTubeID, LocalDate date, int claimType) {
        this.id = id;
        this.trackName = trackName;
        this.lyricist = lyricist;
        this.composer = composer;
        this.youtubeID = youTubeID;
        this.date = date;
        this.claimType = claimType;
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

    public int getId() {
        return id;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getYouTubeURL() {
        return "https://www.youtube.com/watch?v=" + youtubeID;
    }

    public void addTrimTime(String trimStart, String trimEnd) {
        this.trimStart = trimStart;
        this.trimEnd = trimEnd;
    }

    public String getTrimStart() {
        return trimStart;
    }

    public String getTrimEnd() {
        return trimEnd;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public Image getImage() {
        return SwingFXUtils.toFXImage(image, null);
    }

    public void setPreviewImage(BufferedImage image) {
        this.previewImage = image;
    }

    public Image getPreviewImage() {
        if (previewImage != null) {
            return SwingFXUtils.toFXImage(previewImage, null);
        }
        return null;
    }

    public BufferedImage getBufferedImage() {
        return image;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getClaimType() {
        return claimType;
    }

    public String getClaimTypeString() {
        return switch (claimType) {
            case 2 -> "TV Programs";
            case 3 -> "Manual Claim";
            case 4 -> "Single SR";
            case 7 -> "Channel One";
            case 8 -> "Charana";
            case 9 -> "Chat Programmes";
            case 10 -> "Dawasak Da Handawaka";
            case 11 -> "Derana";
            case 12 -> "Derana 60+";
            case 13 -> "Derana Little Star";
            case 14 -> "Dream Star";
            case 15 -> "Hiru";
            case 16 -> "Hiru Star";
            case 17 -> "Imorich Tunes";
            case 18 -> "ITN";
            case 19 -> "Ma Nowana Mama";
            case 20 -> "Monara TV";
            case 21 -> "Roo Tunes";
            case 22 -> "Roopawahini";
            case 23 -> "Sirasa";
            case 24 -> "Siyatha";
            case 25 -> "Supreme TV";
            case 26 -> "Swarnawahini";
            default -> "Unspecified";
        };
    }

    public int unArchive() throws SQLException {
        return DatabasePostgres.unArchiveManualClaim(id);
    }

    public void setTrimStart(String trimStart) {
        this.trimStart = trimStart;
    }

    public void setTrimEnd(String trimEnd) {
        this.trimEnd = trimEnd;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public void setLyricist(String lyricist) {
        this.lyricist = lyricist;
    }
}
