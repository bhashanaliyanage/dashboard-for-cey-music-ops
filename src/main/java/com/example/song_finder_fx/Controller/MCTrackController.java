package com.example.song_finder_fx.Controller;

import com.example.song_finder_fx.ControllerManualClaims;
import com.example.song_finder_fx.DatabasePostgres;
import com.example.song_finder_fx.Model.ManualClaimTrack;

import java.sql.SQLException;
import java.util.List;

public class MCTrackController {
    private final ManualClaimTrack track;

    String trackTitle;

    String composer;

    String lyricist;

    public MCTrackController(ManualClaimTrack track) {
        this.track = track;
        trackTitle = track.getTrackName();
        composer = track.getComposer();
        lyricist = track.getLyricist();
    }

    public ManualClaimTrack getTrack() {
        return track;
    }

    public ManualClaimTrack fetchArtwork() throws SQLException {
        // TODO: Changes made, please test
        DatabasePostgres.getClaimArtwork(track);
        return track;
    }

    public boolean validateArtists() {
        List<String> ceyMusicArtists = ControllerManualClaims.ceyMusicArtists;

        if (ceyMusicArtists != null) {
            if (!ceyMusicArtists.isEmpty()) {
                if (ceyMusicArtists.contains(composer)) return true;
                else return ceyMusicArtists.contains(lyricist);
            }
        }
        return false;
    }
}
