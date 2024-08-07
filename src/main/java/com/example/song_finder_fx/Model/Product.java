package com.example.song_finder_fx.Model;

public class Product {

    private final String upc;

    private final String albumTitle;

    private final String catalogNumber;

    private final String releaseDate;

    public Product(String upc, String albumTitle, String catalogNumber, String releaseDate) {
        this.upc = upc;
        this.albumTitle = albumTitle;
        this.catalogNumber = catalogNumber;
        this.releaseDate = releaseDate;
    }

    public String getUpc() {
        return upc;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public String getCatalogNumber() {
        return catalogNumber;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

}
