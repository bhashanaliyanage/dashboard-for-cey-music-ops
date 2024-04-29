package com.example.song_finder_fx.Model;

public class Artist {
    private final int artistID;

    private String artistName;

    public Artist(int artistID) {
        this.artistID = artistID;
        artistName = null;
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
