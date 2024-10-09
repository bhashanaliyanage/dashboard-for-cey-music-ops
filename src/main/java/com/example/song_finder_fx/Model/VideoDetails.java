package com.example.song_finder_fx.Model;

import java.util.List;

public class VideoDetails {

    private String Title;
    private String Url;
    private String Thumbnail;
    private String releaseDate;
    private List<VideoDetails> list;
    private String channalName;
    private List<String> stList;

    private String videoID;
    private String lable;

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

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
        if (Thumbnail == null) {
            try {
                String videoID = Url.substring(32);
                Thumbnail = "https://i.ytimg.com/vi/" + videoID + "/maxresdefault.jpg";
                return Thumbnail;
            } catch (Exception e) {
                return null;
            }
        } else {
            return Thumbnail;
        }
    }


    public String getVideoID() {
        return videoID;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
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
