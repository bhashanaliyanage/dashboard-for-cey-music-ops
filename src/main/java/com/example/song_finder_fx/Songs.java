package com.example.song_finder_fx;

public class Songs {
    private String isrc;
    private String albumTitle;
    private int upc;
    private String catNo;
    private String primaryArtist;
    private String albumFormat;
    private String songName;
    private String trackVersion;
    private String singer;
    private String featuringArtist;
    private String composer;
    private String lyricist;
    private String fileName;

    public Songs(String songName, String isrc) {
        this.songName = songName;
        this.isrc = isrc;
    }

    public void songDetails(String isrc,
                            String albumTitle,
                            int upc,
                            String trackTitle,
                            String singer,
                            String featuringArtist,
                            String Composer,
                            String lyricist,
                            String fileName) {
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

    public String getSongName() {
        return songName;
    }

    public String getISRC() {
        return isrc;
    }
}
