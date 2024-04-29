package com.example.song_finder_fx.Controller;

import com.example.song_finder_fx.DatabasePostgres;
import com.example.song_finder_fx.Model.Artist;

import java.sql.SQLException;

public class ArtistController {
    private final Artist artist;

    public ArtistController(Artist artist) {
        this.artist = artist;
    }

    public Artist getArtist() {
        return artist;
    }

    public String fetchArtistName() throws SQLException {
        return DatabasePostgres.getArtistName(artist.getID());
    }
}
