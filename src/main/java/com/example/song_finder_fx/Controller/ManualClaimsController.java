package com.example.song_finder_fx.Controller;

import com.example.song_finder_fx.Organizer.MCTrackNew;

import java.util.ArrayList;

public class ManualClaimsController {
    private final ArrayList<MCTrackNew> claims;

    public ManualClaimsController(ArrayList<MCTrackNew> claims) {
        this.claims = claims;
    }

    public ArrayList<MCTrackNew> getClaims() {
        return claims;
    }
}
