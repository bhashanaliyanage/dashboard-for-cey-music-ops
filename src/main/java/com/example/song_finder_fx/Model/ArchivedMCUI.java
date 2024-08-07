package com.example.song_finder_fx.Model;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ArchivedMCUI {
    private Label lblSongNo;
    private Label lblClaimType;
    private CheckBox checkBox;
    private HBox hboxEntry;
    private ManualClaimTrack track;

    public ArchivedMCUI(Label lblSongNo, Label lblClaimType, CheckBox checkBox, HBox hboxEntry) {
        this.lblSongNo = lblSongNo;
        this.lblClaimType = lblClaimType;
        this.checkBox = checkBox;
        this.hboxEntry = hboxEntry;
    }

    // Getters
    public Label getLblSongNo() {
        return lblSongNo;
    }

    public Label getLblClaimType() {
        return lblClaimType;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public HBox getHboxEntry() {
        return hboxEntry;
    }

    // Setters
    public void setLblSongNo(Label lblSongNo) {
        this.lblSongNo = lblSongNo;
    }

    public void setLblClaimType(Label lblClaimType) {
        this.lblClaimType = lblClaimType;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public void setHboxEntry(HBox hboxEntry) {
        this.hboxEntry = hboxEntry;
    }

    public void setClaim(ManualClaimTrack track) {
        this.track = track;
    }

    public ManualClaimTrack getClaim() {
        return track;
    }
}
