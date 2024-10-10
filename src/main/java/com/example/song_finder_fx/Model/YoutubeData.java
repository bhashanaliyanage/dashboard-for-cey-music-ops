package com.example.song_finder_fx.Model;

public class YoutubeData {
    private String url;
    private String name;
    private int type;


    public int getType() {
        return type;
    }

    /**
     * Sets the type of the YouTube entity.
     *
     * @param type An integer representing the type of YouTube entity:
     *             1 for YouTube channels
     *             2 for YouTube playlists
     */
    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
