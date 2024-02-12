package com.example.song_finder_fx.Model;

import java.util.List;



public class SongDetails {
    private String songName;
    private String Precentage;
    private String CpywriteTemp;



    public void setSongName(String songName) {
        this.songName = songName;
    }

    public void setPrecentage(String precentage) {
        Precentage = precentage;
    }

    public void setCpywriteTemp(String cpywriteTemp) {
        CpywriteTemp = cpywriteTemp;
    }

    public String getSongName() {
        return songName;
    }

    public String getPrecentage() {
        return Precentage;
    }

    public String getCpywriteTemp() {
        return CpywriteTemp;
    }


}
