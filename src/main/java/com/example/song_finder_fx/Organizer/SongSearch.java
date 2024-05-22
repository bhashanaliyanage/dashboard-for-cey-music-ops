package com.example.song_finder_fx.Organizer;

import com.example.song_finder_fx.Constants.SearchType;
import com.example.song_finder_fx.Model.Songs;

import java.io.File;
import java.util.List;

public class SongSearch {
    private final DatabaseHandler databaseHandler;

    public SongSearch() {
        databaseHandler = new DatabaseHandler();
    }

    public List<Songs> searchSong(String criteria, String searchType) {
        List<Songs> searchResults = null;

        // Check the search type and perform the appropriate search query
        switch (searchType) {
            case SearchType.SONG_NAME:
                // System.out.println("SongSearch.searchSong");
                searchResults = databaseHandler.searchSongsByTitle(criteria);
                break;
            case SearchType.ISRC:
                searchResults = databaseHandler.searchSongsByISRC(criteria);
                break;
            case SearchType.COMPOSER:
                searchResults = databaseHandler.searchSongsByComposer(criteria);
                break;
            case SearchType.SINGER:
                searchResults = databaseHandler.searchSongsBySinger(criteria);
                break;
            case SearchType.LYRICIST:
                searchResults = databaseHandler.searchSongsByLyricist(criteria);
                break;
            case SearchType.PRODUCT_NAME:
                searchResults = databaseHandler.searchSongsByProductName(criteria);
                break;
            default:
                System.out.println("Invalid search type");
        }

        return searchResults;
    }

    public void addSongToDatabase(Songs song) {

    }

    public void bulkAddSongsFromCSV(File csv) {

    }
}
