package com.example.song_finder_fx.Model;

import com.example.song_finder_fx.ItemSwitcher;

import java.sql.ResultSet;

public class Revenue {

    private ResultSet top5Territories;
    private ResultSet top4DSPs;
    private String assetCount;
    private ResultSet top5StreamedAssets;
    private String month;

    public void setValues(ResultSet top5Territories, ResultSet top4DSPs, String assetCount, ResultSet top5StreamedAssets, String month) {
        this.top5Territories = top5Territories;
        this.top4DSPs = top4DSPs;
        this.assetCount = assetCount;
        this.top5StreamedAssets = top5StreamedAssets;
        this.month = ItemSwitcher.setMonth(month);
    }

    public ResultSet getTop5Territories() {
        return top5Territories;
    }

    public ResultSet getTop4DSPs() {
        return top4DSPs;
    }

    public String getAssetCount() {
        return assetCount;
    }

    public ResultSet getTop5StreamedAssets() {
        return top5StreamedAssets;
    }

    public String getMonth() {
        return month;
    }
}
