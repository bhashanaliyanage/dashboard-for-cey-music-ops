package com.example.song_finder_fx;

import javafx.scene.image.Image;

public class ItemSwitcher {
    public ItemSwitcher() {
    }

    public String setCountry(String countryCode) {
        return switch (countryCode) {
            case "AU" -> "Australia";
            case "KR" -> "South Korea";
            case "US" -> "United State";
            case "IT" -> "Italy";
            case "GB" -> "United Kingdom";
            default -> countryCode;
        };
    }

    public Image setImage(String dsp) {
        Image defaultImage = new Image("com/example/song_finder_fx/images/logo_small_200x.png");

        Image youtube = new Image("com/example/song_finder_fx/images/yt.png");
        Image spotify = new Image("com/example/song_finder_fx/images/spotify.png");
        Image itunes = new Image("com/example/song_finder_fx/images/itunes.png");
        Image fb = new Image("com/example/song_finder_fx/images/fb.png");

        return switch (dsp) {
            case "Youtube Ad Supported", "Youtube Music" -> youtube;
            case "Spotify" -> spotify;
            case "Apple Music" -> itunes;
            case "Facebook" -> fb;
            default -> defaultImage;
        };
    }
}
