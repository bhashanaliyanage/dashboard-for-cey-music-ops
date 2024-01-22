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
            case "AE" -> "United Arab Emirates";
            default -> countryCode;
        };
    }

    public Image setImage(String dsp) {
        Image defaultImage = new Image("com/example/song_finder_fx/images/logo_small_200x.png");

        Image youtube = new Image("com/example/song_finder_fx/images/yt.png");
        Image ytmusic = new Image("com/example/song_finder_fx/images/ytmusic.png");
        Image spotify = new Image("com/example/song_finder_fx/images/spotify.png");
        Image itunes = new Image("com/example/song_finder_fx/images/itunes.png");
        Image fb = new Image("com/example/song_finder_fx/images/fb.png");
        Image tiktok = new Image("com/example/song_finder_fx/images/tiktok.png");

        return switch (dsp) {
            case "Youtube Ad Supported" -> youtube;
            case "Youtube Music" -> ytmusic;
            case "Spotify" -> spotify;
            case "Apple Music" -> itunes;
            case "Facebook", "Facebook Audio Library", "Facebook Fingerprinting" -> fb;
            case "TikTok" -> tiktok;

            default -> defaultImage;
        };
    }

    public String setMonth(String month) {
        return switch (month) {
            case "01" -> "January";
            case "02" -> "February";
            case "03" -> "March";
            case "04" -> "April";
            case "05" -> "May";
            case "06" -> "June";
            case "07" -> "July";
            case "08" -> "August";
            case "09" -> "September";
            case "10" -> "October";
            case "11" -> "November";
            case "12" -> "December";
            default -> month;
        };
    }
}
