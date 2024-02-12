package com.example.song_finder_fx.Model;

import java.util.ArrayList;
import java.util.List;

public class SongList {
    List<SongDetails> songList = new ArrayList<>();

    public void setSongList(List<SongDetails> songList) {
        this.songList = songList;
    }

    public List<SongDetails> getSongList() {
        return songList;
    }
}
