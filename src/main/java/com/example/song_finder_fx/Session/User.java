package com.example.song_finder_fx.Session;

public class User {
    private int id;
    private int privilegeLevel;
    private String email;
    private String nickName;

    public int getPrivilegeLevel() {
        return privilegeLevel;
    }

    public String getEmail() {
        return email;
    }

    public void setPrivilegeLevel(int privilegeLevel) {
        this.privilegeLevel = privilegeLevel;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getUserID() {
        return id;
    }

    public void setUserID(int userID) {
        this.id = userID;
    }
}
