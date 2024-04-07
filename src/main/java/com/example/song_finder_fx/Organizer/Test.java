package com.example.song_finder_fx.Organizer;

import com.example.song_finder_fx.Constants.SearchType;
import com.example.song_finder_fx.Model.Songs;

import java.util.List;

public class Test {
    public static void main(String[] args) {
        DatabaseHandler databaseHandler = new DatabaseHandler();
        SongSearch songSearch = new SongSearch(databaseHandler);

        List<Songs> songs = songSearch.searchSong("Mawathe", SearchType.SONG_NAME);

        for (Songs song : songs) {
            System.out.println(song.getTrackTitle());
        }
    }
}
