package com.example.song_finder_fx.Model;

public class IngestCSVData {

    private String albumTitle;
    private String upc;
    private String catalogNumber;
    private String primaryArtist;
    private String featuringArtist;
    private String releaseData;
    private String mainGenre;
    private String label;
    private String clineYear;
    private String clineName;
    private String plineYear;
    private String plineName;
    private String recordingYear;
    private String recordingLocation;
    private String albumFormat;
    private String trackTitle;
    private String isrc;
    private String trackPrimaryArtist;
    private String composer;
    private String lyricist;
    private String Writers;
    private String Publishers;
    private String originalFileName;
    private Payee payee = new Payee();

    public Payee getPayee() {
        return payee;
    }

    public void setPayee(Payee payee) {
        this.payee = payee;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public String getCatalogNumber() {
        return catalogNumber;
    }

    public void setCatalogNumber(String catalogNumber) {
        this.catalogNumber = catalogNumber;
    }

    public String getPrimaryArtist() {
        return primaryArtist;
    }

    public void setPrimaryArtist(String primaryArtist) {
        this.primaryArtist = primaryArtist;
    }

    public String getFeaturingArtist() {
        return featuringArtist;
    }

    public void setFeaturingArtist(String featuringArtist) {
        this.featuringArtist = featuringArtist;
    }

    public String getReleaseData() {
        return releaseData;
    }

    public void setReleaseData(String releaseData) {
        this.releaseData = releaseData;
    }

    public String getMainGenre() {
        return mainGenre;
    }

    public void setMainGenre(String mainGenre) {
        this.mainGenre = mainGenre;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getClineYear() {
        return clineYear;
    }

    public void setClineYear(String clineYear) {
        this.clineYear = clineYear;
    }

    public String getClineName() {
        return clineName;
    }

    public void setClineName(String clineName) {
        this.clineName = clineName;
    }

    public String getPlineYear() {
        return plineYear;
    }

    public void setPlineYear(String plineYear) {
        this.plineYear = plineYear;
    }

    public String getPlineName() {
        return plineName;
    }

    public void setPlineName(String plineName) {
        this.plineName = plineName;
    }

    public String getRecordingYear() {
        return recordingYear;
    }

    public void setRecordingYear(String recordingYear) {
        this.recordingYear = recordingYear;
    }

    public String getRecordingLocation() {
        return recordingLocation;
    }

    public void setRecordingLocation(String recordingLocation) {
        this.recordingLocation = recordingLocation;
    }

    public String getAlbumFormat() {
        return albumFormat;
    }

    public void setAlbumFormat(String albumFormat) {
        this.albumFormat = albumFormat;
    }

    public String getTrackTitle() {
        return trackTitle;
    }

    public void setTrackTitle(String trackTitle) {
        this.trackTitle = trackTitle;
    }

    public String getIsrc() {
        return isrc;
    }

    public void setIsrc(String isrc) {
        this.isrc = isrc;
    }

    public String getTrackPrimaryArtist() {
        return trackPrimaryArtist;
    }

    public void setTrackPrimaryArtist(String trackPrimaryArtist) {
        this.trackPrimaryArtist = trackPrimaryArtist;
    }

    public String getComposer() {
        return composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public String getLyricist() {
        return lyricist;
    }

    public void setLyricist(String lyricist) {
        this.lyricist = lyricist;
    }

    public String getWriters() {
        return Writers;
    }

    public void setWriters(String writers) {
        Writers = writers;
    }

    public String getPublishers() {
        return Publishers;
    }

    public void setPublishers(String publishers) {
        Publishers = publishers;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }
}
