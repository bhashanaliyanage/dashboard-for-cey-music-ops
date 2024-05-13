package com.example.song_finder_fx.Controller;

import com.example.song_finder_fx.DatabasePostgres;
import com.example.song_finder_fx.Model.ManualClaimTrack;

import java.sql.SQLException;

public class MCTrackController {
    private final ManualClaimTrack track;

    public MCTrackController(ManualClaimTrack track) {
        this.track = track;
    }

    public ManualClaimTrack getTrack() {
        return track;
    }

    public void checkNew() throws SQLException {
        String trackTitle = track.getTrackName();
        String composer = track.getComposer();
        String lyricist = track.getLyricist();

        boolean track = DatabasePostgres.checkTrackExists(trackTitle, composer, lyricist);
        boolean composerStatus = DatabasePostgres.searchArtistTable(composer);
        if (!composerStatus) {
            // TODO: Implement Methods
            DatabasePostgres.addTempArtist(composer);
        }
        boolean lyricistStatus = DatabasePostgres.searchArtistTable(lyricist);
        if (!lyricistStatus) {
            DatabasePostgres.addTempArtist(lyricist);
        }
    }
}
