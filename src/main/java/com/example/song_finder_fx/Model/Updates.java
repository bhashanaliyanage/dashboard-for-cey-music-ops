package com.example.song_finder_fx.Model;

public class Updates {

    private final double version;
    private final String location;
    private final String details;

    public Updates(double version, String location, String details) {
        this.version = version;
        this.location = location;
        this.details = details;
    }

    public double getVersion() {
        return version;
    }

    public String getLocation() {
        return location;
    }

    public String getDetails() {
        return details;
    }
}
