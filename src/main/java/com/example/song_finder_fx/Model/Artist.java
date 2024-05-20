package com.example.song_finder_fx.Model;

public class Artist {
    private final int artistID;

    private String artistName;

    public Artist(int artistID, String artistName) {
        this.artistID = artistID;
        this.artistName = artistName;
    }

    public int getID() {
        return artistID;
    }

    public String getName() {
        return artistName;
    }

    public void setName(String name) {
        artistName = name;
    }
}
