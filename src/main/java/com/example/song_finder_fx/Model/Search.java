package com.example.song_finder_fx.Model;

import com.example.song_finder_fx.DatabaseMySQL;

import java.sql.SQLException;
import java.util.List;

public class Search {
    private String searchType = "TRACK_TITLE";
    private Songs song = new Songs();

    public void setType(String searchType) {
        this.searchType = searchType;
    }

    public String getSearchType() {
        return searchType;
    }

    public List<Songs> search(String text) throws SQLException, ClassNotFoundException {
        return DatabaseMySQL.searchSongDetailsBySearchType(text, searchType);
    }
}
