package com.example.song_finder_fx.Model;

import org.checkerframework.checker.index.qual.EnsuresLTLengthOf;

import java.util.List;

public class VideoDetails {

    private String Title;
    private String Url;
    private String Thumbnail;
    private String releaseDate;
    private List<VideoDetails> list;
    private String channalName;
    private List<String> stList;


    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        Thumbnail = thumbnail;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public List<VideoDetails> getList() {
        return list;
    }

    public void setList(List<VideoDetails> list) {
        this.list = list;
    }

    public String getChannalName() {
        return channalName;
    }

    public void setChannalName(String channalName) {
        this.channalName = channalName;
    }

    public List<String> getStList() {
        return stList;
    }

    public void setStList(List<String> stList) {
        this.stList = stList;
    }
}
